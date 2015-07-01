/******************************************************************************
 * Product: JPiere(ジェイピエール) - JPiere Plugins Matrix Window             *
 * Copyright (C) Hideaki Hagiwara All Rights Reserved.                        *
 * このプログラムはGNU Gneral Public Licens Version2のもと公開しています。    *
 * このプラグラムの著作権は萩原秀明(h.hagiwara@oss-erp.co.jp)が保持しており、 *
 * このプログラムを使用する場合には著作権の使用料をお支払頂く必要があります。 *
 * 著作権の使用料の支払い義務は、このプログラムから派生して作成された         *
 * プログラムにも発生します。 サポートサービスは                              *
 * 株式会社オープンソース・イーアールピー・ソリューションズで                 *
 * 提供しています。サポートをご希望の際には、                                 *
 * 株式会社オープンソース・イーアールピー・ソリューションズまでご連絡下さい。 *
 * http://www.oss-erp.co.jp/                                                  *
 *****************************************************************************/
package jpiere.plugin.matrixwindow.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import org.adempiere.util.GridRowCtx;
import org.adempiere.webui.LayoutUtils;
import org.adempiere.webui.adwindow.ADWindow;
import org.adempiere.webui.adwindow.AbstractADWindowContent;
import org.adempiere.webui.adwindow.GridTableListModel;
import org.adempiere.webui.adwindow.GridView;
import org.adempiere.webui.adwindow.IADTabpanel;
import org.adempiere.webui.apps.ProcessModalDialog;
import org.adempiere.webui.component.Button;
import org.adempiere.webui.component.Checkbox;
import org.adempiere.webui.component.Mask;
import org.adempiere.webui.component.NumberBox;
import org.adempiere.webui.component.Window;
import org.adempiere.webui.editor.WButtonEditor;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WEditorPopupMenu;
import org.adempiere.webui.editor.WebEditorFactory;
import org.adempiere.webui.event.ActionEvent;
import org.adempiere.webui.event.ActionListener;
import org.adempiere.webui.event.ContextMenuListener;
import org.adempiere.webui.panel.CustomForm;
import org.adempiere.webui.session.SessionManager;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.model.PO;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.zkoss.zk.au.out.AuScript;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelMap;
import org.zkoss.zul.Paging;
import org.zkoss.zul.RendererCtrl;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.RowRendererExt;
import org.zkoss.zul.impl.XulElement;

public class JPMatrixGridRowRenderer implements RowRenderer<Map.Entry<Integer,Object>> ,RowRendererExt, RendererCtrl,EventListener<Event>{

	public static final String GRID_ROW_INDEX_ATTR = "grid.row.index";
	private static final String CELL_DIV_STYLE = "height: 100%; cursor: pointer; ";
	private static final String CELL_DIV_STYLE_ALIGN_CENTER = CELL_DIV_STYLE + "text-align:center; ";
	private static final String CELL_DIV_STYLE_ALIGN_RIGHT = CELL_DIV_STYLE + "text-align:right; ";

	private static final int MAX_TEXT_LENGTH = 60;
	public GridTab gridTab ;
	private int windowNo;
	private JPMatrixDataBinder dataBinder;//TODO Data Binder

	public HashMap<Integer,GridField> columnGridFieldMap;

	private Map<GridField, WEditor> fieldEditorMap = new LinkedHashMap<GridField, WEditor>();

	private Paging paging;

	private RowListener rowListener;

	private Grid grid = null;
	public GridView gridView = null;

	private Row currentRow;

	private Object[] currentValues;
	private boolean editing = false;
	private int currentRowIndex = -1;
	private AbstractADWindowContent m_windowPanel;
	private IADTabpanel adTabpanel;
	private ActionListener buttonListener;

//	private ListModelMap<Object, Object> listModelMap;

	private ListModelMap<Object, Object>  viewModel;

	private ListModelMap<Object, Object> convetionTable ;

	private HashMap<Integer,PO> 	tableModel;

	private HashMap<Integer,PO> 	dirtyModel;

	private int columnsSize=0;

	private CustomForm form ;


	public JPMatrixGridRowRenderer(ListModelMap<Object, Object> viewModel
			,ListModelMap<Object, Object>  convetionTable ,HashMap<Integer,PO> tableModel, HashMap<Integer,PO> dirtyModel,int windowNo,CustomForm form) {
		this.viewModel = viewModel;
		this.convetionTable = convetionTable;
		this.tableModel = tableModel;
		this.dirtyModel = dirtyModel;
		this.windowNo = windowNo;
		this.form = form;
		this.dataBinder = new JPMatrixDataBinder(viewModel,convetionTable,tableModel,dirtyModel);
	}

	private WEditor getEditorCell(GridField gridField) {
		WEditor editor = fieldEditorMap.get(gridField);
		if (editor != null)  {
			prepareFieldEditor(gridField, editor);
			editor.addValueChangeListener(dataBinder);
			gridField.removePropertyChangeListener(editor);
			gridField.addPropertyChangeListener(editor);
			editor.setValue(gridField.getValue());
		}
		return editor;
	}

	private void prepareFieldEditor(GridField gridField, WEditor editor)
	{
		if (editor instanceof WButtonEditor)
        {
			if (buttonListener != null)
			{
				((WButtonEditor)editor).addActionListener(buttonListener);
			}
			else
			{
				Object window = SessionManager.getAppDesktop().findWindow(windowNo);
            	if (window != null && window instanceof ADWindow)
            	{
            		AbstractADWindowContent windowPanel = ((ADWindow)window).getADWindowContent();
            		((WButtonEditor)editor).addActionListener(windowPanel);
            	}
			}
        }

        //streach component to fill grid cell
		if (editor.getComponent() instanceof HtmlBasedComponent)
		{
        	editor.fillHorizontal();
		}
	}

	public int getColumnIndex(GridField field) {
//		GridField[] fields = gridPanel.getFields();
//		for(int i = 0; i < fields.length; i++) {
//			if (fields[i] == field)
//				return i;
//		}
		return 0;
	}

	private Component createReadonlyCheckbox(Object value) {
		Checkbox checkBox = new Checkbox();
		if (value != null && "true".equalsIgnoreCase(value.toString()))
			checkBox.setChecked(true);
		else
			checkBox.setChecked(false);
		checkBox.setDisabled(true);
		return checkBox;
	}


	/**
	 * call {@link #getDisplayText(Object, GridField, int, boolean)} with isForceGetValue = false
	 * @param value
	 * @param gridField
	 * @param rowIndex
	 * @return
	 */
	private String getDisplayText(Object value, GridField gridField, int rowIndex){
		return getDisplayText(value, gridField, rowIndex, false);
	}


	/**
	 * Get display text of a field. when field have isDisplay = false always return empty string, except isForceGetValue = true
	 * @param value
	 * @param gridField
	 * @param rowIndex
	 * @param isForceGetValue
	 * @return
	 */
	private String getDisplayText(Object value, GridField gridField, int rowIndex, boolean isForceGetValue)
	{
		if (value == null)
			return "";

		if (rowIndex >= 0) {
			GridRowCtx gridRowCtx = new GridRowCtx(Env.getCtx(), gridField.getGridTab(), rowIndex);
			if (!isForceGetValue && !gridField.isDisplayed(gridRowCtx, true)) {
				return "";
			}
		}

		if (gridField.isEncryptedField())
		{
			return "********";
		}
//		else if (readOnlyEditors.get(gridField) != null)
//		{
//			WEditor editor = readOnlyEditors.get(gridField);
//			return editor.getDisplayTextForGridView(value);
//		}
    	else
    		return value.toString();
	}


	/**
	 * get component to display value of a field.
	 * when display is boolean or button, return correspond component
	 * other return a label with text get from {@link #getDisplayText(Object, GridField, int, boolean)}
	 * @param rowIndex
	 * @param value
	 * @param gridField
	 * @param isForceGetValue
	 * @return
	 */
	private Component getDisplayComponent(int rowIndex, Object value, GridField gridField, boolean isForceGetValue) {
		Component component;
		if (gridField.getDisplayType() == DisplayType.YesNo) {
			component = createReadonlyCheckbox(value);
		} else if (gridField.getDisplayType() == DisplayType.Button) {
			GridRowCtx gridRowCtx = new GridRowCtx(Env.getCtx(), gridTab, rowIndex);
			WButtonEditor editor = new WButtonEditor(gridField, rowIndex);
			editor.setValue(gridTab.getValue(rowIndex, gridField.getColumnName()));
			editor.setReadWrite(gridField.isEditable(gridRowCtx, true,true));
			editor.getComponent().setAttribute(GRID_ROW_INDEX_ATTR, rowIndex);
			editor.addActionListener(buttonListener);
			component = editor.getComponent();
		} else {
			String text = getDisplayText(value, gridField, rowIndex, isForceGetValue);

			Label label = new Label();
			setLabelText(text, label);

			component = label;
		}
		return component;
	}









	private void setLabelText(String text, Label label) {
		String display = text;
		if (text != null && text.length() > MAX_TEXT_LENGTH)
			display = text.substring(0, MAX_TEXT_LENGTH - 3) + "...";
		// since 5.0.8, the org.zkoss.zhtml.Text is encoded by default
//		if (display != null)
//			display = XMLs.encodeText(display);
		label.setValue(display);
		if (text != null && text.length() > MAX_TEXT_LENGTH)
			label.setTooltiptext(text);
	}

	static class RowListener implements EventListener<Event> {

		private Grid _grid;

		private int currentRowNo;
		private Integer currentColumnNo;

		public int getY()
		{
			return currentRowNo;
		}

		public int getX()
		{
			return currentColumnNo.intValue();
		}


		public RowListener(Grid grid) {
			_grid = grid;
		}

		public void onEvent(Event event) throws Exception {
			String name = event.getName();
			Object object = event.getData();
			Component comp = event.getTarget();

			if(comp instanceof Cell)
			{
				Cell cell = (Cell)comp;
				Object component = cell.getAttribute("display.component");

				String cellID = cell.getId();			//TODO:
				Row row = (Row)cell.getParent();
				currentRowNo = row.getIndex();//IndexはRowの識別番号(Y)と一緒のはず
//				String x = cellID.substring(String.valueOf(currentRowNo).length()+1);
				currentColumnNo = Integer.valueOf(cellID.substring(String.valueOf(currentRowNo).length()+1));
			}


			if (Events.ON_CLICK.equals(event.getName())) {
				if (Executions.getCurrent().getAttribute("gridView.onSelectRow") != null)
					return;
				Event evt = new Event(Events.ON_CLICK, _grid, event.getTarget());
				Events.sendEvent(_grid, evt);
				evt.stopPropagation();
			}
			else if (Events.ON_DOUBLE_CLICK.equals(event.getName())) {
				Event evt = new Event(Events.ON_DOUBLE_CLICK, _grid, _grid);
				Events.sendEvent(_grid, evt);
			}
			else if (Events.ON_OK.equals(event.getName())) {
				Event evt = new Event(Events.ON_OK, _grid, _grid);
				Events.sendEvent(_grid, evt);
			}
		}
	}


	@Override
	public void render(Row row, Map.Entry<Integer,Object> dataEntry, int rowIndex) throws Exception
	{
		//render()メソッドはrow単位で呼び出されます。つまりRowの行数だけ呼び出されます。

		@SuppressWarnings("unchecked")
		TreeMap<Integer,Object> data = (TreeMap<Integer,Object>)dataEntry.getValue();

		//TODO なぜか、ある時から１つだけComponentがrowに追加されている状態になって。要原因調査。
		List<Component> comps = row.getChildren();
		int size = comps.size();
		if(size > 0)
		{
			comps.remove(0);
		}

		if (grid == null)
			grid = (Grid) row.getParent().getParent();

		if (rowListener == null)
			rowListener = new RowListener((Grid)row.getParent().getParent());



		for (int i = 0; i < columnsSize; i++)
		{

			Cell div = new Cell();
			String divStyle = CELL_DIV_STYLE;
			if (data.get(i)!=null )
			{
				/*******************************************************************************
				 * TODO:この段階でWEditorを作ってしまっているが、別途イベント処理で、編集している行だけ
				 * TODO:作るようにするなどすれば、パフォーマンスが向上すると思われる
				 ********************************************************************************/

				WEditor editor = WebEditorFactory.getEditor(columnGridFieldMap.get(i), true);
				if (editor instanceof WButtonEditor) {
					((WButtonEditor)editor).addActionListener(buttonListener);
					((WButtonEditor)editor).setADTabpanel(adTabpanel);
					Object  aaaa = convetionTable.get(data.get(0));
					TreeMap<Integer,Object> bbb = new TreeMap<Integer,Object>();
					if(aaaa instanceof TreeMap<?,?>)
					{
						bbb = (TreeMap<Integer,Object>)aaaa;
						editor.setValue(bbb.get(i));
					}

				}else{
					editor.setValue(data.get(i));
				}

				editor.getComponent().setId(String.valueOf(row.getIndex())+"_"+String.valueOf(i));
				if(columnGridFieldMap.get(i).isReadOnly() || i==0)
				{
					editor.setReadWrite(false);
					div.appendChild(new Label(editor.getDisplay()));//WEDitorを経由して、文字列にする事で、表示形式をiDempiereのエンジン側で処理してもらえる。

					//TODO:数値の場合は、右寄せにする処理をする。必要であれば、Editorによりスタイルを変える記述を追加する。
					if(editor.getComponent() instanceof NumberBox)
					{
						div.setStyle(CELL_DIV_STYLE_ALIGN_RIGHT);
					}//


				}else{

					editor.addValueChangeListener(dataBinder);
					fieldEditorMap.put(columnGridFieldMap.get(i), editor);//編集するフィールドだけWEditorのMapを作成する。
					Component component = getCellComponent(rowIndex, data.get(i), columnGridFieldMap.get(i), false);
					div.appendChild(editor.getComponent());
//					editor.setValue(data.get(i));
					div.setAttribute("display.component", component);
					div.setId(String.valueOf(row.getIndex())+"_"+String.valueOf(i));//CellのY軸とX軸の設定

					//スタイルの設定
					div.setStyle(divStyle);
					div.setWidth("100%");
					div.setAttribute("columnName", columnGridFieldMap.get(i).getColumnName());
//					div.addEventListener(Events.ON_CLICK, rowListener);
//					div.addEventListener(Events.ON_DOUBLE_CLICK, rowListener);
				}
//				div.addEventListener(Events.ON_CLICK, rowListener);
				row.appendChild(div);

			}else{//データが無い場合の処理

				Component component = new Label("");
				div.appendChild(component);
				div.setAttribute("display.component", component);
				div.setStyle(divStyle);
				div.setWidth("100%");
				div.setAttribute("columnName", columnGridFieldMap.get(i).getColumnName());
				row.appendChild(div);

			}//if
		}//for
		return;
	}

	public void setcColumnsSize(int size)
	{
		columnsSize = size;
	}

	private Component getCellComponent(int rowIndex ,Object value, GridField gridField, boolean isGridViewCustomized)
	{

		Component component ;
		if (gridField.getDisplayType() == DisplayType.YesNo) {
			component = createReadonlyCheckbox(value);
		} else if (gridField.getDisplayType() == DisplayType.Button) {
			GridRowCtx gridRowCtx = new GridRowCtx(Env.getCtx(), gridTab, rowIndex);
			WButtonEditor editor = new WButtonEditor(gridField, rowIndex);
			editor.setValue(gridTab.getValue(rowIndex, gridField.getColumnName()));
			editor.setReadWrite(gridField.isEditable(gridRowCtx, true,true));
			editor.getComponent().setAttribute(GRID_ROW_INDEX_ATTR, rowIndex);
			editor.addActionListener(buttonListener);
			component = editor.getComponent();
		} else {
			String text = getDisplayText(value, gridField, rowIndex, isGridViewCustomized);

			Label label = new Label();
			setLabelText(text, label);

			component = label;
		}
		return component;
	}



	@Override
	public void onEvent(Event event) throws Exception {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void doTry() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void doCatch(Throwable ex) throws Throwable {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void doFinally() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public Row newRow(Grid grid) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Component newCell(Row row) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public int getControls() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}


	/**
	 * Detach all editor and optionally set the current value of the editor as cell label.
	 * @param updateCellLabel
	 */
	public void stopEditing(boolean updateCellLabel) {
		if (!editing) {
			return;
		} else {
			editing = false;
		}
		Row row = null;
		for (Entry<GridField, WEditor> entry : fieldEditorMap.entrySet()) {
			if (entry.getValue().getComponent().getParent() != null) {
				Component child = entry.getValue().getComponent();
				Cell div = null;
				while (div == null && child != null) {
					Component parent = child.getParent();
					if (parent instanceof Cell && parent.getParent() instanceof Row)
						div = (Cell)parent;
					else
						child = parent;
				}
				Component component = div!=null ? (Component) div.getAttribute("display.component") : null;
				if (updateCellLabel) {
					if (component instanceof Label) {
						Label label = (Label)component;
						label.getChildren().clear();
						String text = getDisplayText(entry.getValue().getValue(), entry.getValue().getGridField(), -1);
						setLabelText(text, label);
					} else if (component instanceof Checkbox) {
						Checkbox checkBox = (Checkbox)component;
						Object value = entry.getValue().getValue();
						if (value != null && "true".equalsIgnoreCase(value.toString()))
							checkBox.setChecked(true);
						else
							checkBox.setChecked(false);
					}
				}
				if (row == null)
					row = ((Row)div.getParent());

				entry.getValue().getComponent().detach();
				entry.getKey().removePropertyChangeListener(entry.getValue());
				entry.getValue().removeValuechangeListener(dataBinder);

				if (component.getParent() == null || component.getParent() != div)
					div.appendChild(component);
				else if (!component.isVisible()) {
					component.setVisible(true);
				}
			}
		}

		GridTableListModel model = (GridTableListModel) grid.getModel();
		model.setEditing(false);
	}


	/**
	 * @param row
	 */
	public void setCurrentRow(Row row) {
		if (currentRow != null && currentRow.getParent() != null && currentRow != row) {
			Cell cell = (Cell) currentRow.getChildren().get(1);
			if (cell != null) {
				cell.setSclass("row-indicator");
			}
		}
		currentRow = row;
		Cell cell = (Cell) currentRow.getChildren().get(1);
		if (cell != null) {
			cell.setSclass("row-indicator-selected");
		}
		currentRowIndex = gridTab.getCurrentRow();

		if (currentRowIndex == gridTab.getCurrentRow()) {
			if (editing) {
				stopEditing(false);
				editCurrentRow();
			}
		} else {
			currentRowIndex = gridTab.getCurrentRow();
			if (editing) {
				stopEditing(false);
			}
		}

		String script = "jq('#"+row.getUuid()+"').addClass('highlight').siblings().removeClass('highlight')";

		Boolean isActive = null;
		Object isActiveValue = gridTab.getValue(currentRowIndex, "IsActive");
		if (isActiveValue != null) {
			if ("true".equalsIgnoreCase(isActiveValue.toString())) {
				isActive = Boolean.TRUE;
			} else {
				isActive = Boolean.FALSE;
			}
		}
		if (isActive != null && !isActive.booleanValue()) {
			script = "jq('#"+row.getUuid()+"').addClass('grid-inactive-row').siblings().removeClass('highlight')";
		}

		Clients.response(new AuScript(script));
	}

	/**
	 * Enter edit mode
	 */
	public void editCurrentRow() {
		if (currentRow != null && currentRow.getParent() != null && currentRow.isVisible()
			&& grid != null && grid.isVisible() && grid.getParent() != null && grid.getParent().isVisible()) {

			org.zkoss.zul.Columns columns = grid.getColumns();
			//skip selection and indicator column
			int colIndex = 1;
			for (int i = 0; i < columnGridFieldMap.size(); i++) {
				GridField gridFeld = columnGridFieldMap.get(i);

//				if ((!isGridViewCustomized && !gridPanelFields[i].isDisplayedGrid()) || gridPanelFields[i].isToolbarOnlyButton()) {
//					continue;
//				}
				colIndex ++;

				if (fieldEditorMap.get(gridFeld) == null)
					fieldEditorMap.put(gridFeld, WebEditorFactory.getEditor(gridFeld, true));

				org.zkoss.zul.Column column = (org.zkoss.zul.Column) columns.getChildren().get(colIndex);
				if (column.isVisible()) {
					Cell div = (Cell) currentRow.getChildren().get(colIndex);
					WEditor editor = getEditorCell(gridFeld);
					if (div.getChildren().isEmpty() || !(div.getChildren().get(0) instanceof Button))
						div.getChildren().clear();
					else if (!div.getChildren().isEmpty()) {
						div.getChildren().get(0).setVisible(false);
					}
					div.appendChild(editor.getComponent());
					WEditorPopupMenu popupMenu = editor.getPopupMenu();

		            if (popupMenu != null)
		            {
		            	popupMenu.addMenuListener((ContextMenuListener)editor);
		            	div.appendChild(popupMenu);
		            	popupMenu.addContextElement((XulElement) editor.getComponent());
		            }


		            Properties ctx = gridFeld.getVO().ctx;
		            //check context
					if (!gridFeld.isDisplayed(ctx, true)){
						// IDEMPIERE-2253
						div.removeChild(editor.getComponent());
					}

					editor.setReadWrite(gridFeld.isEditableGrid(true));
				}
			}
			editing = true;

			GridTableListModel model = (GridTableListModel) grid.getModel();
			model.setEditing(true);

		}
	}


	/**
	 * @return Row
	 */
	public Row getCurrentRow() {
		return currentRow;
	}

	/**
	 * @return boolean
	 */
	public boolean isEditing() {
		return editing;
	}

	/**
	 *
	 * @return active editor list
	 */
	public List<WEditor> getEditors() {
		List<WEditor> editorList = new ArrayList<WEditor>();
		if (!fieldEditorMap.isEmpty())
			editorList.addAll(fieldEditorMap.values());

		return editorList;
	}

	/**
	 * set focus to first active editor
	 */
	public void focusToFirstEditor()
	{
		if (currentRow != null && currentRow.getParent() != null)
		{
			WEditor toFocus = null;
			WEditor firstEditor = null;
//			if (defaultFocusField != null
//					&& defaultFocusField.isVisible() && defaultFocusField.isReadWrite() && defaultFocusField.getComponent().getParent() != null
//					&& !(defaultFocusField instanceof WImageEditor)) {
//				toFocus = defaultFocusField;
//			}
//			else
//			{
				for (WEditor editor : getEditors()) {
					if (editor.isVisible() && editor.getComponent().getParent() != null) {
						if (editor.isReadWrite()) {
							toFocus = editor;
							break;
						}
						if (firstEditor == null)
							firstEditor = editor;
					}
				}
//			}
//			if (toFocus != null) {
//				focusToEditor(toFocus);
//			} else if (firstEditor != null) {
//				focusToEditor(firstEditor);
//			}
		}//if
	}

	/**
	 * @param windowPanel
	 */
	public void setADWindowPanel(AbstractADWindowContent windowPanel,IADTabpanel adTabpanel) {
		if (this.m_windowPanel == windowPanel)
			return;

		this.m_windowPanel = windowPanel;
		this.adTabpanel = adTabpanel;

		buttonListener = new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				WButtonEditor editor = (WButtonEditor) event.getSource();
				String stringRecord_ID = editor.getDisplay();//valueの取得

				ProcessModalDialog dialog = new ProcessModalDialog(windowNo, editor.getProcess_ID(), 0, Integer.parseInt(stringRecord_ID), false);


				if (dialog.isValid())
				{
					//dialog.setWidth("500px");
					dialog.setBorder("normal");
					form.getParent().appendChild(dialog);
//					showBusyMask(dialog);
					LayoutUtils.openOverlappedWindow(form.getParent(), dialog, "middle_center");
					dialog.focus();
				}
				else
				{
//					onRefresh(true, false);
				}

			}
		};
	}

	public void showBusyMask(Window window) {
		form.getParent().appendChild(getMask());
		StringBuilder script = new StringBuilder("var w=zk.Widget.$('#");
		script.append(form.getParent().getUuid()).append("');");
		if (window != null) {
			script.append("var d=zk.Widget.$('#").append(window.getUuid()).append("');w.busy=d;");
		} else {
			script.append("w.busy=true;");
		}
		Clients.response(new AuScript(script.toString()));
	}

	private Div mask;

	private Div getMask() {
		if (mask == null) {
			mask = new Mask();
		}
		return mask;
	}


}
