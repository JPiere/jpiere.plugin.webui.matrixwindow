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
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import jpiere.plugin.matrixwindow.model.MMatrixField;
import jpiere.plugin.matrixwindow.model.MMatrixSearch;
import jpiere.plugin.matrixwindow.model.MMatrixWindow;

import org.adempiere.base.IModelFactory;
import org.adempiere.base.Service;
import org.adempiere.webui.AdempiereWebUI;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.adwindow.ADTabpanel;
import org.adempiere.webui.adwindow.ADWindow;
import org.adempiere.webui.adwindow.ADWindowContent;
import org.adempiere.webui.adwindow.GridView;
import org.adempiere.webui.adwindow.IADTabbox;
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
import org.adempiere.webui.window.FDialog;
import org.compiere.model.GridField;
import org.compiere.model.GridFieldVO;
import org.compiere.model.GridTab;
import org.compiere.model.GridTabVO;
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
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Trx;
import org.compiere.util.TrxRunnable;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Center;
import org.zkoss.zul.Frozen;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Menuitem;
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


	//Create Map of PO per column of x-axis:LinkedHashMap<Key of Column info,LinkedHashMap<Key of Row info,PO>>
	private LinkedHashMap<Object,LinkedHashMap<Object,PO>> keyColumnModel = new LinkedHashMap<Object,LinkedHashMap<Object,PO>>();


	/*Information of key of Vertical axis and key of Horizontal axis*/
	/*You need to unique constraint that contains key of vertical axis and key of horizontal axis */

	//List of Column Key(Key of Column info)
	private ArrayList<Object> columnKeys = new ArrayList<Object>();
	 //Map of Column key and Column name <key column, column name>
	private HashMap<Object,String> columnKeyNameMap = new HashMap<Object,String>();

	//Map of Column key and Virtual GridTab
	private LinkedHashMap<Object,GridTab> virtualTabMap = new LinkedHashMap<Object,GridTab>();

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
	private ADTabpanel adTabpanel;
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


		AD_WINDOW_ID = m_matrixWindow.getAD_Window_ID();
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
				adTabpanel =(ADTabpanel)adTabbox.getADTabpanel(i);
			}
		}
		if(adTabpanel == null)
		{
			;//Error
		}
		gridTab = adTabpanel.getGridTab();
		gridView = adTabpanel.getGridView();
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

		/*Main Layout(Borderlayout)*/
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");

		//Main Layout(Borderlayout)-North
		North north = new North();
		mainLayout.appendChild(north);

		//Search Parameter Panel
		north.appendChild(parameterPanel);
		north.setStyle("border: none");
		parameterPanel.appendChild(parameterLayout); 		//parameterLayout = Grid
		parameterLayout.setWidth("100%");
		Rows parameterLayoutRows = parameterLayout.newRows();
		Row row = null;
		if(m_matrixSearches.length > 0)
		{
			row = parameterLayoutRows.newRow();
				Groupbox searchGB = new Groupbox();
				row.appendCellChild(searchGB,8);
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
					;//TODO エラー
					
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
					}

					editor.setMandatory(m_matrixSearches[i].isMandatory());

					//positioning
					row.appendCellChild(editor.getLabel().rightAlign(),1);
					actualxpos = actualxpos + 1;
					row.appendCellChild(editor.getComponent(),searchField.getColumnSpan());
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
							if(image.endsWith("Zoom16.png")||image.endsWith("Refresh16.png")
									|| image.endsWith("New16.png") || image.endsWith("InfoBPartner16.png"))
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
//					String tableName = m_matrixSearches[i].getAD_Tab().getAD_Table().getTableName();
					searchEditorMap.put(editor.getColumnName(), editor);
				
				}
				
				
				
//				for(int j = 0; j < gridFields.length; j++)
//				{
//					if(m_matrixSearches[i].getAD_Field_ID() == gridFields[j].getAD_Field_ID())
//					{
//						WEditor editor = WebEditorFactory.getEditor(gridFields[j], false);
//						String DefaultValue = m_matrixSearches[i].getDefaultValue();
//						if(DefaultValue == null || DefaultValue.isEmpty())
//						{
//							;
//						}else{
//
//							String value = Env.parseContext(Env.getCtx(), form.getWindowNo(), DefaultValue, false);
//							Env.setContext(Env.getCtx(), form.getWindowNo(), editor.getColumnName(), value);
//							editor.setValue(Env.parseContext(Env.getCtx(), form.getWindowNo(), DefaultValue, false));
//
//							if(editor instanceof WTableDirEditor)
//							{
//								((WTableDirEditor) editor).actionRefresh();
//								((WTableDirEditor) editor).getLookup().setSelectedItem("");
//							}
//
//						}
//
//						if(!editor.isReadWrite())
//						{
//							editor.setReadWrite(true);
//							if(editor instanceof WTableDirEditor)
//								((WTableDirEditor) editor).actionRefresh();
//
//						}
//
//						//Set zoom
//						if(editor instanceof WSearchEditor
//								|| editor instanceof WTableDirEditor)
//						{
//							editor.getLabel().addEventListener(Events.ON_CLICK, new ZoomListener((IZoomableEditor) editor));
//							if(m_matrixSearches[i].isMandatory() && editor.getValue()==null)
//								editor.getLabel().setStyle("cursor: pointer; text-decoration: underline;color: #333; color:red;");
//							else
//								editor.getLabel().setStyle("cursor: pointer; text-decoration: underline;color: #333;");
//						}
//
//						editor.setMandatory(m_matrixSearches[i].isMandatory());
//
//						//positioning
//						row.appendCellChild(editor.getLabel().rightAlign(),1);
//						actualxpos = actualxpos + 1;
//						row.appendCellChild(editor.getComponent(),field.getColumnSpan());
//						actualxpos = actualxpos + field.getColumnSpan();
//
//						//Popup Menu
//						WEditorPopupMenu  popupMenu = editor.getPopupMenu();
//						List<Component> listcomp = popupMenu.getChildren();
//						Menuitem menuItem = null;
//						String image = null;
//						for(Component comp : listcomp)
//						{
//							if(comp instanceof Menuitem)
//							{
//								menuItem = (Menuitem)comp;
//								image = menuItem.getImage();
//								if(image.endsWith("Zoom16.png")||image.endsWith("Refresh16.png")
//										|| image.endsWith("New16.png") || image.endsWith("InfoBPartner16.png"))
//								{
//									menuItem.setVisible(true);
//								}else{
//									menuItem.setVisible(false);
//								}
//							}
//						}//for
//
//			            if (popupMenu != null)
//			            {
//			            	popupMenu.addMenuListener((ContextMenuListener)editor);
//			            	row.appendChild(popupMenu);
//
//			            	popupMenu.addContextElement((XulElement) editor.getComponent());
//			            }
//
//						editor.addValueChangeListener(this);
//						searchEditorMap.put(editor.getColumnName(), editor);
//						break;
//					}
//				}//for j
				
				
			}//for i
		}//if


		//Create Button
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

				if(!editMode.equals(EDITMODE_READ))
					row.appendCellChild(SaveButton);

				CreateButton = new Button(Msg.getMsg(Env.getCtx(), "NewRecord"));
				CreateButton.setId("CreateButton");
				CreateButton.addActionListener(this);
				CreateButton.setEnabled(false);
				CreateButton.setImage(ThemeManager.getThemeResource("images/New16.png"));
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
				ProcessButton.setImage(ThemeManager.getThemeResource("images/Process16.png"));
				if(toolbarProcessButtons.size()> 0 && !editMode.equals(EDITMODE_READ))
					row.appendCellChild(ProcessButton);


		//for space under Button
		row = parameterLayoutRows.newRow();
				row.appendCellChild(new Space(),1);

		//Edit Area
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
		String pName = e.getPropertyName();
		Object obj = e.getSource();
		String string = e.toString();
		
		searchEditorMap.get(e.getPropertyName()).setValue(e.getNewValue());

		if(searchEditorMap.get(e.getPropertyName()) instanceof WYesNoEditor)
		{
			Env.setContext(Env.getCtx(), form.getWindowNo(), searchEditorMap.get(e.getPropertyName()).getColumnName(), e.getNewValue().equals("true") ? "Y" : "N");
		}else{
			Env.setContext(Env.getCtx(), form.getWindowNo(), searchEditorMap.get(e.getPropertyName()).getColumnName(), e.getNewValue()==null ? null : e.getNewValue().toString());
		}

		SearchButton.setEnabled(true);
		SaveButton.setEnabled(false);
		CreateButton.setEnabled(false);
		ProcessButton.setEnabled(false);

		quickEntry = null;

		matrixGrid.setVisible(false);

		if(e.getNewValue()==null && searchEditorMap.get(e.getPropertyName()).isMandatory())
		{
			searchEditorMap.get(e.getPropertyName()).getLabel().setStyle("cursor: pointer; text-decoration: underline;color: #333; color:red;");
		}else{
			searchEditorMap.get(e.getPropertyName()).getLabel().setStyle("cursor: pointer; text-decoration: underline;color: #333; ");
		}
	}



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
			if (data != null && data instanceof Component)
			{
				AbstractComponent cmp = (AbstractComponent) data;
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
				matrixGrid.setVisible(false);
				throw new Exception(message.toString());
			}

			SearchButton.setEnabled(false);
			SaveButton.setEnabled(true);
			CreateButton.setEnabled(true);
			ProcessButton.setEnabled(true);

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
				matrixGrid.setVisible(false);
				if(e.getTarget().equals(SearchButton))
					throw new Exception(Msg.getMsg(Env.getCtx(), "NotFound"));
				else
					return;
			}


			SearchButton.setEnabled(false);
			SaveButton.setEnabled(true);
			CreateButton.setEnabled(true);
			ProcessButton.setEnabled(true);

		}else if(e.getTarget().equals(SaveButton)){

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

			//Create Quick entry window
			quickEntry = new JPiereMatrixWindowQuickEntry (form.getWindowNo(), m_matrixWindow.getJP_QuickEntryWindow_ID(), this);
			quickEntry.loadRecord (0);
			List<WEditor> editors = quickEntry.getQuickEditors();


			String  JP_QuickEntryConf = m_matrixWindow.getJP_QuickEntryConf();
			for(WEditor editor : editors)
			{
				//Search Field Value can not update.Search Field Value is read only
				for(Map.Entry<String, WEditor> entry: searchEditorMap.entrySet())
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

			LayoutUtils.openPopupWindow(ProcessButton, popup, "after_start");
		}

	}//onEvent()

	private void loadToolbarButtons() {
		//get extra toolbar process buttons
        MToolBarButton[] mToolbarButtons = MToolBarButton.getProcessButtonOfTab(gridTab.getAD_Tab_ID(), null);
        for(MToolBarButton mToolbarButton : mToolbarButtons) {
        	Boolean access = MRole.getDefault().getProcessAccess(mToolbarButton.getAD_Process_ID());
        	if (access != null && access.booleanValue()) {
        		ToolbarProcessButton toolbarProcessButton = new ToolbarProcessButton(mToolbarButton, adTabpanel, this, form.getWindowNo());
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
		if(message.length() > 0)
			return false;

		//Create Column key info from where clause
		columnKeys = createColumnKeys(whereClause);
		if(columnKeys.size()==0)
		{
			message.append(System.getProperty("line.separator") + Msg.getMsg(Env.getCtx(), "not.found"));
			return false;
		}

		virtualTabMap = createVirtualTabMap(columnKeys);
		if(virtualTabMap == null || virtualTabMap.size() == 0)
		{
			message.append(System.getProperty("line.separator") + Msg.getMsg(Env.getCtx(), "not.found"));
			return false;
		}

		//Create Row key info from where clause
		rowKeys = createRowKeys(whereClause);
		if(rowKeys.size()==0)
		{
			message.append(System.getProperty("line.separator") + Msg.getMsg(Env.getCtx(), "not.found"));
			return false;
		}

		//Create array of PO from where clause
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
		renderer.setGridView(gridView);
		renderer.setGridTab(gridTab);
		renderer.setColumnGridFieldMap(columnGridFieldMap);
		renderer.setADWindowPanel(adWindowContent,adTabpanel);

		matrixGrid.setRowRenderer(renderer);
		matrixGrid.addEventListener(Events.ON_CLICK, this);

		return true;

	}


	private String createWhere()
	{
		StringBuilder whereClause = new StringBuilder(" WHERE "+ TABLE_NAME+".AD_Client_ID = "+ Env.getAD_Client_ID(Env.getCtx()));

		for(Map.Entry<String, WEditor> entry: searchEditorMap.entrySet())
		{
			if(entry.getValue().getValue()!=null)
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
			whereClause.append(" AND " + m_matrixWindow.getWhereClause() );
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


	private LinkedHashMap<Object,GridTab> createVirtualTabMap(ArrayList<Object> columnKeys)//TODO
	{
		GridWindowVO gridWindowVO = GridWindowVO.create(Env.getCtx(), adWindowContent.getWindowNo(), AD_WINDOW_ID);
		virtualTabMap = new LinkedHashMap<Object,GridTab>();

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM AD_Tab_vt WHERE AD_Tab_ID=? ";
		pstmt = DB.prepareStatement(sql, null);
		try
		{
			for(int i = 0; i < columnKeys.size(); i++)
			{
				pstmt.setInt(1, gridTab.getAD_Tab_ID());
				rs = pstmt.executeQuery();

				while (rs.next())
				{
					//  Create TabVO
					GridTabVO gridTabVO = GridTabVO.create(gridWindowVO, i, rs,
							editMode.equals(EDITMODE_READ),  //  isRO
							true);   //  onlyCurrentRows
					if (gridTabVO != null)
					{
						GridTab gtab = new GridTab(gridTabVO, gridTab.getGridWindow());
						virtualTabMap.put(columnKeys.get(i), gtab);
					}else{
						break;
					}
				}//while
			}//for

		}
		catch (SQLException e)
		{
			CLogger.get().log(Level.SEVERE, "createTabs", e);
			return null;
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null; pstmt = null;
		}


		return virtualTabMap;
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


	public PO[] getPOs (String whereClause,boolean reload)
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
	}	//	getLocations


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
	 * (縦軸となるKey Column毎のPOのMAPを作成しいます。)
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

			GridTab gtab =virtualTabMap.get(columnKeys.get(i));
			gtab.initTab(false);
			GridField[] gFields = gtab.getFields();

			for(int j = 0; j < m_contentFields.length; j++)//TODO
			{
				columnNameMap.put(c, Msg.getElement(Env.getCtx(), m_contentColumns[j].getColumnName()));
				columnLengthMap.put(c, m_matrixFields[j].getFieldLength());
				columnSummarizedMap.put(c, m_matrixFields[j].isSummarized());
				for(int k = 0; k < gFields.length; k++)
				{
					if(m_contentFields[j].getAD_Field_ID()==gFields[k].getAD_Field_ID())
						columnGridFieldMap.put(c, gFields[k]);
				}//k
				c++;
			}//j

//			for(int j = 0; j < m_contentFields.length; j++)
//			{
//				columnNameMap.put(c, Msg.getElement(Env.getCtx(), m_contentColumns[j].getColumnName()));
//				columnLengthMap.put(c, m_matrixFields[j].getFieldLength());
//				columnSummarizedMap.put(c, m_matrixFields[j].isSummarized());
//				for(int k = 0; k < gridFields.length; k++)
//				{
//					if(m_contentFields[j].getAD_Field_ID()==gridFields[k].getAD_Field_ID())
//						columnGridFieldMap.put(c, gridFields[k]);
//				}//k
//				c++;
//			}//j

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

					updateColumn();

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
;

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



}
