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

	//View Model:Map of Data Model for Display<Identifier of Row.<Column Number,data>>
	private ListModelMap<Object, Object>  viewModel;

	//Convertion Table:Connect View Model with Table Modle<Identifier of Row.<Column Number,Identifier of Data>>
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


        	//Update Context
        	GridField gridField = editor.getGridField();
    		gridField.setValue(newValue,false);


    		/*Test for Matrix Window Callout*/
//    		MatrixWindowSampleCallout test = new MatrixWindowSampleCallout();
//    		test.start(Env.getCtx(), x, columnEditorMap, columnGridFieldMap, viewModelRowData, conversionTableRowData, tableModel, dirtyModel);

        }//if (source instanceof WEditor)

    } // ValueChange

}