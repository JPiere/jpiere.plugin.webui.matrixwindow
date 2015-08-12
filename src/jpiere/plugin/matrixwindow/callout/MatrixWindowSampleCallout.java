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
package jpiere.plugin.matrixwindow.callout;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Properties;
import java.util.TreeMap;

import jpiere.plugin.matrixwindow.base.IMatrixWindowCallout;

import org.adempiere.webui.editor.WEditor;
import org.compiere.model.GridField;
import org.compiere.model.PO;

public class MatrixWindowSampleCallout implements IMatrixWindowCallout {

	@Override
	public String start(Properties ctx, int colNumX, HashMap<Integer, WEditor> columnEditorMap, HashMap<Integer, GridField> columnGridFieldMap,
			TreeMap<Integer, Object> viewModelRowData, TreeMap<Integer, Object> conversionTableRowData, HashMap<Integer, PO> tableModel, HashMap<Integer, PO> dirtyModel)
	{
    	int xTest = 1;
    	Object valueTest = 777;
    	//Step1:Update Editor Value for display data.
    	WEditor editorTest = columnEditorMap.get(xTest);
    	editorTest.setValue(valueTest);

    	//Step2:Update ViewModel data for display data.
    	viewModelRowData.put(xTest,new BigDecimal(valueTest.toString()));

    	//Step3:Update Context : GridField.setValue method can update context
    	columnGridFieldMap.get(xTest).setValue(valueTest, false);

    	//Step4:Update tableModel for consistency
    	Object poTest_ID = conversionTableRowData.get(xTest);
    	PO poTest = tableModel.get(poTest_ID);
    	poTest.set_ValueNoCheck(editorTest.getColumnName(), new BigDecimal(valueTest.toString()));

    	//Sstep5:Put map of dirtyModel for save data.
    	dirtyModel.put((Integer)poTest_ID, poTest);

		//Hint : you can judge whether same record or not.
		//If same record,TabNo that get from GridField is same.
    	int tabNo = columnGridFieldMap.get(1).getGridTab().getTabNo();

    	//If same record,conversionTableRowData is same data.
    	 Object poTest_ID1 = conversionTableRowData.get(xTest);
    	 Object poTest_ID2 = conversionTableRowData.get(xTest+1);
    	 Object poTest_ID3 = conversionTableRowData.get(xTest+5);
    	 Object poTest_ID4 = conversionTableRowData.get(xTest+6);
		return null;
	}

}
