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

import java.util.HashMap;
import java.util.TreeMap;

import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WTableDirEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
import org.adempiere.webui.panel.CustomForm;
import org.compiere.model.GridField;
import org.compiere.model.PO;
import org.compiere.util.CLogger;
import org.zkoss.zul.ListModelMap;

/**
 * Transfer data from editor to GridTab
 *
 * JPIERE-0098
 *
 * @author hengsin
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class JPMatrixDataBinder implements ValueChangeListener {

	private final static CLogger logger = CLogger.getCLogger(JPMatrixDataBinder.class);

	private ListModelMap<Object, Object>  viewModel;

	private ListModelMap<Object, Object>  convetionTable ;

	private HashMap<Integer,PO> 	tableModel;

	private HashMap<Integer,PO> 	dirtyModel;

	private HashMap<Integer,GridField> columnGridFieldMap;

	private HashMap<Integer,WEditor>   columnEditorMap;

	private CustomForm form;

	/**
	 *
	 * @param gridTab
	 */
	public JPMatrixDataBinder(ListModelMap<Object, Object> listModel
			,ListModelMap<Object, Object>  convetionTable ,HashMap<Integer,PO> tableModel,HashMap<Integer,PO> dirtyModel,CustomForm form)
	{
		this.viewModel = listModel;
		this.convetionTable = convetionTable;
		this.tableModel = tableModel;
		this.dirtyModel = dirtyModel;
		this.form = form;
	}

	public void setColumnGridFieldMap(HashMap<Integer,GridField> columnGridFieldMap)
	{
		this.columnGridFieldMap = columnGridFieldMap;
	}

	public void setColumnEditorMap(HashMap<Integer,WEditor>  columnEditorMap)
	{
		this.columnEditorMap = columnEditorMap;
	}



	/**
	 * @param e
	 */
	public void valueChange(ValueChangeEvent e)
    {

		Object newValue = e.getNewValue();

        Object source = e.getSource();
        if (source instanceof WEditor)
        {
        	//Get Row(Y) and Column(X) info
        	WEditor editor = (WEditor) source;
        	String[] yx = editor.getComponent().getId().split("_");
            	int y =Integer.valueOf(yx[0]);
            	int x =Integer.valueOf(yx[1]);

            /** update ViewModel that is used JPMatrixGridRowRenderer.editRow() method**/
        	ListModelMap.Entry<Object, Object>  viewModelRow = viewModel.getElementAt(y);
        	@SuppressWarnings("unchecked")
			TreeMap<Integer,Object> viewModelRowData = (TreeMap<Integer,Object>)viewModelRow.getValue();
        	viewModelRowData.put(x, newValue);

        	//Get Po's ID form convetionTable
        	ListModelMap.Entry<Object, Object>  conversionTableRow = convetionTable.getElementAt(y);

        	@SuppressWarnings("unchecked")
			TreeMap<Integer,Object> conversionTableRowData = (TreeMap<Integer,Object>)conversionTableRow.getValue();
        	Object PO_ID = conversionTableRowData.get(x);

        	//Update Table Model
        	PO po = tableModel.get(PO_ID);
        	po.set_ValueNoCheck(editor.getColumnName(), newValue);

        	//Add DirtyModel for Save.
        	dirtyModel.put((Integer)PO_ID, po);


        	GridField gridField = editor.getGridField();


        	/**Callout memo start

        	int xTest = 1;
        	Object valueTest = 9999;
        	//for display(Editor):Editorに数値を正しく表示するために設定する
        	WEditor editorTest = columnEditorMap.get(xTest);
        	editorTest.setValue(valueTest);

        	//for display(text):Editorが外れた時に正しく表示するために設定する
        	viewModelRowData.put(xTest,new BigDecimal(valueTest.toString()));

        	//for update context:Contextに正しく表示するためにGridGieldに値を設定する
        	columnGridFieldMap.get(xTest).setValue(valueTest, false);

        	int aaaa = columnGridFieldMap.get(1).getGridTab().getTabNo();
        	int bbbb = columnGridFieldMap.get(5).getGridTab().getTabNo();
        	int cccc = columnGridFieldMap.get(10).getGridTab().getTabNo();

        	//for save：保存するためにdirtyModelに値を設定する
        	Object poTest_ID = conversionTableRowData.get(xTest);
        	PO poTest = tableModel.get(poTest_ID);
        	poTest.set_ValueNoCheck(editorTest.getColumnName(), new BigDecimal(valueTest.toString()));
        	dirtyModel.put((Integer)poTest_ID, poTest);

        	//Matrix WindowのCalloutに必要な情報:x, columnEditorMap, columnGridFieldMap,viewModelRowData,conversionTableRowData,tableModel,dirtyModel

        	Callout memo finish***/


        	if(gridField != null)
        	{

        		gridField.setValue(newValue,false);

        		//TODO:ここのリフレッシュは不要ではないか？→リフレッシュさせる事で、依存するフィールドの内容もリフレッシュさせている様子…要調査。
        		if(editor instanceof WTableDirEditor)
        			((WTableDirEditor)editor).getLookup().refresh();

        		if(!gridField.isEditable(true))
        		{
//            		if (logger.isLoggable(Level.CONFIG)) logger.config("(" + gridTab.toString() + ") " + e.getPropertyName());
        			return;
        		}
        	}
        	else if(!editor.isReadWrite())
        	{
//            	if (logger.isLoggable(Level.CONFIG)) logger.config("(" + gridTab.toString() + ") " + e.getPropertyName());
        		return;
        	}
        }
        else
        {
//          if (logger.isLoggable(Level.CONFIG)) logger.config("(" + gridTab.toString() + ") " + e.getPropertyName());
            return;
        }


    } // ValueChange
}