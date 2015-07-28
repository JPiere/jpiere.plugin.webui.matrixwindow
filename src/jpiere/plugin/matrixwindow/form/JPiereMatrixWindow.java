/******************************************************************************
 * Product: JPiere(Japan + iDempiere)                                         *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 *                                                                            *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY.                          *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * JPiere supported by OSS ERP Solutions Co., Ltd.                            *
 * (http://www.oss-erp.co.jp)                                                 *
 *****************************************************************************/
package jpiere.plugin.matrixwindow.form;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

import jpiere.plugin.matrixwindow.model.MMatrixField;
import jpiere.plugin.matrixwindow.model.MMatrixSearch;
import jpiere.plugin.matrixwindow.model.MMatrixWindow;

import org.adempiere.base.IModelFactory;
import org.adempiere.base.Service;
import org.adempiere.webui.adwindow.ADWindow;
import org.adempiere.webui.adwindow.ADWindowContent;
import org.adempiere.webui.adwindow.GridTabRowRenderer;
import org.adempiere.webui.adwindow.GridView;
import org.adempiere.webui.adwindow.IADTabbox;
import org.adempiere.webui.adwindow.IADTabpanel;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Column;
import org.adempiere.webui.component.Columns;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.EditorBox;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.NumberBox;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.editor.IZoomableEditor;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WEditorPopupMenu;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.editor.WebEditorFactory;
import org.adempiere.webui.event.ContextMenuListener;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.window.FDialog;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.I_AD_Column;
import org.compiere.model.MColumn;
import org.compiere.model.MField;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MTab;
import org.compiere.model.MTable;
import org.compiere.model.PO;
import org.compiere.model.SystemIDs;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.TrxRunnable;
import org.zkoss.zk.au.out.AuFocus;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Center;
import org.zkoss.zul.Frozen;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.North;
import org.zkoss.zul.Space;
import org.zkoss.zul.impl.XulElement;

/**
 * JPiereMatrixWindow
 *
 * JPIERE-0098
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class JPiereMatrixWindow extends AbstractMatrixWindowForm implements EventListener<Event>, ValueChangeListener,WTableModelListener{

	/**	Logger			*/
	public static CLogger log = CLogger.getCLogger(JPiereMatrixWindow.class);

	private CustomForm form = new CustomForm();


	/**********************************************************************
	 * UI Component
	 **********************************************************************/

	private Borderlayout mainLayout = new Borderlayout();

	private Panel parameterPanel = new Panel();						//Set field of search condition
	private Grid parameterLayout = GridFactory.newGridLayout();

	//Display Data
	private Panel displayDataPanel = new Panel();
	private Borderlayout displayDataLayout = new Borderlayout();

	Grid matrixGrid  = new Grid();			//main component

	private Button SearchButton;

	private Button SaveButton;

	private Button CreateButton;


	/**********************************************************************
	 * Variable for management
	 **********************************************************************/

	private boolean     m_calculating = false;

	PO[] m_POs;

	StringBuilder message = new StringBuilder();


	//View Model:Map of Data Model for Display<Identifier of Row.<Column Number,data>>
	TreeMap<Object,TreeMap<Integer,Object>> viewModel = new TreeMap<Object,TreeMap<Integer,Object>>() ;

	//Convetion Table:Connect View Model with Table Modle<Identifier of Row.<Column Number,Identifier of Data>>
	TreeMap<Object,TreeMap<Integer,Object>> conversionTable = new TreeMap<Object,TreeMap<Integer,Object>> ();

	//Map of PO Instance that corresponding to Table.<ID,PO>
	HashMap<Integer,PO> 				tableModel = new HashMap<Integer,PO>();

	//Map of PO Instance that have to save.<POのID,PO>
	HashMap<Integer,PO> 				dirtyModel  = new HashMap<Integer,PO>();

	//画面表示のためにキーカラム毎にモデルクラスのインスタンスを区分管理しているMAP
	//TreeMap<縦軸となるカラムの識別子,<横軸となるrowの識別子,PO>>
	TreeMap<Object,TreeMap<Object,PO>> keyColumnModel = new TreeMap<Object,TreeMap<Object,PO>>();


	/*Information of key of Vertical axis and key of horizontal axis*/
	//リンクカラムと縦軸のキーと横軸のキーの３つでユニーク制約をつけておく前提です。
	//Columnキー(Columnの軸となるキー情報)のリスト
	ArrayList<Object> columnKeys = new ArrayList<Object>();
	 //Columnキーと名称のマップ
	HashMap<Object,String> columnKeyNameMap = new HashMap<Object,String>();

	//Rowキー(Rowの軸となるキー情報)のリスト
	ArrayList<Object> rowKeys = new ArrayList<Object>();


	//全部(固定カラム+変動カラム)のカラムの名称のマップ<カラム順番,カラム名称>
	HashMap<Integer,String> columnNameMap = new HashMap<Integer,String> ();
	//全部(固定カラム+変動カラム)のカラムの表示長さのマップ<カラム順番,長さ>
	HashMap<Integer,Integer> columnLengthMap = new HashMap<Integer,Integer> ();
	//全部(固定カラム+変動カラム)のカラムのGridFieldのインスタンスのマップ<カラム順番,GridField>
	HashMap<Integer,GridField> columnGridFieldMap = new HashMap<Integer,GridField> ();

	//表領域に表示する固定項目のMAP<表示順番,項目(カラム)名> ※現在の仕様として、Rowの識別子となるカラムは1行に固定。
	TreeMap<Integer, String> fixItem = new TreeMap<Integer, String>();//TODO：SQLでパラメータ取得できるようにする。

	//固定項目(カラム)のAD_Field_IDのマップ<表示順番,AD_Field_ID>
	TreeMap<Integer,Integer> fixItemFieldIDMap = new TreeMap<Integer,Integer>();


	private String whereClause ;

	private JPMatrixGridRowRenderer renderer;

	private String columnOnClick;


	/**********************************************************************
	 * Parameter of Application Dictionary(System Client)
	 **********************************************************************/

	//Model
	MMatrixWindow 	m_matrixWindow;
	MTab			m_Tab;
	MMatrixField[]  m_matrixFields ;
	MField[]		m_contentFields;
	MColumn[]		m_contentColumns;
	MMatrixSearch[] m_matrixSearches ;

	//Search Field Editor Map
	HashMap<String,WEditor> searchEditorMap = new HashMap<String,WEditor> ();

	//Column Key Column
	I_AD_Column m_columnKeyColumn;

	//Row Key Column
	I_AD_Column m_rowKeyColumn ;

	//AD_Window_ID
	private int AD_WINDOW_ID = 0;

	//Table Name
	private String TABLE_NAME ;


	private int FIX_ITEM_FIELD_ID = 0;


	/****************************************************
	 * Window Info
	 ****************************************************/

	private CustomForm window = new CustomForm();
	private ADWindow adWindow;
	private ADWindowContent adWindowContent;
	private IADTabpanel adTabpanel;
	private GridTab gridTab ;
	private GridView gridView ;
	private GridField[] gridFields ;


	/**
	 * Constractor
	 *
	 * @throws IOException
	 */
    public JPiereMatrixWindow() throws IOException
    {
    	;
    }

	public ADForm getForm()
	{
		return form;
	}

	public void createMatrixWindow(String Value)
	{
    	try
		{
    		prepare(Value);
			zkInit();
		}
		catch(Exception e)
		{
			log.log(Level.SEVERE, "", e);
		}

	}

	public void prepare(String Value) throws Exception
	{
		//マトリクスウィドウのモデルクラス群を作成
		m_matrixWindow = MMatrixWindow.get(Env.getCtx(), Value);
		if(m_matrixWindow == null)
		{
			;//TODO エラー処理
		}

		m_matrixFields = m_matrixWindow.getMatrixFields();
		m_contentFields = m_matrixWindow.getContentFields();
		m_matrixSearches = m_matrixWindow.getMatrixSearches();
		m_contentColumns = new MColumn[m_contentFields.length];
		for(int i = 0; i < m_contentFields.length; i++)
		{
			m_contentColumns[i]=new MColumn(Env.getCtx(),m_contentFields[i].getAD_Column_ID(),null);
		}


		AD_WINDOW_ID = m_matrixWindow.getAD_Window_ID();
		m_Tab = new MTab(Env.getCtx(), m_matrixWindow.getAD_Tab_ID(), null);
		TABLE_NAME = MTable.get(Env.getCtx(), m_Tab.getAD_Table_ID()).getTableName();


		m_columnKeyColumn = m_matrixWindow.getJP_MatrixColumnKey().getAD_Column();
		m_rowKeyColumn = m_matrixWindow.getJP_MatrixRowKey().getAD_Column();

		FIX_ITEM_FIELD_ID = m_matrixWindow.getJP_MatrixRowKey().getAD_Field_ID();

		/*表領域に表示する項目<表示順番,項目(カラム)名>を取得する*/
		//fixItemはRowの識別子となるカラムの1行で固定
		fixItem.put(0, m_rowKeyColumn.getColumnName());
		fixItemFieldIDMap.put(0,FIX_ITEM_FIELD_ID);


		//Windowの情報を使うので、画面上には表示させませんが、ウィンドウを作成します。
		if(adWindow==null)
		{
			adWindow = new ADWindow(Env.getCtx(),AD_WINDOW_ID, null);
			adWindow.createPart(window);
		}
		adWindowContent = adWindow.getADWindowContent();
		IADTabbox adTabbox = adWindowContent.getADTab();
		int tabCount = adTabbox.getTabCount();
		for(int i = 0; i < tabCount; i++)
		{
			if(adTabbox.getADTabpanel(i).getTableName().equals(m_Tab.getAD_Table().getTableName()))
			{
				adTabpanel =adTabbox.getADTabpanel(i);
			}
		}
		if(adTabpanel == null)
		{
			//TODO:エラー処理
		}
		gridTab = adTabpanel.getGridTab();
		gridView = adTabpanel.getGridView();
		gridFields = gridTab.getFields();
	}


	private void zkInit() throws Exception
	{
		form.appendChild(mainLayout);

		/*【メインレイアウト(Borderlayout)】*/
		mainLayout.setWidth("99%");
		mainLayout.setHeight("100%");

		//【メインレイアウト(Borderlayout)-北】
		North north = new North();
		mainLayout.appendChild(north);



		//Search Parameter Panel
		north.appendChild(parameterPanel);
		north.setStyle("border: none");
		parameterPanel.appendChild(parameterLayout); 		//parameterLayout = Grid
		parameterLayout.setWidth("90%");
		Rows parameterLayoutRows = parameterLayout.newRows();
		Row row = parameterLayoutRows.newRow();
			Groupbox searchGB = new Groupbox();
			row.appendCellChild(searchGB,8);
			searchGB.appendChild(new Caption(Msg.getMsg(Env.getCtx(), "SearchCriteria")));
			Grid searchGrid  = new Grid();
			searchGrid.setStyle("background-color: #E9F0FF");
			searchGrid.setStyle("border: none");
			searchGB.appendChild(searchGrid);
			Rows rows = searchGrid.newRows();

			//Search Fields
			for(int i = 0; i < m_matrixSearches.length; i++)
			{
				if(i%2 == 0)
				{
					row = rows.newRow();
					row.setStyle("background-color: #ffffff");
				}

				for(int j = 0; j < gridFields.length; j++)
				{
					if(m_matrixSearches[i].getAD_Field_ID() == gridFields[j].getAD_Field_ID())
					{
						WEditor editor = WebEditorFactory.getEditor(gridFields[j], false);

						//Set zoom
						if(editor instanceof WSearchEditor
								|| editor instanceof WTableDirEditor)
						{
							editor.getLabel().addEventListener(Events.ON_CLICK, new ZoomListener((IZoomableEditor) editor));
							if(m_matrixSearches[i].isMandatory())
								editor.getLabel().setStyle("cursor: pointer; text-decoration: underline;color: #333; color:red;");
							else
								editor.getLabel().setStyle("cursor: pointer; text-decoration: underline;color: #333;");
						}

						editor.setMandatory(m_matrixSearches[i].isMandatory());

						row.appendCellChild(editor.getLabel().rightAlign());
						row.appendCellChild(editor.getComponent(),2);

						WEditorPopupMenu popupMenu = editor.getPopupMenu();
			            if (popupMenu != null)
			            {
			            	popupMenu.addMenuListener((ContextMenuListener)editor);
			            	row.appendChild(popupMenu);

			            	popupMenu.addContextElement((XulElement) editor.getComponent());
			            }

						editor.addValueChangeListener(this);
						searchEditorMap.put(editor.getColumnName(), editor);
						break;
					}
				}//for j
			}//for i

		//Button
		row = parameterLayoutRows.newRow();
				SearchButton = new Button(Msg.getMsg(Env.getCtx(), "search"));
				SearchButton.setId("SearchButton");
				SearchButton.addActionListener(this);
				SearchButton.setEnabled(true);
				SearchButton.setImage(ThemeManager.getThemeResource("images/Find16.png"));
				row.appendCellChild(SearchButton);

				SaveButton = new Button(Msg.getMsg(Env.getCtx(), "save"));
				SaveButton.setId("SaveButton");
				SaveButton.addActionListener(this);
				SaveButton.setEnabled(false);
				SaveButton.setImage(ThemeManager.getThemeResource("images/Save16.png"));
				row.appendCellChild(SaveButton);

				CreateButton = new Button(Msg.getMsg(Env.getCtx(), "NewRecord"));
				CreateButton.setId("CreateButton");
				CreateButton.addActionListener(this);
				CreateButton.setEnabled(false);
				CreateButton.setImage(ThemeManager.getThemeResource("images/New16.png"));
				if(m_matrixWindow.getJP_QuickEntryWindow_ID() > 0)
				{

					row.appendCellChild(CreateButton);
				}

		//for space under Button
		row = parameterLayoutRows.newRow();
				row.appendCellChild(new Space(),1);

		Center center = new Center();
		mainLayout.appendChild(center);
		center.appendChild(displayDataPanel);
		displayDataPanel.appendChild(displayDataLayout);//Borderlayout
		displayDataPanel.setWidth("100%");
		displayDataPanel.setHeight("100%");
		displayDataPanel.setHflex("1");
		displayDataPanel.setVflex("1");
		displayDataLayout.setWidth("100%");
		displayDataLayout.setHeight("100%");
		displayDataLayout.setStyle("border: none");

				//Contents
				center = new Center();
				displayDataLayout.appendChild(center);
				center.appendChild(matrixGrid);
				center.setStyle("border: none");
				matrixGrid.setWidth("100%");
				matrixGrid.setHeight("100%");
				matrixGrid.setVflex(true);

				matrixGrid.setMold("paging");
				matrixGrid.setPageSize(m_matrixWindow.getJP_PageSize());
	}

	static class ZoomListener implements EventListener<Event> {

		private IZoomableEditor searchEditor;

		ZoomListener(IZoomableEditor editor) {
			searchEditor = editor;
		}

		public void onEvent(Event event) throws Exception {
			if (Events.ON_CLICK.equals(event.getName())) {
				searchEditor.actionZoom();
			}

		}

	}

	@Override
	public void tableChanged(WTableModelEvent e) {
		int row = e.getFirstRow();
		int col = e.getColumn();
		ListModel<?> listModel = e.getModel();

		updateModel(listModel,row, col);

		m_calculating = false;

	}

	private void updateModel(ListModel<?> listmodel ,int row, int col)
	{

		if (m_calculating)  //  Avoid recursive calls
			return ;

		if(col < fixItemFieldIDMap.size())
		{
			m_calculating = false;
			return;

		}else{

			Map.Entry<?,?> mapEntry = (Map.Entry<?,?>)listmodel.getElementAt(row);
			Object mapKey = mapEntry.getKey();
			TreeMap<?,?> mapValue = (TreeMap<?,?>)mapEntry.getValue();
			Object value = mapValue.get(col);
			Object poID = conversionTable.get(mapKey).get(col);
			PO po = tableModel.get(poID);
//			String columnName = columnNameMap.get(col);
			String columnName = columnGridFieldMap.get(col).getColumnName();
			po.set_ValueNoCheck(columnName, value);
			dirtyModel.put(po.get_ID(), po);


			m_calculating = true;
			return;
		}
	}


//	@Override
	public void valueChange(ValueChangeEvent e)
	{
		searchEditorMap.get(e.getPropertyName()).setValue(e.getNewValue());

		SearchButton.setEnabled(true);
		SaveButton.setEnabled(false);
		CreateButton.setEnabled(false);

		quickEntry = null;

		matrixGrid.setVisible(false);

		if(e.getNewValue()==null && searchEditorMap.get(e.getPropertyName()).isMandatory())
		{
			searchEditorMap.get(e.getPropertyName()).getLabel().setStyle("cursor: pointer; text-decoration: underline;color: #333; color:red;");
		}else{
			searchEditorMap.get(e.getPropertyName()).getLabel().setStyle("cursor: pointer; text-decoration: underline;color: #333; ");
		}
	}

	JPiereMatrixWindowQuickEntry quickEntry = null;

	@Override
	public void onEvent(Event e) throws Exception {

		message = new StringBuilder();

		if (e == null)
		{
			return;
		}
		else if (e.getTarget() == matrixGrid && Events.ON_CLICK.equals(e.getName()))
		{
			Object data = e.getData();
			org.zkoss.zul.Row row = null;
			String columnName = null;
			if (data != null && data instanceof Component)
			{
				if (data instanceof org.zkoss.zul.Row)
					row = (org.zkoss.zul.Row) data;
				else
				{
					AbstractComponent cmp = (AbstractComponent) data;
					if (cmp.getParent() instanceof org.zkoss.zul.Row)
					{
						row = (Row) cmp.getParent();
						columnName = (String) cmp.getAttribute("columnName");
					}
				}
			}
			if (row != null)
			{
				//click on selected row to enter edit mode
				if (row == renderer.getCurrentRow())
				{
					if (!renderer.isEditing())
					{
						renderer.editCurrentRow();
						if (columnName != null && columnName.trim().length() > 0)
							setFocusToField(columnName);
						else
							renderer.focusToFirstEditor();
					}
				}
				else
				{
					int index = matrixGrid.getRows().getChildren().indexOf(row);
					if (index >= 0 ) {
						columnOnClick = columnName;
						onSelectedRowChange(index);
					}
				}
			}
			e.stopPropagation();
        }
//		else if (e.getTarget() == paging)
//		{
//			int pgNo = paging.getActivePage();
//			if (pgNo != listModel.getPage())
//			{
//				listModel.setPage(pgNo);
//				onSelectedRowChange(0);
//				gridTab.clearSelection();
//				Clients.resize(listbox);
//			}
//		}
//		else if (e.getTarget() == selectAll)
//		{
//			toggleSelectionForAll(selectAll.isChecked());
//		}
		else if (e.getName().equals("onSelectRow"))
		{
			Checkbox checkbox = (Checkbox) e.getData();
			int rowIndex = (Integer) checkbox.getAttribute(GridTabRowRenderer.GRID_ROW_INDEX_ATTR);
			if (checkbox.isChecked())
			{
//				gridTab.addToSelection(rowIndex);
//				if (!selectAll.isChecked() && isAllSelected())
//				{
//					selectAll.setChecked(true);
//				}
			}
			else
			{
//				gridTab.removeFromSelection(rowIndex);
//				if (selectAll.isChecked())
//					selectAll.setChecked(false);
			}

		/*JPiereMatrixWindowQuickEntry#ConfirmPanel*/
		}else if(e.getName().equals(ConfirmPanel.A_CANCEL)){

			if(!createView ())
			{
				SearchButton.setEnabled(true);
				SaveButton.setEnabled(false);
				CreateButton.setEnabled(false);
				matrixGrid.setVisible(false);
				throw new Exception(message.toString());
			}

			SearchButton.setEnabled(false);
			SaveButton.setEnabled(true);
			CreateButton.setEnabled(true);

			quickEntry = null;

		/*JPiereMatrixWindowQuickEntry#ConfirmPanel*/
		}else if(e.getName().equals(ConfirmPanel.A_OK)){ //Keep on creating new record

			Events.sendEvent(Events.ON_CLICK, CreateButton, null);

		}else if (e.getTarget().equals(SearchButton) || e.getName().equals("onComplete")){

			if(!createView ())
			{
				SearchButton.setEnabled(true);
				SaveButton.setEnabled(false);
				CreateButton.setEnabled(true);
				matrixGrid.setVisible(false);
				throw new Exception(message.toString());
			}


			SearchButton.setEnabled(false);
			SaveButton.setEnabled(true);
			CreateButton.setEnabled(true);

		}else if(e.getTarget().equals(SaveButton)){

			matrixGrid.focus();
			boolean isOK = saveData();

			if(isOK)
			{
				dirtyModel.clear();

				if(!createView ())
				{
					matrixGrid.setVisible(false);
					throw new Exception(message.toString());
				}

			}else{
				;//Nothing to do
			}

		}else if(e.getTarget().equals(CreateButton)){

			Object old_rowKeyColumn_Value = null;
			Object old_columnKeyColumn_Value = null;
			if(quickEntry == null){

				saveData();

			}else{
				List<WEditor> editors = quickEntry.getQuickEditors();

				for(WEditor editor : editors)
				{
					if(editor.getColumnName().equals(m_rowKeyColumn.getColumnName()))
					{
						old_rowKeyColumn_Value = editor.getValue();
					}else if(editor.getColumnName().equals(m_columnKeyColumn.getColumnName())){
						old_columnKeyColumn_Value = editor.getValue();
					}
				}
			}

			quickEntry = new JPiereMatrixWindowQuickEntry (form.getWindowNo(), m_matrixWindow.getJP_QuickEntryWindow_ID(), this);
			quickEntry.loadRecord (0);
			List<WEditor> editors = quickEntry.getQuickEditors();


			String  JP_QuickEntryConf = m_matrixWindow.getJP_QuickEntryConf();
			for(WEditor editor : editors)
			{
				for(Map.Entry<String, WEditor> entry: searchEditorMap.entrySet())//検索パラメータを新規登録データの初期値として設定し変更不可とする
				{
					if(editor.getColumnName().equals(entry.getKey()))
					{
						editor.setValue(entry.getValue().getValue());
						if(entry.getValue().getValue() == null)
							editor.setReadWrite(true);
						else
							editor.setReadWrite(false);
					}
				}//for

				//Set Column key info or Row key info or Both;
				if(JP_QuickEntryConf != null)
				{

					if(JP_QuickEntryConf.equals(MMatrixWindow.JP_QUICKENTRYCONF_ColumnInfoOnly))
					{
						if(editor.getColumnName().equals(m_rowKeyColumn.getColumnName()))
							editor.setValue(old_rowKeyColumn_Value);

					}else if(JP_QuickEntryConf.equals(MMatrixWindow.JP_QUICKENTRYCONF_RowInfoOnly)){

						if(editor.getColumnName().equals(m_columnKeyColumn.getColumnName()))
							editor.setValue(old_columnKeyColumn_Value);

					}else if(JP_QuickEntryConf.equals(MMatrixWindow.JP_QUICKENTRYCONF_ColumnAndRowInfo)){

						if(editor.getColumnName().equals(m_rowKeyColumn.getColumnName()))
							editor.setValue(old_rowKeyColumn_Value);
						else if(editor.getColumnName().equals(m_columnKeyColumn.getColumnName()))
							editor.setValue(old_columnKeyColumn_Value);
					}
				}


			}//for

			AEnv.showWindow(quickEntry);
		}

	}

	Auxhead auxhead ;

	private boolean createView () throws Exception {

		matrixGrid.setVisible(true);

		whereClause = createWhere();
		if(message.length() > 0)
			return false;

		columnKeys = createColumnKeys(whereClause);
		if(columnKeys.size()==0)
		{
			message.append(System.getProperty("line.separator") + Msg.getMsg(Env.getCtx(), "not.found"));
			return false;
		}

		rowKeys = createRowKeys(whereClause);
		if(rowKeys.size()==0)
		{
			message.append(System.getProperty("line.separator") + Msg.getMsg(Env.getCtx(), "not.found"));
			return false;
		}

		m_POs = getPOs(whereClause,true);
		if(m_POs.length==0)
		{
			message.append(System.getProperty("line.separator") + Msg.getMsg(Env.getCtx(), "not.found"));
			return false;
		}

		tableModel = createTableModel(m_POs);
		keyColumnModel = createKeyColumnModel(m_POs);

		viewModel = createViewModelConvetionTable();
		conversionTable = createViewModelConvetionTable();

		setViewModelConvetionTable();


		JPListModelMapTable vmListModelMap = new JPListModelMapTable(viewModel);
		JPListModelMapTable ctListModelMap = new JPListModelMapTable(conversionTable);

		vmListModelMap.addTableModelListener(this);		//JPListModelMapTable#setDataAt()処理から、this#tableChanged()メソッドが呼び出される。
		matrixGrid.setModel(vmListModelMap);

		org.zkoss.zul.Columns columns = matrixGrid.getColumns();
		if(columns == null)
		{
			auxhead = createAuxhead();
			matrixGrid.appendChild(auxhead);

			createtColumnMap();
			Columns clms = createColumns();
			clms.setSizable(true);
			matrixGrid.appendChild(clms);

			Frozen frozen = new Frozen();
			frozen.setColumns(fixItemFieldIDMap.size());
			matrixGrid.appendChild(frozen);
		}else{
			matrixGrid.removeChild(columns);
			matrixGrid.removeChild(auxhead);
			auxhead = createAuxhead();
			matrixGrid.appendChild(auxhead);

			createtColumnMap();
			Columns clms = createColumns();
			clms.setSizable(true);
			matrixGrid.appendChild(clms);
		}


		renderer = new JPMatrixGridRowRenderer(vmListModelMap,ctListModelMap,tableModel,dirtyModel, form.getWindowNo(),form,this);
		renderer.setcColumnsSize(columnNameMap.size());					//TODO:コンストラクタの引数とする
		renderer.gridView = gridView;									//TODO:コンストラクタの引数とする
		renderer.gridTab = gridTab;										//TODO:コンストラクタの引数とする
		renderer.columnGridFieldMap = columnGridFieldMap;				//TODO:コンストラクタの引数とする
		matrixGrid.setRowRenderer(renderer);
		renderer.setADWindowPanel(adWindowContent,adTabpanel);

		return true;

	}


	private String createWhere()
	{
		StringBuilder whereClause = new StringBuilder(" WHERE AD_Client_ID = "+ Env.getAD_Client_ID(Env.getCtx()));

		for(Map.Entry<String, WEditor> entry: searchEditorMap.entrySet())
		{
			if(entry.getValue().getValue()!=null)
			{
				whereClause.append(" AND "+ entry.getKey() + " = " + entry.getValue().getValue());
			}else{

				if(entry.getValue().isMandatory())
				{
					message.append(System.getProperty("line.separator") + Msg.getMsg(Env.getCtx(), "FillMandatory") + entry.getValue().getLabel().getValue() );
				}
			}
		}

		return whereClause.toString();
	}


	private ArrayList<Object> createColumnKeys(String whereClause)
	{
		ArrayList<Object> list = new ArrayList<Object>();
		final String sql = "SELECT DISTINCT " + m_columnKeyColumn.getColumnName() +" FROM " + TABLE_NAME + whereClause
							+ " ORDER BY " + m_columnKeyColumn.getColumnName();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		columnKeyNameMap.clear();
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			rs = pstmt.executeQuery();
			I_AD_Column keyColumn = m_matrixWindow.getJP_MatrixColumnKey().getAD_Column();
			while (rs.next())
			{
				if(keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_TABLEDIR
						|| keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_TABLE
						|| keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_SEARCH )
				{
					list.add(rs.getInt(1));
					columnKeyNameMap.put(rs.getInt(1), getDisplayValue(m_columnKeyColumn.getAD_Column_ID(), rs.getInt(1)));
				}else if(keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_INTEGER ){
					list.add(rs.getInt(1));
				}else if(keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_STRING ){
					list.add(rs.getString(1));
				}else if( keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_DATE
						|| keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_DATETIME
						|| keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_TIME ){
					list.add(rs.getTimestamp(1));
				}else{
					list.add(rs.getObject(1));
				}
			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		return list;
	}

	private ArrayList<Object> createRowKeys(String whereClause)
	{
		ArrayList<Object> list = new ArrayList<Object>();
		final String sql = "SELECT DISTINCT " + m_rowKeyColumn.getColumnName() +" FROM " + TABLE_NAME + whereClause
							+ " ORDER BY " + m_rowKeyColumn.getColumnName();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		I_AD_Column keyRow = m_matrixWindow.getJP_MatrixRowKey().getAD_Column();
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				if(keyRow.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_TABLEDIR
						|| keyRow.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_TABLE
						|| keyRow.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_SEARCH )
				{
					list.add(rs.getInt(1));
				}else if(keyRow.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_INTEGER ){
					list.add(rs.getInt(1));
				}else if(keyRow.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_STRING ){
					list.add(rs.getString(1));
				}else if( keyRow.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_DATE
						|| keyRow.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_DATETIME
						|| keyRow.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_TIME ){
					list.add(rs.getTimestamp(1));
				}else{
					list.add(rs.getObject(1));
				}

			}
		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		return list;
	}

	public PO[] getPOs (String whereClause,boolean reload)
	{
		if (reload || m_POs == null || m_POs.length == 0)
			;
		else
			return m_POs;
		//
		ArrayList<PO> list = new ArrayList<PO>();

		final String sql = "SELECT *  FROM " + TABLE_NAME + whereClause
				+ " ORDER BY " + m_columnKeyColumn.getColumnName() + "," + m_rowKeyColumn.getColumnName();;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, null);
			rs = pstmt.executeQuery();

			List<IModelFactory> factoryList = Service.locator().list(IModelFactory.class).getServices();
			if (factoryList == null)
			{
				;//
			}
			PO po = null;
			while (rs.next())
			{
				for(IModelFactory factory : factoryList) {
					po = factory.getPO(TABLE_NAME, rs, null);//
					if (po != null)
					{
						list.add(po);
						break;
					}
				}//for
			}//while

		}
		catch (Exception e)
		{
			log.log(Level.SEVERE, sql, e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		m_POs = new PO[list.size()];
		list.toArray(m_POs);
		return m_POs;
	}	//	getLocations


	/*
	 * POのIDをキーとしたPOインスタンスのマップ
	 *
	 *
	 */
	private HashMap<Integer, PO> createTableModel(PO[] POs)
	{

		for(int i = 0; i < POs.length; i++)
		{
			tableModel.put(POs[i].get_ID(), POs[i]);
		}

		return tableModel;
	}

	/*
	 * 縦軸となるKey Column毎のPOのMAPを作成しいます。
	 * 作成もとの情報となるPOの配列は、縦軸となるKey Columnと
	 * 横軸となるKey Rowで、ソートされているのが前提の処理です。
	 *
	 * @return TreeMap<Key Column,TreeMap<Key Row,PO>>
	 */
	private TreeMap<Object,TreeMap<Object, PO>> createKeyColumnModel(PO[] POs)
	{
		Object columnKey = "";
		TreeMap<Object, PO> obj = null;	//<Rowの識別子,PO>
		keyColumnModel.clear();
		for(int i = 0; i < POs.length; i++)
		{
			if(columnKey.equals(POs[i].get_Value(m_columnKeyColumn.getColumnName())))
			{
				obj.put(POs[i].get_Value(m_rowKeyColumn.getColumnName()), POs[i]);
			}else{
				obj = new TreeMap<Object, PO> ();
				obj.put(POs[i].get_Value(m_rowKeyColumn.getColumnName()), POs[i]);
				columnKey = POs[i].get_Value(m_columnKeyColumn.getColumnName());
				keyColumnModel.put(columnKey, obj);
			}
		}

		return keyColumnModel;
	}

	/*
	 * View ModelとConvetion Tableを作成するメソッドです。
	 * View ModelとConvetion Tableは同じTreeMap構造になります。
	 * 横軸のキー情報が設定され、必要となる縦軸のカラム数が確保されます。
	 *
	 * @return TreeMap<y,TreeMap<x,object>>
	 */
	private TreeMap<Object,TreeMap<Integer,Object>> createViewModelConvetionTable()
	{

		TreeMap<Object,TreeMap<Integer,Object>> y = new TreeMap<Object,TreeMap<Integer,Object>>();

		for(int i = 0; i < rowKeys.size(); i++)
		{
			TreeMap<Integer,Object> x = new TreeMap<Integer,Object>();
			Object obj = rowKeys.get(i);
			y.put(obj,x);
			x.put(0, obj);

			int columnNum = columnKeys.size() * m_contentFields.length;
			for(int j = 1; j < columnNum+1; j++)
			{
				x.put(j, null);
			}//for j

		}//for i

		return y;

	}

	private void setViewModelConvetionTable()
	{
		//画面表示のためにキーカラム毎にモデルクラスのインスタンスを区分管理しているMAP
		//TreeMap<縦軸となるカラムの識別子,<横軸となるrowの識別子,PO>>
		Set<Object> keyColumnModelKeySet = keyColumnModel.keySet();
		int i = 0;
		for(Object keyColumn :keyColumnModelKeySet)
		{
			//TreeMap<rowの識別子,PO>
			TreeMap<Object,PO> POs = keyColumnModel.get(keyColumn);
			Set<Object>  rowKeys = POs.keySet();
			for(Object rowKey : rowKeys)
			{
				//TreeMap<カラム番号,Data>
				TreeMap<Integer,Object> vmRow = viewModel.get(POs.get(rowKey).get_Value(m_rowKeyColumn.getColumnName()));
				TreeMap<Integer,Object> ctRow = conversionTable.get(POs.get(rowKey).get_Value(m_rowKeyColumn.getColumnName()));


				/*固定カラムの処理*/
				//現在の仕様では固定カラムは１カラムに限定されており、その情報はすでにputされているので、コメントアウトしておく。
//				if(i==0)
//				{
//					for(int j = 0; j < fixItem.size(); j++)
//					{
//							vmRow.put(j, POs.get(rowKey).get_Value(fixItem.get(j)));
//							ctRow.put(j, POs.get(rowKey).get_Value(fixItem.get(j)));
//					}
//				}

				/*変動カラムの処理*/
				for(int k = 0; k < m_contentFields.length; k++)
				{
					vmRow.put(fixItemFieldIDMap.size()+i*m_contentFields.length+k, POs.get(rowKey).get_Value(m_contentColumns[k].getColumnName()));
					ctRow.put(fixItemFieldIDMap.size()+i*m_contentFields.length+k, POs.get(rowKey).get_ID());
				}

			}//for(Object rowKey : rowKeys)

			i++;//縦軸となるキーカラムの処理カウンター

		}//for(Object keyColumn :keyColumnModelKeySet)

		return ;
	}

	private Auxhead createAuxhead() throws ClassNotFoundException{//TODO

		Auxhead auxhead = new Auxhead();

		//固定カラムの部分は仕様上1行とし、空白にする。
		Auxheader fix = new Auxheader("");
		auxhead.appendChild(fix);
		fix.setColspan(fixItemFieldIDMap.size());

		I_AD_Column keyColumn = m_matrixWindow.getJP_MatrixColumnKey().getAD_Column();
		if(keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_TABLEDIR
				|| keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_TABLE
				|| keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_SEARCH )
		{
			for(int i = 0 ; i < columnKeys.size(); i++)
			{
				Auxheader auxheader = new Auxheader(columnKeyNameMap.get(columnKeys.get(i)));
				auxhead.appendChild(auxheader);
				auxheader.setColspan(m_contentFields.length);
				auxheader.setAlign("center");
			}
		}else if(keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_INTEGER ){
			;//auxheadなし
		}else if(keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_STRING ){
			for(int i = 0 ; i < columnKeys.size(); i++)
			{
				Auxheader auxheader = new Auxheader((String)columnKeys.get(i));
				auxhead.appendChild(auxheader);
				auxheader.setColspan(m_contentFields.length);
				auxheader.setAlign("center");
			}
		}else if( keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_DATE
				|| keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_DATETIME
				|| keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_TIME ){
			for(int i = 0 ; i < columnKeys.size(); i++)
			{
				Auxheader auxheader = new Auxheader(columnKeys.get(i).toString());
				auxhead.appendChild(auxheader);
				auxheader.setColspan(m_contentFields.length);
				auxheader.setAlign("center");
			}
		}else{
			;//auxheadなし
		}


		return auxhead;
	}


	private Columns createColumns()
	{

		Columns clms = new Columns();

		Column col  = null;
		for(int i = 0; i < columnNameMap.size(); i++)
		{
			col = new Column(columnNameMap.get(i));
			col.setWidth(columnLengthMap.get(i)+"px");
			col.setDraggable("false");
			clms.appendChild(col);
		}

		return clms;
	}


	private String getDisplayValue(int AD_Column_ID, int Record_ID){

		MLookup lookup = MLookupFactory.get(Env.getCtx(), form.getWindowNo(), 0, AD_Column_ID, DisplayType.Search);
		WSearchEditor keyColumn = new WSearchEditor("keyColumn", true, false, true, lookup);
		keyColumn.setValue(Record_ID);

		return keyColumn.getDisplay();
	}

	private void createtColumnMap()
	{
		int c = 0;	//	Column counter
		columnNameMap.clear();
		columnLengthMap.clear();

		//Fix Column
		for(int i = 0; i < fixItemFieldIDMap.size(); i++)
		{
			columnNameMap.put(c, Msg.getElement(Env.getCtx(), fixItem.get(i)));
			columnLengthMap.put(c,m_matrixWindow.getFieldLength());
			for(int j = 0; j < gridFields.length; j++)
			{
				if(fixItemFieldIDMap.get(i).intValue() == gridFields[j].getAD_Field_ID())
					columnGridFieldMap.put(c, gridFields[j]);
			}
			c++;
		}

		//Repetition Column
		for(int i = 0; i < columnKeys.size(); i++)
		{
			for(int j = 0; j < m_contentFields.length; j++)
			{
				columnNameMap.put(c, Msg.getElement(Env.getCtx(), m_contentColumns[j].getColumnName()));
				columnLengthMap.put(c, m_matrixFields[j].getFieldLength());
				for(int k = 0; k < gridFields.length; k++)
				{
					if(m_contentFields[j].getAD_Field_ID()==gridFields[k].getAD_Field_ID())
						columnGridFieldMap.put(c, gridFields[k]);
				}//k
				c++;
			}//j
		}//i

		return;
	}

	/**
	 * @param columnName
	 */
	public void setFocusToField(String columnName) {
		for (WEditor editor : renderer.getEditors()) {
			if (columnName.equals(editor.getColumnName())) {
				Component c = editor.getComponent();
				if (c instanceof EditorBox) {
					c = ((EditorBox)c).getTextbox();
				} else if (c instanceof NumberBox) {
					c = ((NumberBox)c).getDecimalbox();
				}
				Clients.response(new AuFocus(c));
				break;
			}
		}
	}

	private void onSelectedRowChange(int index) {
		if (updateModelIndex(index)) {
			updateListIndex();
		}
	}

	private boolean updateModelIndex(int rowIndex) {
//		if (pageSize > 0) {
//			int start = listModel.getPage() * listModel.getPageSize();
//			rowIndex = start + rowIndex;
//		}

		if (gridTab.getCurrentRow() != rowIndex) {//TODO:これは不具合の原因になりそう。girdTabではなくrenderereの方がまだ適切か!?
			gridTab.navigate(rowIndex);
			return true;
		}
		return false;
	}

	/**
	 * Update current row from model
	 */
	public void updateListIndex() {
		if (gridTab == null || !gridTab.isOpen()) return;

//		updateEmptyMessage();

		int rowIndex  = gridTab.getCurrentRow();
//		if (pageSize > 0) {
//			if (paging.getTotalSize() != gridTab.getRowCount())
//				paging.setTotalSize(gridTab.getRowCount());
//			if (paging.getPageCount() > 1 && !gridFooter.isVisible()) {
//				showPagingControl();
//			}
//			int pgIndex = rowIndex >= 0 ? rowIndex % pageSize : 0;
//			int pgNo = rowIndex >= 0 ? (rowIndex - pgIndex) / pageSize : 0;
//			if (listModel.getPage() != pgNo) {
//				listModel.setPage(pgNo);
//				if (renderer.isEditing()) {
//					renderer.stopEditing(false);
//				}
//			} else if (rowIndex == renderer.getCurrentRowIndex()){
//				if (modeless && !renderer.isEditing())
//					echoOnPostSelectedRowChanged();
//				return;
//			} else {
//				if (renderer.isEditing()) {
//					renderer.stopEditing(false);
//					int editingRow = renderer.getCurrentRowIndex();
//					if (editingRow >= 0) {
//						int editingPgIndex = editingRow % pageSize;
//						int editingPgNo = (editingRow - editingPgIndex) / pageSize;
//						if (editingPgNo == pgNo) {
//							listModel.updateComponent(renderer.getCurrentRowIndex() % pageSize);
//						}
//					}
//				}
//			}
//			if (paging.getActivePage() != pgNo) {
//				paging.setActivePage(pgNo);
//			}
//			if (paging.getPageCount() == 1) {
//				hidePagingControl();
//			} else {
//				showPagingControl();
//			}
//			if (rowIndex >= 0 && pgIndex >= 0) {
//				echoOnPostSelectedRowChanged();
//			}
//		} else {
			if (rowIndex >= 0) {
				echoOnPostSelectedRowChanged();
			}
//		}
	}

	private static final String ATTR_ON_POST_SELECTED_ROW_CHANGED = "org.adempiere.webui.adwindow.GridView.onPostSelectedRowChanged";

	/**
	 *
	 */
	protected void echoOnPostSelectedRowChanged() {
//		if (getAttribute(ATTR_ON_POST_SELECTED_ROW_CHANGED) == null) {
//			setAttribute(ATTR_ON_POST_SELECTED_ROW_CHANGED, Boolean.TRUE);
//			Events.echoEvent("onPostSelectedRowChanged", this, null);
//		}
	}

	private boolean saveData()
	{
		try
		{

			Trx.run(new TrxRunnable()
			{
				public void run(String trxName)
				{

					Collection<PO> POs = dirtyModel.values();
					for(PO po :POs)
					{
						po.saveEx(trxName);
					}

				}
			});

			return true;

		}
		catch (Exception e)
		{
			FDialog.error(form.getWindowNo(), form, "Error", e.getLocalizedMessage());
			return false;
		}finally{
			;
		}
	}   //  saveData

}
