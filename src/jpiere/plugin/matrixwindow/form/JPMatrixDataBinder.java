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
import java.util.Properties;
import java.util.TreeMap;

import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.event.ValueChangeEvent;
import org.adempiere.webui.event.ValueChangeListener;
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

	/**
	 *
	 * @param gridTab
	 */
	public JPMatrixDataBinder(ListModelMap<Object, Object> listModel
			,ListModelMap<Object, Object>  convetionTable ,HashMap<Integer,PO> tableModel,HashMap<Integer,PO> dirtyModel)
	{
		this.viewModel = listModel;
		this.convetionTable = convetionTable;
		this.tableModel = tableModel;
		this.dirtyModel = dirtyModel;
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
;
            //Update viewModel	TODO:処理的に考えるとviewModelの更新は現段階では不要な気がする…。コンポ―ネントをSave()前に更新する場合には必要になると思う…。
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

        	if(gridField != null)
        	{
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

	/**************************************************************************
	 * Save Multiple records - Clone a record and assign new values to each
	 * clone for a specific column.
	 * @param ctx context
	 * @param tableName Table Name
	 * @param columnName Column for which value need to be changed
	 * @param recordId Record to clone
	 * @param values Values to be assigned to clones for the specified column
	 * @param trxName Transaction
	 * @throws Exception If error is occured when loading the PO or saving clones
	 *
	 * @author ashley
	 */
	protected void saveMultipleRecords(Properties ctx, String tableName,
			String columnName, int recordId, Integer[] values,
			String trxName) throws Exception
	{
//		if (values == null)
//		{
//			return ;
//		}
//
//		int oldRow = gridTab.getCurrentRow();
//		GridField lineField = gridTab.getField("Line");
//
//		for (int i = 0; i < values.length; i++)
//		{
//			if (!gridTab.dataNew(false))
//			{
//				throw new IllegalStateException("Could not create new row");
//			}
//
//			gridTab.setValue(columnName, values[i]);
//
//			if (lineField != null)
//			{
//				gridTab.setValue(lineField, 0);
//			}
//
//			if (!gridTab.dataSave(false))
//			{
//				throw new IllegalStateException("Could not update row");
//			}
//		}
//		gridTab.setCurrentRow(oldRow);
	}
}