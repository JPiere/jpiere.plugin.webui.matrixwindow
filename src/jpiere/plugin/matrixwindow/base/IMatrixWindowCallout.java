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
package jpiere.plugin.matrixwindow.base;

import java.util.HashMap;
import java.util.Properties;
import java.util.TreeMap;

import org.adempiere.webui.editor.WEditor;
import org.compiere.model.GridField;
import org.compiere.model.PO;

/**
 *  Matrix Window Callout Interface
 */
public interface IMatrixWindowCallout
{
	/**
	 *	Start Matrix Window Callout.
	 *
	 *	<p>Step1:Update WEditor Value for display data.</p>
	 *	<p>Step2:Update ViewModel data for display data.</p>
	 *	<p>Step3:Update Context : GridField.setValue method can update context</p>
	 *	<p>Step4:Update tableModel for consistency</p>
	 *	<p>Step5:Put map of dirtyModel for save data.</p>
	 *
	 *  @param ctx      				:Context
	 *  @param colNumX  				:Column number that Callout occurred
	 *  @param columnEditorMap  		:Map of "Column number(Integer),WEditor" use for update display editor.
	 *  @param columnGridFieldMap  		:Map of "Column number(Integer),GridField"  use for update context.
	 *  @param viewModelRowData 		:Map of "Column Number(Ingeter),data(Object)" is a row data of veiwModel, use for display value.
	 *  @param conversionTableRowData 	:Map of "Column Number(Integer),ID of PO(Object)" use for to get PO
	 *  @param tableModel    			:Map of "ID of PO(Integer),PO" use for consistency
	 *  @param dirtyModel The old value	:Map of "ID of PO(Integer),PO" use for save.
	 *  @return Error message or ""
	 */
	public String start (Properties ctx, int colNumX, HashMap<Integer,WEditor> columnEditorMap, HashMap<Integer,GridField> columnGridFieldMap,
			TreeMap<Integer,Object> viewModelRowData , TreeMap<Integer,Object> conversionTableRowData, HashMap<Integer,PO> 	tableModel, HashMap<Integer,PO> dirtyModel);



}
