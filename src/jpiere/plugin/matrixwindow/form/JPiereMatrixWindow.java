/******************************************************************************
 * Product: JPiere                                                            *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 *                                                                            *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY.                          *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * JPiere is maintained by OSS ERP Solutions Co., Ltd.                        *
 * (http://www.oss-erp.co.jp)                                                 *
 *****************************************************************************/
package jpiere.plugin.matrixwindow.form;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;

import org.adempiere.base.IModelFactory;
import org.adempiere.base.Service;
import org.adempiere.exceptions.DBException;
import org.adempiere.webui.AdempiereWebUI;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.adwindow.ProcessButtonPopup;
import org.adempiere.webui.adwindow.ToolbarProcessButton;
import org.adempiere.webui.apps.AEnv;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Column;
import org.adempiere.webui.component.Columns;
import org.adempiere.webui.component.ConfirmPanel;
import org.adempiere.webui.component.Grid;
import org.adempiere.webui.component.GridFactory;
import org.adempiere.webui.component.Panel;
import org.adempiere.webui.component.Row;
import org.adempiere.webui.component.Rows;
import org.adempiere.webui.editor.IZoomableEditor;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WEditorPopupMenu;
import org.adempiere.webui.editor.WSearchEditor;
import org.adempiere.webui.editor.WStringEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.editor.WYesNoEditor;
import org.adempiere.webui.editor.WebEditorFactory;
import org.adempiere.webui.event.ActionEvent;
import org.adempiere.webui.event.ActionListener;
import org.adempiere.webui.event.ContextMenuListener;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.event.WTableModelEvent;
import org.adempiere.webui.event.WTableModelListener;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.theme.ThemeManager;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.adempiere.webui.window.Dialog;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.GridWindow;
import org.compiere.model.GridWindowVO;
import org.compiere.model.I_AD_Column;
import org.compiere.model.I_AD_Field;
import org.compiere.model.MColumn;
import org.compiere.model.MField;
import org.compiere.model.MLookup;
import org.compiere.model.MLookupFactory;
import org.compiere.model.MRefTable;
import org.compiere.model.MRole;
import org.compiere.model.MTab;
import org.compiere.model.MTable;
import org.compiere.model.MToolBarButton;
import org.compiere.model.MToolBarButtonRestrict;
import org.compiere.model.PO;
import org.compiere.model.SystemIDs;
import org.compiere.model.X_AD_ToolBarButton;
import org.compiere.process.ProcessInfo;
import org.compiere.tools.FileUtil;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.TrxRunnable;
import org.compiere.util.Util;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Center;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Frozen;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Menuitem;
import org.zkoss.zul.North;
import org.zkoss.zul.impl.XulElement;

import jpiere.plugin.matrixwindow.model.MMatrixField;
import jpiere.plugin.matrixwindow.model.MMatrixSearch;
import jpiere.plugin.matrixwindow.model.MMatrixWindow;

/**
 * JPiereMatrixWindow
 *
 * JPIERE-0098
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class JPiereMatrixWindow extends AbstractMatrixWindowForm implements EventListener<Event>, ValueChangeListener,WTableModelListener,ActionListener{

	/**	Logger			*/
	private  static CLogger log = CLogger.getCLogger(JPiereMatrixWindow.class);

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

	private Grid matrixGrid  = new Grid();			//main component

	private Button SearchButton;

	private Button SaveButton;

	private Button CreateButton;

	private Button ProcessButton;

	private Button matrixExportButton;

	private Button matrixExportXLS ;


	/**********************************************************************
	 * Variable for management
	 **********************************************************************/

	private boolean     m_calculating = false;

	private PO[] m_POs;

	private StringBuilder message = new StringBuilder();


	//View Model:Map of Data Model for Display<Identifier of Row.<Column Number,data>>
	private LinkedHashMap<Object,TreeMap<Integer,Object>> viewModel = new LinkedHashMap<Object,TreeMap<Integer,Object>>() ;

	//Convertion Table:Connect View Model with Table Modle<Identifier of Row.<Column Number,Identifier of Data>>
	private LinkedHashMap<Object,TreeMap<Integer,Object>> conversionTable = new LinkedHashMap<Object,TreeMap<Integer,Object>> ();

	//Map of PO Instance that corresponding to Table.<ID of PO,PO>
	private HashMap<Integer,PO> 				tableModel = new HashMap<Integer,PO>();

	//Map of PO Instance that have to save.<ID of PO,PO>
	private HashMap<Integer,PO> 				dirtyModel  = new HashMap<Integer,PO>();

	private ArrayList<PO> notSavePO = null;


	//Create Map of PO per column of x-axis:LinkedHashMap<Key of Column info,LinkedHashMap<Key of Row info,PO>>
	private LinkedHashMap<Object,LinkedHashMap<Object,PO>> keyColumnModel = new LinkedHashMap<Object,LinkedHashMap<Object,PO>>();


	/*Information of key of Vertical axis and key of Horizontal axis*/
	/*You need to unique constraint that contains key of vertical axis and key of horizontal axis */

	//List of Column Key(Key of Column info)
	private ArrayList<Object> columnKeys = new ArrayList<Object>();
	 //Map of Column key and Column name <key column, column name>
	private HashMap<Object,String> columnKeyNameMap = new HashMap<Object,String>();

	//List of Row Key(Key of Row info)
	private ArrayList<Object> rowKeys = new ArrayList<Object>();


	//Map of All Column Name <Column order num, Column name>
	private HashMap<Integer,String> columnNameMap = new HashMap<Integer,String> ();
	//Map of All Column length <Column order num,  Column length>
	private HashMap<Integer,Integer> columnLengthMap = new HashMap<Integer,Integer> ();
	//Map of All Column GridField <Column order num,,GridField>
	private HashMap<Integer,GridField> columnGridFieldMap = new HashMap<Integer,GridField> ();
	//Map of All Column GridField <Column order num,,GridField>
	private HashMap<Integer,Boolean> columnSummarizedMap = new HashMap<Integer,Boolean> ();


	//Map of All Fix Column(Item)  <Fix Column order num, Column name>. Specification:Fix column that is identifier of row is one column only
	private TreeMap<Integer, String> fixItem = new TreeMap<Integer, String>();

	//Map of AD_Field_ID that is Fix Column(Item)  <Fix Column order num, AD_Field_ID>.
	private TreeMap<Integer,Integer> fixItemFieldIDMap = new TreeMap<Integer,Integer>();


	private String whereClause ;

	private JPMatrixGridRowRenderer renderer;


	/**********************************************************************
	 * Parameter of Application Dictionary(System Client)
	 **********************************************************************/

	//Model
	private MMatrixWindow 	m_matrixWindow;
	private MTab			m_Tab;
	private MMatrixField[]  m_matrixFields ;
	private MField[]		m_contentFields;
	private MColumn[]		m_contentColumns;
	private MMatrixSearch[] m_matrixSearches ;
	 private ArrayList<ToolbarProcessButton> toolbarProcessButtons = new ArrayList<ToolbarProcessButton>();

	private JPiereMatrixWindowQuickEntry quickEntry = null;


	public static final String EDITMODE_EDIT ="edit";
	public static final String EDITMODE_TEST ="test";
	public static final String EDITMODE_READ ="read";

	private String editMode = EDITMODE_EDIT ;

	//Search Field Editor Map
	private HashMap<String,WEditor> searchEditorMap = new HashMap<String,WEditor> ();

	//Column Key Column
	private I_AD_Column m_columnKeyColumn;

	//Row Key Column
	private I_AD_Column m_rowKeyColumn ;

	//AD_Window_ID
	//private int AD_WINDOW_ID = 0;

	//Table Name
	private String TABLE_NAME ;


	private int FIX_ITEM_FIELD_ID = 0;


	/****************************************************
	 * Window Info
	 ****************************************************/
	private GridTab gridTab ;
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

	private void prepare(String Value) throws Exception
	{
		//Create Models that is used by Matrix Window
		m_matrixWindow = MMatrixWindow.get(Env.getCtx(), Value);
		if(m_matrixWindow == null)
		{
			;//Error
		}

		m_matrixFields = m_matrixWindow.getMatrixFields();
		m_contentFields = m_matrixWindow.getContentFields();
		m_matrixSearches = m_matrixWindow.getMatrixSearches();
		m_contentColumns = new MColumn[m_contentFields.length];
		for(int i = 0; i < m_contentFields.length; i++)
		{
			m_contentColumns[i]=new MColumn(Env.getCtx(),m_contentFields[i].getAD_Column_ID(),null);
		}


		//AD_WINDOW_ID = m_matrixWindow.getAD_Window_ID();
		m_Tab = new MTab(Env.getCtx(), m_matrixWindow.getAD_Tab_ID(), null);
		TABLE_NAME = MTable.get(Env.getCtx(), m_Tab.getAD_Table_ID()).getTableName();


		m_columnKeyColumn = m_matrixWindow.getJP_MatrixColumnKey().getAD_Column();
		m_rowKeyColumn = m_matrixWindow.getJP_MatrixRowKey().getAD_Column();

		FIX_ITEM_FIELD_ID = m_matrixWindow.getJP_MatrixRowKey().getAD_Field_ID();

		/*get <Display order,name of Column> for Grid*/
		//fixItem is a row that is Identifier of row. it is specification only one column.
		fixItem.put(0, m_rowKeyColumn.getColumnName());
		fixItemFieldIDMap.put(0,FIX_ITEM_FIELD_ID);


		//Create Window because of use Window info.
		GridWindowVO gridWindowVO =AEnv.getMWindowVO(form.getWindowNo(), m_matrixWindow.getAD_Window_ID(), 0);
		GridWindow gridWindow = new GridWindow(gridWindowVO);
		for(int i = 0; i < gridWindow.getTabCount(); i++)
		{
			GridTab gtab =gridWindow.getTab(i);
			if(gtab.getAD_Tab_ID()==m_matrixWindow.getAD_Tab_ID())
			{
				gridTab = gtab;
				break;
			}
		}

		gridTab.initTab(false);
		gridFields = gridTab.getFields();

		//Set Edit Mode.
		String discription = m_matrixWindow.getDescription();
		if(gridTab.isReadOnly())
		{
			editMode = EDITMODE_READ;
		}else if(discription != null && !discription.isEmpty() && discription.contains("test")){
			editMode = EDITMODE_TEST;
		}else{
			editMode = EDITMODE_EDIT;
		}

	}


	private void zkInit() throws Exception
	{
		form.appendChild(mainLayout);
		ZKUpdateUtil.setHeight(form, "100%");

		/*Main Layout(Borderlayout)*/
		ZKUpdateUtil.setWidth(mainLayout, "100%");
		ZKUpdateUtil.setHeight(mainLayout, "100%");

		//Main Layout(Borderlayout)-North
		North north = new North();
		mainLayout.appendChild(north);

		//Search Parameter Panel
		north.appendChild(parameterPanel);
		north.setStyle("border: none");
		parameterPanel.appendChild(parameterLayout); 		//parameterLayout = Grid
		ZKUpdateUtil.setWidth(parameterLayout, "100%");
		Rows parameterLayoutRows = parameterLayout.newRows();
		Row row = null;
		if(m_matrixSearches.length > 0)
		{
			//Caluculate max colspan
			int maxColspan = 0;
			int tmpColspan = 0;
			for(int i = 0; i < m_matrixSearches.length; i++)
			{
				tmpColspan = m_matrixSearches[i].getXPosition() + m_matrixSearches[i].getColumnSpan();
				if(maxColspan < tmpColspan)
					maxColspan = tmpColspan;
			}

			row = parameterLayoutRows.newRow();
				Groupbox searchGB = new Groupbox();
				row.appendCellChild(searchGB, maxColspan >= 10?  maxColspan+2 : 10);
				searchGB.appendChild(new Caption(Msg.getMsg(Env.getCtx(), "SearchCriteria")));
				Grid searchGrid  = new Grid();
				searchGrid.setStyle("background-color: #E9F0FF");
				searchGrid.setStyle("border: none");
				searchGB.appendChild(searchGrid);
				Rows rows = searchGrid.newRows();


			int actualxpos = 0;
			//Create Search Fields
			for(int i = 0; i < m_matrixSearches.length; i++)
			{


				MMatrixSearch searchField = m_matrixSearches[i];


				if(i == 0 || actualxpos > searchField.getXPosition())
				{
					actualxpos = 0;
					row = rows.newRow();
					row.setStyle("background-color: #ffffff");
				}

				WEditor editor = null;
				for(int j = 0; j < gridFields.length; j++)
				{
					if(m_matrixSearches[i].getAD_Field_ID() == gridFields[j].getAD_Field_ID())
					{
						editor = WebEditorFactory.getEditor(gridFields[j], false);
						break;
					}
				}

				if(editor == null)
				{
					GridField[] gFields = GridField.createFields(Env.getCtx(), form.getWindowNo(), 0, searchField.getAD_Tab_ID());
					for(int k = 0; k < gFields.length; k++)
					{
						if(m_matrixSearches[i].getAD_Field_ID() == gFields[k].getAD_Field_ID())
						{
							editor = WebEditorFactory.getEditor(gFields[k], false);
							break;
						}
					}
					;
				}

				if(editor == null)
				{
					;//TODO Error

				}else{
					String DefaultValue = m_matrixSearches[i].getDefaultValue();
					if(DefaultValue == null || DefaultValue.isEmpty())
					{
						;
					}else{

						String value = Env.parseContext(Env.getCtx(), form.getWindowNo(), DefaultValue, false);
						Env.setContext(Env.getCtx(), form.getWindowNo(), editor.getColumnName(), value);
						editor.setValue(Env.parseContext(Env.getCtx(), form.getWindowNo(), DefaultValue, false));

						if(editor instanceof WTableDirEditor)
						{
							((WTableDirEditor) editor).actionRefresh();
							((WTableDirEditor) editor).getLookup().setSelectedItem("");
						}

					}

					if(!editor.isReadWrite())
					{
						editor.setReadWrite(true);
						if(editor instanceof WTableDirEditor)
							((WTableDirEditor) editor).actionRefresh();

					}

					//Set zoom
					if(editor instanceof WSearchEditor
							|| editor instanceof WTableDirEditor)
					{
						editor.getLabel().addEventListener(Events.ON_CLICK, new ZoomListener((IZoomableEditor) editor));
						if(m_matrixSearches[i].isMandatory() && editor.getValue()==null)
							editor.getLabel().setStyle("cursor: pointer; text-decoration: underline;color: #333; color:red;");
						else
							editor.getLabel().setStyle("cursor: pointer; text-decoration: underline;color: #333;");
					}else if (editor instanceof WStringEditor){

						String stringValue = (String)editor.getValue();
						if(m_matrixSearches[i].isMandatory() && Util.isEmpty(stringValue))
							editor.getLabel().setStyle("color:red;");
					}

					editor.setMandatory(m_matrixSearches[i].isMandatory());

					//positioning
					row.appendCellChild(editor.getLabel().rightAlign(),1);
					actualxpos = actualxpos + 1;
					row.appendCellChild(editor.getComponent(),searchField.getColumnSpan());
					ZKUpdateUtil.setHflex((HtmlBasedComponent)editor.getComponent(), "true");
					actualxpos = actualxpos + searchField.getColumnSpan();

					//Popup Menu
					WEditorPopupMenu  popupMenu = editor.getPopupMenu();
					List<Component> listcomp = popupMenu.getChildren();
					Menuitem menuItem = null;
					String image = null;
					for(Component comp : listcomp)
					{
						if(comp instanceof Menuitem)
						{
							menuItem = (Menuitem)comp;
							image = menuItem.getImage();
							if(image != null && ( image.endsWith("Zoom16.png")||image.endsWith("Refresh16.png")
									|| image.endsWith("New16.png") || image.endsWith("InfoBPartner16.png") ) )
							{
								menuItem.setVisible(true);
							}else{
								menuItem.setVisible(false);
							}
						}
					}//for

		            if (popupMenu != null)
		            {
		            	popupMenu.addMenuListener((ContextMenuListener)editor);
		            	row.appendChild(popupMenu);

		            	popupMenu.addContextElement((XulElement) editor.getComponent());
		            }

					editor.addValueChangeListener(this);
					searchEditorMap.put(editor.getColumnName(), editor);

				}


			}//for i : Create Search Fields

			//Dynamic Validation
			for(Map.Entry<String, WEditor> entry: searchEditorMap.entrySet())
			{
				WEditor otherEditor = entry.getValue();
				GridField gridField = otherEditor.getGridField();

				if(otherEditor instanceof WTableDirEditor || otherEditor instanceof WSearchEditor )
				{

					if(gridField.getVFormat() != null && gridField.getVFormat().indexOf('@') != -1)
					{
						String validated = Env.parseContext(Env.getCtx(), form.getWindowNo(), gridField.getVFormat(), false);
						((MLookup)gridField.getLookup()).getLookupInfo().ValidationCode=validated;

					}else if(gridField.getLookup().getValidation().indexOf('@') != -1){

						gridField.setVFormat(gridField.getLookup().getValidation());
						String validated = Env.parseContext(Env.getCtx(), form.getWindowNo(), gridField.getVFormat(), false);
						((MLookup)gridField.getLookup()).getLookupInfo().ValidationCode=validated;

					}

					if(otherEditor instanceof WTableDirEditor)
						((WTableDirEditor)otherEditor).getLookup().refresh();

				}//if

			}//for Dynamic Validation

		}//if


		//Create Button
		row = parameterLayoutRows.newRow();
				SearchButton = new Button(Msg.getMsg(Env.getCtx(), "search"));
				SearchButton.setId("SearchButton");
				SearchButton.addActionListener(this);
				SearchButton.setEnabled(true);
				if (ThemeManager.isUseFontIconForImage())
					SearchButton.setIconSclass("z-icon-Find");
				else
					SearchButton.setImage(ThemeManager.getThemeResource("images/Find16.png"));
				ZKUpdateUtil.setWidth(SearchButton, "100%");
				row.appendCellChild(SearchButton);



				SaveButton = new Button(Msg.getMsg(Env.getCtx(), "save"));
				SaveButton.setId("SaveButton");
				SaveButton.addActionListener(this);
				SaveButton.setEnabled(false);
				if (ThemeManager.isUseFontIconForImage())
					SaveButton.setIconSclass("z-icon-Save");
				else
					SaveButton.setImage(ThemeManager.getThemeResource("images/Save16.png"));
				ZKUpdateUtil.setWidth(SaveButton, "100%");
				if(!editMode.equals(EDITMODE_READ))
					row.appendCellChild(SaveButton);

				CreateButton = new Button(Msg.getMsg(Env.getCtx(), "NewRecord"));
				CreateButton.setId("CreateButton");
				CreateButton.addActionListener(this);
				CreateButton.setEnabled(false);
				if (ThemeManager.isUseFontIconForImage())
					CreateButton.setIconSclass("z-icon-New");
				else
					CreateButton.setImage(ThemeManager.getThemeResource("images/New16.png"));
				ZKUpdateUtil.setWidth(CreateButton, "100%");
				if(m_matrixWindow.getJP_QuickEntryWindow_ID() > 0)
				{
					if(!editMode.equals(EDITMODE_READ))
						row.appendCellChild(CreateButton);
				}

				loadToolbarButtons();
				ProcessButton = new Button(Msg.getMsg(Env.getCtx(), "Process"));
				ProcessButton.setId("ProcessButton");
				ProcessButton.addActionListener(this);
				ProcessButton.setEnabled(false);
				if (ThemeManager.isUseFontIconForImage())
					ProcessButton.setIconSclass("z-icon-Process");
				else
					ProcessButton.setImage(ThemeManager.getThemeResource("images/Process16.png"));
				ZKUpdateUtil.setWidth(ProcessButton, "100%");
				if(toolbarProcessButtons.size()> 0 && !editMode.equals(EDITMODE_READ))
					row.appendCellChild(ProcessButton);

				matrixExportButton = new Button(Msg.getMsg(Env.getCtx(), "Export"));
				matrixExportButton.setId("ExportButton");
				matrixExportButton.addActionListener(this);
				matrixExportButton.setEnabled(false);
				if (ThemeManager.isUseFontIconForImage())
					matrixExportButton.setIconSclass("z-icon-Export");
				else
					matrixExportButton.setImage(ThemeManager.getThemeResource("images/Export16.png"));
				ZKUpdateUtil.setWidth(matrixExportButton, "100%");
				row.appendCellChild(matrixExportButton);



		//Edit Area
		Center center = new Center();
		center.setStyle("padding-top: 16px");
		mainLayout.appendChild(center);
		center.appendChild(displayDataPanel);
		displayDataPanel.appendChild(displayDataLayout);//Borderlayout
		ZKUpdateUtil.setWidth(displayDataPanel, "100%");
		ZKUpdateUtil.setHeight(displayDataPanel, "100%");
		ZKUpdateUtil.setHflex(displayDataPanel, "1");
		ZKUpdateUtil.setVflex(displayDataPanel, "1");
		ZKUpdateUtil.setWidth(displayDataLayout, "100%");
		ZKUpdateUtil.setHeight(displayDataLayout, "100%");
		displayDataLayout.setStyle("border: none");

			//Contents
			center = new Center();
			displayDataLayout.appendChild(center);
			center.appendChild(matrixGrid);
			center.setStyle("border: none");
			ZKUpdateUtil.setWidth(matrixGrid, "100%");
			ZKUpdateUtil.setHeight(matrixGrid, "100%");
			ZKUpdateUtil.setVflex(matrixGrid, "true");
			matrixGrid.setVisible(false);

			matrixGrid.setMold("paging");
			matrixGrid.setPageSize(m_matrixWindow.getJP_PageSize());
	}


	static class ZoomListener implements EventListener<Event>
	{

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
	public void tableChanged(WTableModelEvent e)
	{
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
			String columnName = columnGridFieldMap.get(col).getColumnName();
			po.set_ValueNoCheck(columnName, value);
			dirtyModel.put(po.get_ID(), po);

			if (renderer != null && renderer.isEditing())
				renderer.stopEditing();

			m_calculating = true;
			return;
		}
	}


//	@Override
	public void valueChange(ValueChangeEvent e)
	{

		WEditor editor = searchEditorMap.get(e.getPropertyName());

		editor.setValue(e.getNewValue());

		if(editor instanceof WYesNoEditor)
		{
			Env.setContext(Env.getCtx(), form.getWindowNo(), editor.getColumnName(), e.getNewValue().equals("true") ? "Y" : "N");
		}else{
			Env.setContext(Env.getCtx(), form.getWindowNo(), editor.getColumnName(), e.getNewValue()==null ? null : e.getNewValue().toString());
		}

		SearchButton.setEnabled(true);
		SaveButton.setEnabled(false);
		CreateButton.setEnabled(false);
		ProcessButton.setEnabled(false);
		matrixExportButton.setEnabled(false);

		quickEntry = null;

		matrixGrid.setVisible(false);

		if(e.getNewValue()==null)
		{
			if(editor.isMandatory() && (editor instanceof WSearchEditor
					|| editor instanceof WTableDirEditor))
			{
				editor.getLabel().setStyle("cursor: pointer; text-decoration: underline;color: #333; color:red;");
			}

		}else{

			if(editor.isMandatory() && (editor instanceof WSearchEditor
					|| editor instanceof WTableDirEditor))
			{
				editor.getLabel().setStyle("cursor: pointer; text-decoration: underline;color: #333; ");
			}else if (editor.isMandatory() && editor instanceof WStringEditor){

				String stringValue =(String)e.getNewValue();
				if(Util.isEmpty(stringValue))
					editor.getLabel().setStyle("color:red;");
				else
					editor.getLabel().setStyle("color:#333;");;
			}

		}


		//Dynamic Validation
		for(Map.Entry<String, WEditor> entry: searchEditorMap.entrySet())
		{
			WEditor otherEditor = entry.getValue();
			GridField gridField = otherEditor.getGridField();

			if(otherEditor.getColumnName().equals(editor.getColumnName()))
			{
				;
			}else if(otherEditor instanceof WTableDirEditor || otherEditor instanceof WSearchEditor ){

				if(gridField.getVFormat() != null && gridField.getVFormat().indexOf('@') != -1)
				{
					String validated = Env.parseContext(Env.getCtx(), form.getWindowNo(), gridField.getVFormat(), false);
					((MLookup)gridField.getLookup()).getLookupInfo().ValidationCode=validated;

				}else if(gridField.getLookup().getValidation().indexOf('@') != -1){

					gridField.setVFormat(gridField.getLookup().getValidation());
					String validated = Env.parseContext(Env.getCtx(), form.getWindowNo(), gridField.getVFormat(), false);
					((MLookup)gridField.getLookup()).getLookupInfo().ValidationCode=validated;

				}

				if(otherEditor instanceof WTableDirEditor)
					((WTableDirEditor)otherEditor).getLookup().refresh();

			}//if

		}//for

	}



	@Override
	public void onEvent(Event e) throws Exception {

		if(message != null && !Util.isEmpty(message.toString()))
		{
			Dialog.info(form.getWindowNo(), null, message.toString());
			message = new StringBuilder();
			return;
		}

		if (e == null)
		{
			return;
		}
		else if (e.getTarget() == matrixGrid && Events.ON_CLICK.equals(e.getName()))
		{
			Object data = e.getData();
			org.zkoss.zul.Row row = null;
			if (data != null && data instanceof Component)
			{
				AbstractComponent cmp = (AbstractComponent) data;
				if(cmp instanceof Cell && !(cmp.getChildren().get(0) instanceof org.zkoss.zul.Label))
				{
					//control focus
					if(renderer.setFocus(cmp.getChildren().get(0)))
						return;
				}

				if (cmp.getParent() instanceof org.zkoss.zul.Row)
				{
					row = (org.zkoss.zul.Row) cmp.getParent();
				}
			}

			if (row != null)
			{
				renderer.setCurrentRow(renderer.getCurrentRow());
				renderer.editCurrentRow();
			}
			e.stopPropagation();

		/*JPiereMatrixWindowQuickEntry#ConfirmPanel*/
		}else if(e.getName().equals(ConfirmPanel.A_CANCEL)){

			if(!createView ())
			{
				SearchButton.setEnabled(true);
				SaveButton.setEnabled(false);
				CreateButton.setEnabled(false);
				ProcessButton.setEnabled(false);
				matrixExportButton.setEnabled(false);

				matrixGrid.setVisible(false);

				Dialog.info(form.getWindowNo(), null, message.toString());

				return;
			}

			SearchButton.setEnabled(false);
			SaveButton.setEnabled(true);
			CreateButton.setEnabled(true);
			ProcessButton.setEnabled(true);
			matrixExportButton.setEnabled(true);


			quickEntry = null;

		/*JPiereMatrixWindowQuickEntry#ConfirmPanel*/
		}else if(e.getName().equals(ConfirmPanel.A_OK)){ //Keep on creating new record

			Events.sendEvent(Events.ON_CLICK, CreateButton, null);

		}else if (e.getTarget().equals(SearchButton) || e.getName().equals("onComplete")){//onCompolete from process dialog

			if(!createView ())
			{
				SearchButton.setEnabled(true);
				SaveButton.setEnabled(false);
				CreateButton.setEnabled(true);
				ProcessButton.setEnabled(true);
				matrixExportButton.setEnabled(true);

				matrixGrid.setVisible(false);
				return;
			}


			SearchButton.setEnabled(false);
			SaveButton.setEnabled(true);
			CreateButton.setEnabled(true);
			ProcessButton.setEnabled(true);
			matrixExportButton.setEnabled(true);

			if(e.getName().equals("onComplete"))
			{
				JPiereMatrixWindowProcessModelDialog dialog = (JPiereMatrixWindowProcessModelDialog)e.getTarget();
				ProcessInfo pInfo = dialog.getProcessInfo();

//				dialog.updateUI();
//				HtmlBasedComponent  ditailLog = dialog.getInfoResultContent();

				Dialog.info(form.getWindowNo(), pInfo.getSummary(), pInfo.getLogInfo(), pInfo.getTitle());

			}

		}else if(e.getTarget().equals(SaveButton)){

			boolean isOK = saveData();

			if(isOK)
			{
				dirtyModel.clear();

				if(!createView ())
				{
					matrixGrid.setVisible(false);
					Dialog.info(form.getWindowNo(), message.toString());//FDialog.
					return ;
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

			//Create Quick entry window
			quickEntry = new JPiereMatrixWindowQuickEntry (form.getWindowNo(), m_matrixWindow.getJP_QuickEntryWindow_ID(), this);
			quickEntry.loadRecord (0);
			List<WEditor> editors = quickEntry.getQuickEditors();


			String  JP_QuickEntryConf = m_matrixWindow.getJP_QuickEntryConf();
			for(WEditor editor : editors)
			{
				//Search Field Value can not update,Search Field Value is read only except WStringEditor
				for(Map.Entry<String, WEditor> entry: searchEditorMap.entrySet())
				{
					if(editor.getColumnName().equals(entry.getKey()))
					{
						editor.setValue(entry.getValue().getValue());
						if(entry.getValue().getValue() == null)
						{
							editor.setReadWrite(true);
						}else{
							if(editor instanceof WStringEditor)
								editor.setReadWrite(true);
							else
								editor.setReadWrite(false);
						}
					}
				}//for

				//Set Column key Valu or Row key Value or Both;
				if(JP_QuickEntryConf != null)
				{

					if(JP_QuickEntryConf.equals(MMatrixWindow.JP_QUICKENTRYCONF_ColumnInfoOnly))
					{
						if(editor.getColumnName().equals(m_columnKeyColumn.getColumnName()))
							editor.setValue(old_columnKeyColumn_Value);

					}else if(JP_QuickEntryConf.equals(MMatrixWindow.JP_QUICKENTRYCONF_RowInfoOnly)){

						if(editor.getColumnName().equals(m_rowKeyColumn.getColumnName()))
							editor.setValue(old_rowKeyColumn_Value);

					}else if(JP_QuickEntryConf.equals(MMatrixWindow.JP_QUICKENTRYCONF_ColumnAndRowInfo)){

						if(editor.getColumnName().equals(m_rowKeyColumn.getColumnName()))
							editor.setValue(old_rowKeyColumn_Value);
						else if(editor.getColumnName().equals(m_columnKeyColumn.getColumnName()))
							editor.setValue(old_columnKeyColumn_Value);
					}
				}


			}//for

			AEnv.showWindow(quickEntry);

		}else if(e.getTarget().equals(ProcessButton)){

			ProcessButtonPopup popup = new ProcessButtonPopup();
			popup.setWidgetAttribute(AdempiereWebUI.WIDGET_INSTANCE_NAME, "processButtonPopup");

			List<org.zkoss.zul.Button> buttonList = new ArrayList<org.zkoss.zul.Button>();
			for(ToolbarProcessButton processButton : toolbarProcessButtons) {
				if (processButton.getButton().isVisible()) {
					buttonList.add(processButton.getButton());
				}
			}

			popup.render(buttonList);
			if (popup.getChildren().size() > 0) {
				popup.setPage(ProcessButton.getPage());
				popup.open(ProcessButton, "after_start");
			}

		}else if(e.getTarget() instanceof Auxheader) {

			Auxheader header = (Auxheader)e.getTarget() ;
			Object record_id = header.getAttribute("record_id");
			Object table_name = header.getAttribute("table_name");
			AEnv.zoom(MTable.getTable_ID(table_name.toString()), Integer.valueOf(record_id.toString()));


		}else if (e.getTarget().equals(matrixExportButton)) {

			MatrixWindowButtonPopup popup = new MatrixWindowButtonPopup();
			popup.setWidgetAttribute(AdempiereWebUI.WIDGET_INSTANCE_NAME, "exportButtonPopup");

			List<org.zkoss.zul.Button> buttonList = new ArrayList<org.zkoss.zul.Button>();
			matrixExportXLS = new Button();
			matrixExportXLS.setId("matrixExportXLS");
			matrixExportXLS.addActionListener(this);
			matrixExportXLS.setLabel("xls");
			matrixExportXLS.setEnabled(true);
			buttonList.add(matrixExportXLS);

			popup.render(buttonList);
			LayoutUtils.openPopupWindow(matrixExportButton, popup, "after_start");

		}else if (e.getTarget().equals(matrixExportXLS)){

			int recordSize = viewModel.size();

			if(recordSize == 0)
			{
				Dialog.info(form.getWindowNo(), "not.found", "",Msg.getElement(Env.getCtx(), "JP_MatrixWindow_ID"));
			}else{

				JPiereMatrixExcelExporter exporter = new JPiereMatrixExcelExporter(this);
				File file;
				try {

					file = new File(FileUtil.getTempMailName(form.getFormName(), ".xls"));
					exporter.export(file);
					Filedownload.save(file, "application/vnd.ms-excel");

				} catch (Exception exception) {

					Dialog.info(form.getWindowNo(), "Error", Msg.getMsg(Env.getCtx(), "ExportExcel") + " : " + exception.toString());
					throw new RuntimeException(exception);
				}


			}

		}

	}//onEvent()

	private void loadToolbarButtons() {
		//get extra toolbar process buttons
        MToolBarButton[] mToolbarButtons = MToolBarButton.getProcessButtonOfTab(gridTab.getAD_Tab_ID(), null);
        for(MToolBarButton mToolbarButton : mToolbarButtons) {
        	Boolean access = MRole.getDefault().getProcessAccess(mToolbarButton.getAD_Process_ID());
        	if (access != null && access.booleanValue()) {
        		ToolbarProcessButton toolbarProcessButton = new ToolbarProcessButton(mToolbarButton, null, this, form.getWindowNo());
        		toolbarProcessButtons.add(toolbarProcessButton);
        	}
        }

        if (toolbarProcessButtons.size() > 0) {
        	int ids[] = MToolBarButtonRestrict.getProcessButtonOfTab(Env.getCtx(), Env.getAD_Role_ID(Env.getCtx()), gridTab.getAD_Tab_ID(), null);
        	if (ids != null && ids.length > 0) {
        		for(int id : ids) {
        			X_AD_ToolBarButton tbt = new X_AD_ToolBarButton(Env.getCtx(), id, null);
        			for(ToolbarProcessButton btn : toolbarProcessButtons) {
        				if (tbt.getComponentName().equals(btn.getColumnName())) {
        					toolbarProcessButtons.remove(btn);
        					break;
        				}
        			}
        		}
        	}
        }
	}

	Auxhead auxhead ;

	private boolean createView () throws Exception {

		matrixGrid.setVisible(true);

		//Create String where clause
		whereClause = createWhere();
		if(!Util.isEmpty(message.toString()))
		{
			Dialog.info(form.getWindowNo(), message.toString());
			message = new StringBuilder();
			return false;
		}


		//Create Column key info from where clause
		columnKeys = createColumnKeys(whereClause);
		if(columnKeys.size()==0)
		{
			message.append(System.getProperty("line.separator") + Msg.getMsg(Env.getCtx(), "not.found"));
			Dialog.info(form.getWindowNo(), message.toString());
			message = new StringBuilder();
			return false;
		}


		//Create Row key info from where clause
		rowKeys = createRowKeys(whereClause);
		if(rowKeys.size()==0)
		{
			message.append(System.getProperty("line.separator") + Msg.getMsg(Env.getCtx(), "not.found"));
			Dialog.info(form.getWindowNo(),message.toString());
			message = new StringBuilder();
			return false;
		}

		//Create array of PO from where clause
		m_POs = getPOs(whereClause,true);
		if(m_POs.length==0)
		{
			message.append(System.getProperty("line.separator") + Msg.getMsg(Env.getCtx(), "not.found"));
			Dialog.info(form.getWindowNo(), message.toString());
			message = new StringBuilder();
			return false;
		}

		tableModel = createTableModel(m_POs);
		keyColumnModel = createKeyColumnModel(m_POs);

		viewModel = createViewModelConvetionTable();
		conversionTable = createViewModelConvetionTable();

		setViewModelConvetionTable();


		JPListModelMapTable vmListModelMap = new JPListModelMapTable(viewModel);
		JPListModelMapTable ctListModelMap = new JPListModelMapTable(conversionTable);

		//Call out this#tableChanged() method form JPListModelMapTable#setDataAt()
		vmListModelMap.addTableModelListener(this);
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

		updateColumn();

		renderer = new JPMatrixGridRowRenderer(vmListModelMap,ctListModelMap,tableModel,dirtyModel, form,this);
		renderer.setcColumnsSize(columnNameMap.size());
		renderer.setGridTab(gridTab);
		renderer.setColumnGridFieldMap(columnGridFieldMap);
		renderer.createRecordProcessDialog();

		matrixGrid.setRowRenderer(renderer);
		matrixGrid.addEventListener(Events.ON_CLICK, this);

		return true;

	}


	private String createWhere()
	{
		StringBuilder whereClause = new StringBuilder(" WHERE "+ TABLE_NAME+".AD_Client_ID = "+ Env.getAD_Client_ID(Env.getCtx()));

		for(Map.Entry<String, WEditor> entry: searchEditorMap.entrySet())
		{
			Object value = entry.getValue().getValue();
			if(entry.getValue() instanceof WStringEditor)
			{
				String stringValue = (String)entry.getValue().getValue();
				if(Util.isEmpty(stringValue))
					value = null;
			}

			if(value != null)
			{

				String tableName = null;
				GridField gField = ((WEditor)entry.getValue()).getGridField();
				GridTab gTab = gField.getGridTab();
				if(gTab != null)
				{
					tableName = gTab.getTableName();
				}else{
					int AD_Tab_ID = gField.getAD_Tab_ID();
					MTab tab = new MTab(Env.getCtx(),AD_Tab_ID,null);
					tableName = tab.getAD_Table().getTableName();
				}

				if(entry.getValue() instanceof WYesNoEditor)
				{
					if(entry.getValue().getValue().equals(true))
						whereClause.append(" AND "+ tableName+"."+ entry.getKey() + " = " + "'Y'");
					else
						whereClause.append(" AND "+ tableName+"."+ entry.getKey() + " = " + "'N'");

				}else if(entry.getValue().getGridField().getDisplayType()==DisplayType.List){

					whereClause.append(" AND "+ tableName+"."+ entry.getKey() + " = " + "'" + entry.getValue().getValue() + "'");

				}else if(DisplayType.isText(entry.getValue().getGridField().getDisplayType())){
					String string = (String)entry.getValue().getValue();
					if(!string.isEmpty())
					{
						whereClause.append(" AND "+ tableName+"."+ entry.getKey() + " LIKE " + "'" + string + "'");
					}

				}else if(DisplayType.isDate(entry.getValue().getGridField().getDisplayType())){

					Timestamp timestamp = (Timestamp)entry.getValue().getValue();
					whereClause.append(" AND "+ tableName+"."+ entry.getKey() + "=" +"TO_DATE('"+ timestamp.toString() +"','YYYY-MM-DD HH24:MI:SS')");

//					if(entry.getValue().getGridField().getDisplayType()==DisplayType.Date)
//					{
//						whereClause.append(" AND "+ tableName+"."+ entry.getKey() + "=" +"TO_DATE('"+ timestamp.toString() +"','YYYY-MM-DD HH24:MI:SS')");
//
//					}else if(entry.getValue().getGridField().getDisplayType()==DisplayType.DateTime){
//
//						whereClause.append(" AND "+ tableName+"."+ entry.getKey() + "=" +"TO_DATE('"+ timestamp.toString() +"','YYYY-MM-DD HH24:MI:SS')");
//
//					}else if(entry.getValue().getGridField().getDisplayType()==DisplayType.Time){
//
//						;
//					}

				}else{

					whereClause.append(" AND "+ tableName+"."+ entry.getKey() + " = " + entry.getValue().getValue());

				}

			}else{

				if(entry.getValue().isMandatory())
				{
					message.append(System.getProperty("line.separator") + Msg.getMsg(Env.getCtx(), "FillMandatory") + entry.getValue().getLabel().getValue() );
				}
			}
		}//for

		if(m_matrixWindow.getWhereClause() != null)
		{
			String parsed = Env.parseContext(Env.getCtx(), form.getWindowNo(), m_matrixWindow.getWhereClause(), false);
			whereClause.append(" AND " + parsed);
		}

		MRole role = MRole.get(Env.getCtx(), Env.getAD_Role_ID(Env.getCtx()));
		String orgAccessSQL = role.getOrgWhere(false);
		if( orgAccessSQL != null)
		{
			orgAccessSQL = orgAccessSQL.substring(1);//Ref - IDEMPIERE-4254
			whereClause.append(" AND (").append(gridTab.getTableName()).append(".").append(orgAccessSQL);
		}

		return whereClause.toString();
	}


	private ArrayList<Object> createColumnKeys(String whereClause)
	{
		ArrayList<Object> list = new ArrayList<Object>();
		StringBuilder sql =new StringBuilder("SELECT DISTINCT " + TABLE_NAME + "." + m_columnKeyColumn.getColumnName() + " FROM " + TABLE_NAME );
		if(m_matrixWindow.getJP_JoinClause() != null)
		{
			sql.append(" "+m_matrixWindow.getJP_JoinClause());
		}

		sql.append(whereClause).append(" ORDER BY " + TABLE_NAME + "." + m_columnKeyColumn.getColumnName());

		I_AD_Field keyField = m_matrixWindow.getJP_MatrixColumnKey();
		I_AD_Column keyColumn = keyField.getAD_Column();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		columnKeyNameMap.clear();
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
			rs = pstmt.executeQuery();
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
			log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		if(list.size() == 0)
		{
			return list;
		}

		//Sort List
		if(keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_TABLEDIR
				|| keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_TABLE
				|| keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_SEARCH )
		{
			ArrayList<Object> sortedList = new ArrayList<Object>();
			StringBuilder sortSQL = new StringBuilder("SELECT ");
			int AD_Reference_Value_ID = 0;

			if(keyField.getAD_Reference_Value_ID()!=0)
			{
				AD_Reference_Value_ID = keyField.getAD_Reference_Value_ID();

			}else if(keyColumn.getAD_Reference_Value_ID()!=0){

				AD_Reference_Value_ID = keyColumn.getAD_Reference_Value_ID();
			}

			if(AD_Reference_Value_ID == 0)
			{
				return list;
			}else{
				MRefTable ref = new MRefTable(Env.getCtx(), AD_Reference_Value_ID, null);
				sortSQL.append(MColumn.getColumnName(Env.getCtx(), ref.getAD_Key()));
				sortSQL.append(" FROM ").append(ref.getAD_Table().getTableName());
				sortSQL.append(" WHERE ").append(MColumn.getColumnName(Env.getCtx(), ref.getAD_Key()));
				sortSQL.append(" IN (");
				for(int i = 0; i < list.size(); i++)
				{
					if(i == 0)
						sortSQL.append(list.get(i));
					else
						sortSQL.append(","+list.get(i));
				}
				sortSQL.append(" )");
				sortSQL.append(" ORDER BY ").append(ref.getOrderByClause());
			}

			try
			{
				pstmt = DB.prepareStatement(sortSQL.toString(), null);
				rs = pstmt.executeQuery();
				while (rs.next())
				{
					sortedList.add(rs.getInt(1));
				}
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, sortSQL.toString(), e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}

			return sortedList;

		}else{

			return list;

		}
	}


	private ArrayList<Object> createRowKeys(String whereClause)
	{
		ArrayList<Object> list = new ArrayList<Object>();
		StringBuilder sql = new StringBuilder("SELECT DISTINCT "  + TABLE_NAME + "." +  m_rowKeyColumn.getColumnName() +" FROM " + TABLE_NAME);
		if(m_matrixWindow.getJP_JoinClause() != null)
		{
			sql.append(" "+m_matrixWindow.getJP_JoinClause());
		}
		sql.append(whereClause).append(" ORDER BY " + TABLE_NAME + "." + m_rowKeyColumn.getColumnName());

		I_AD_Field keyField = m_matrixWindow.getJP_MatrixRowKey();
		I_AD_Column keyColumn = keyField.getAD_Column();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
			rs = pstmt.executeQuery();
			while (rs.next())
			{
				if(keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_TABLEDIR
						|| keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_TABLE
						|| keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_SEARCH )
				{
					list.add(rs.getInt(1));
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
			log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}


		//Sort List
		if(keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_TABLEDIR
				|| keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_TABLE
				|| keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_SEARCH )
		{
			ArrayList<Object> sortedList = new ArrayList<Object>();
			StringBuilder sortSQL = new StringBuilder("SELECT ");
			int AD_Reference_Value_ID = 0;

			if(keyField.getAD_Reference_Value_ID()!=0)
			{
				AD_Reference_Value_ID = keyField.getAD_Reference_Value_ID();

			}else if(keyColumn.getAD_Reference_Value_ID()!=0){

				AD_Reference_Value_ID = keyColumn.getAD_Reference_Value_ID();
			}

			if(AD_Reference_Value_ID == 0)
			{
				return list;
			}else{
				MRefTable ref = new MRefTable(Env.getCtx(), AD_Reference_Value_ID, null);
				sortSQL.append(MColumn.getColumnName(Env.getCtx(), ref.getAD_Key()));
				sortSQL.append(" FROM ").append(ref.getAD_Table().getTableName());
				sortSQL.append(" WHERE ").append(MColumn.getColumnName(Env.getCtx(), ref.getAD_Key()));
				sortSQL.append(" IN (");
				for(int i = 0; i < list.size(); i++)
				{
					if(i == 0)
						sortSQL.append(list.get(i));
					else
						sortSQL.append(","+list.get(i));
				}
				sortSQL.append(" )");
				if(!Util.isEmpty(ref.getOrderByClause()))
					sortSQL.append(" ORDER BY ").append(ref.getOrderByClause());
			}

			try
			{
				pstmt = DB.prepareStatement(sortSQL.toString(), null);
				rs = pstmt.executeQuery();
				while (rs.next())
				{
					sortedList.add(rs.getInt(1));
				}
			}
			catch (Exception e)
			{
				log.log(Level.SEVERE, sortSQL.toString(), e);
			}
			finally
			{
				DB.close(rs, pstmt);
				rs = null; pstmt = null;
			}

			return sortedList;

		}else{

			return list;

		}
	}//createRowKeys


	private PO[] getPOs (String whereClause,boolean reload)
	{
		if (reload || m_POs == null || m_POs.length == 0)
			;
		else
			return m_POs;
		//
		ArrayList<PO> list = new ArrayList<PO>();

		StringBuilder sql = new StringBuilder("SELECT " + TABLE_NAME+".* FROM " + TABLE_NAME );
		if(m_matrixWindow.getJP_JoinClause() != null)
		{
			sql.append(" "+ m_matrixWindow.getJP_JoinClause());
		}
		sql.append(whereClause + " ORDER BY " + m_columnKeyColumn.getColumnName() + "," + m_rowKeyColumn.getColumnName());

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql.toString(), null);
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
			log.log(Level.SEVERE, sql.toString(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}

		m_POs = new PO[list.size()];
		list.toArray(m_POs);
		return m_POs;
	}	//	getPOs


	/*
	 * Map of PO Instance <ID of PO,PO>
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
	 * Create Map of PO per column of x-axis
	 *
	 * @return LinkedHashMap<Key of Column info,LinkedHashMap<Key of Row info,PO>>
	 */
	private LinkedHashMap<Object,LinkedHashMap<Object, PO>> createKeyColumnModel(PO[] POs)
	{
		keyColumnModel.clear();
		Object columnKey = null;
		LinkedHashMap<Object, PO>  mapObj = null; //LinkedHashMap<Key of Row info,PO>
		for(int i = 0; i < columnKeys.size(); i++)
		{
			columnKey = columnKeys.get(i);
			mapObj = null;
			for(int j = 0; j < POs.length; j++)
			{
				if(columnKey.equals(POs[j].get_Value(m_columnKeyColumn.getColumnName())))
				{
					mapObj = keyColumnModel.get(columnKey);
					if(mapObj == null)
					{
						mapObj = new LinkedHashMap<Object,PO>();
						mapObj.put(POs[j].get_Value(m_rowKeyColumn.getColumnName()), POs[j]);
						keyColumnModel.put(columnKey, mapObj);
					}else{
						mapObj.put(POs[j].get_Value(m_rowKeyColumn.getColumnName()), POs[j]);
					}
				}

			}//for j
		}//for i

		return keyColumnModel;
	}

	/*
	 * This Method creates "View Model" and "Convetion Table".
	 * "View Model" and "Convetion Table" is same Map structure.
	 * Set Row Key(Y-axis),and Secure number of column required.
	 *
	 * @return LinkedHashMap<y,TreeMap<x,object>>
	 */
	private LinkedHashMap<Object,TreeMap<Integer,Object>> createViewModelConvetionTable()
	{

		LinkedHashMap<Object,TreeMap<Integer,Object>> y = new LinkedHashMap<Object,TreeMap<Integer,Object>>();

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
			LinkedHashMap<Object,PO> POs = keyColumnModel.get(keyColumn);
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
					vmRow.put(fixItemFieldIDMap.size()+(i*m_contentFields.length)+k,  POs.get(rowKey).get_Value(m_contentColumns[k].getColumnName()));
					ctRow.put(fixItemFieldIDMap.size()+(i*m_contentFields.length)+k,  POs.get(rowKey).get_ID());
				}

			}//for(Object rowKey : rowKeys)

			i++;//Column key counter

		}//for(Object keyColumn :keyColumnModelKeySet)

		return ;
	}

	private Auxhead createAuxhead() throws ClassNotFoundException{

		Auxhead auxhead = new Auxhead();

		//Fix item header is blank.
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
				auxheader.setTooltiptext(columnKeyNameMap.get(columnKeys.get(i)));
				auxheader.addEventListener("onClick", this);
				auxheader.setAttribute("record_id", columnKeys.get(i));
				auxheader.setStyle("cursor: pointer;text-decoration: underline;");

				String zoom_TableName = null;
				if(keyColumn.getAD_Reference_Value_ID()==0)
				{
					zoom_TableName = keyColumn.getColumnName().substring(0, keyColumn.getColumnName().indexOf("_ID"));

				}else {

					MRefTable refTable = MRefTable.get(Env.getCtx(), keyColumn.getAD_Reference_Value_ID());
					zoom_TableName = MTable.getTableName(Env.getCtx(), refTable.getAD_Table_ID());
				}

				auxheader.setAttribute("table_name", zoom_TableName);
			}

		}else if(keyColumn.getAD_Reference_ID()==SystemIDs.REFERENCE_DATATYPE_INTEGER ){
			;//no auxhead
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
			;//no auxhead
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

			if(columnLengthMap.get(i)==0)
			{
				col.setHflex("min");
			}else{
				col.setWidth(columnLengthMap.get(i)+"px");
			}
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
		columnGridFieldMap.clear();
		columnSummarizedMap.clear();

		//Fix Column
		for(int i = 0; i < fixItemFieldIDMap.size(); i++)
		{
			columnNameMap.put(c, Msg.getElement(Env.getCtx(), fixItem.get(i)));
			columnLengthMap.put(c,m_matrixWindow.getFieldLength());
			columnSummarizedMap.put(c, false);
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
				columnSummarizedMap.put(c, m_matrixFields[j].isSummarized());
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


	String sum = Msg.getMsg(Env.getCtx(), "Sum");
	private void updateColumn()
	{
		org.zkoss.zul.Columns columns = matrixGrid.getColumns();
		List<Component>columnList =  columns.getChildren();

		BigDecimal[] totalValues = new BigDecimal[columnList.size()];
		for(int i = 0 ; i < totalValues.length; i++)
			totalValues[i] = new BigDecimal(0);


		TreeMap<Integer,Object> columnDataMap = null;
		int columnDisplayType = 0;
		Object valuObj = null;
		for(Object rowKey :rowKeys)//get row
		{
			columnDataMap = viewModel.get(rowKey);
			for(int i = 0; i < totalValues.length; i++)//get columns
			{
				if(i==0)//Fix Column
				{
					;//Nothing to do;
				}else{

					columnDisplayType = columnGridFieldMap.get(i).getDisplayType();

					if(columnDisplayType == DisplayType.Number || columnDisplayType == DisplayType.Quantity
							|| columnDisplayType == DisplayType.Amount || columnDisplayType == DisplayType.CostPrice)
					{
						valuObj = columnDataMap.get(i);
						if(valuObj!=null)
							totalValues[i] = totalValues[i].add((BigDecimal)valuObj);
					}else if(columnDisplayType == DisplayType.Integer){
						valuObj = columnDataMap.get(i);
						if(valuObj!=null)
							totalValues[i] = totalValues[i].add(new BigDecimal(valuObj.toString()));
					}
					valuObj=null;
				}//if
			}//for
		}//for



		int c = 0;//Column counter
		columnDisplayType = 0;
		for(Component column : columnList)
		{
			if(c == 0)//Fix Column
			{
				c++;
				continue;
			}

			columnDisplayType = columnGridFieldMap.get(c).getDisplayType();

			Column col= (Column)column;
			if(columnDisplayType == DisplayType.Number || columnDisplayType == DisplayType.Quantity
					|| columnDisplayType == DisplayType.Amount || columnDisplayType == DisplayType.Integer
					|| columnDisplayType == DisplayType.CostPrice)
			{
				if(columnSummarizedMap.get(c).booleanValue())
				{
					DecimalFormat format = DisplayType.getNumberFormat(columnDisplayType);
					col.setLabel(columnNameMap.get(c) + "(" + sum + ":" + format.format(totalValues[c]) + ")");
				}
			}
			c++;
		}//for


		return;

	}


	private boolean saveData()
	{
		notSavePO = new ArrayList<PO>();

		try
		{

			Trx.run(new TrxRunnable()
			{
				public void run(String trxName)
				{

					Collection<PO> POs = dirtyModel.values();
					for(PO po :POs)
					{
						if(checkExclusiveControl(po))
						{
							po.saveEx(trxName);

						}else{//not save
							notSavePO.add(po);
						}
					}

					updateColumn();

				}
			});

			if(notSavePO.size() > 0)
			{
				String msg = Msg.getMsg(Env.getCtx(), "SaveErrorDataChanged");//Could not save changes - data was changed after query.

				for(PO po :notSavePO)
				{
					msg = msg + System.lineSeparator() + po.toString();
				}
				Dialog.error(form.getWindowNo(), "Next", msg);
				createView();
			}

			return true;

		}
		catch (Exception e)
		{
			Dialog.error(form.getWindowNo(), "SaveError", e.getLocalizedMessage());
			return false;
		}finally{
			;
		}
	}   //  saveData


	/**
	 *
	 *  If this method returns false, you can not save. because other people saved same record before you save.
	 *  I refered GridTable.hasChanged() method.
	 *
	 */
	private boolean checkExclusiveControl(PO po)
	{
		int colUpdated = po.get_ColumnIndex("Updated");
		int colProcessed = po.get_ColumnIndex("Processed");

		boolean hasUpdated = (colUpdated > 0);
		boolean hasProcessed = (colProcessed > 0);

		String columns = null;
		if (hasUpdated && hasProcessed) {
			columns = new String("Updated, Processed");
		} else if (hasUpdated) {
			columns = new String("Updated");
		} else if (hasProcessed) {
			columns = new String("Processed");
		} else {
			// no columns updated or processed to commpare
			return false;
		}

		Timestamp dbUpdated = null;
	   	String dbProcessedS = null;
	   	PreparedStatement pstmt = null;
	   	ResultSet rs = null;
	   	String sql = "SELECT " + columns + " FROM " + TABLE_NAME + " WHERE " + TABLE_NAME + "_ID=?";
	   	try
	   	{
	   		pstmt = DB.prepareStatement(sql, null);
	   		pstmt.setInt(1, po.get_ID());
	   		rs = pstmt.executeQuery();
	   		if (rs.next()) {
	   			int idx = 1;
	   			if (hasUpdated)
	   				dbUpdated = rs.getTimestamp(idx++);
	   			if (hasProcessed)
	   				dbProcessedS = rs.getString(idx++);
	   		}
	   		else
	   			if (log.isLoggable(Level.INFO)) log.info("No Value " + sql);
	   	}
	   	catch (SQLException e)
	   	{
	   		throw new DBException(e, sql);
	   	}
	   	finally
	   	{
	   		DB.close(rs, pstmt);
	   		rs = null; pstmt = null;
	   	}

	   	if (hasUpdated)
	   	{
				Timestamp memUpdated = null;
				memUpdated = (Timestamp) po.get_Value(colUpdated);
				if (memUpdated != null && ! memUpdated.equals(dbUpdated))
					return false;
	   	}

	   	if (hasProcessed)
	   	{
				Boolean memProcessed = null;
				memProcessed = (Boolean) po.get_Value(colProcessed);

				Boolean dbProcessed = Boolean.TRUE;
				if (! dbProcessedS.equals("Y"))
					dbProcessed = Boolean.FALSE;
				if (memProcessed != null && ! memProcessed.equals(dbProcessed))
					return false;
	   	}

		return true;
	}


	public String getEditMode()
	{
		return editMode;

	}

	public void actionPerformed(final ActionEvent event)
	{
		if(dirtyModel.size() > 0 )
		{
			boolean isOK = saveData();

			if(isOK)
			{
				dirtyModel.clear();

			}else{
				;//Nothing to do
			}
		}

		ToolbarProcessButton button = (ToolbarProcessButton)event.getSource();

		JPiereMatrixWindowProcessModelDialog dialog = new JPiereMatrixWindowProcessModelDialog(form.getWindowNo(),button.getProcess_ID(), 0, 0, false, this);

		if (dialog.isValid())
		{
			//dialog.setWidth("500px");
			dialog.setBorder("normal");
			form.getParent().appendChild(dialog);
			//showBusyMask(dialog);
			LayoutUtils.openOverlappedWindow(form.getParent(), dialog, "middle_center");
			dialog.focus();
		}
		else
		{
			//onRefresh(true, false);
		}

	}


	public LinkedHashMap<Object,TreeMap<Integer,Object>> getViewModel()
	{
		return viewModel ;
	}

	public 	 HashMap<Object,String> getColumnKeyNameMap()
	{
		return columnKeyNameMap;
	}

	public HashMap<Integer,String> getColumnNameMap()
	{
		return columnNameMap;
	}

	public 	ArrayList<Object> getColumnKeys()
	{
		return columnKeys;
	}

	public ArrayList<Object> getRowKeys()
	{
		return rowKeys;
	}

	public MField[]	 getContentFields()
	{
		return m_contentFields;
	}

	public HashMap<Integer,GridField> getColumnGridFieldMap()
	{
		return columnGridFieldMap;
	}

	public JPMatrixGridRowRenderer getRowRenderer()
	{
		return renderer;
	}
}
