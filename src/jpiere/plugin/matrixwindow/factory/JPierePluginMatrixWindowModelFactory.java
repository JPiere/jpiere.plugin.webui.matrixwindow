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

package jpiere.plugin.matrixwindow.factory;

import java.sql.ResultSet;

import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.compiere.util.Env;

import jpiere.plugin.matrixwindow.model.MMatrixField;
import jpiere.plugin.matrixwindow.model.MMatrixSearch;
import jpiere.plugin.matrixwindow.model.MMatrixWindow;

/**
 *  JPiere Plugins Matrix Window Model Factory
 *
 *  JPIERE-0098
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPierePluginMatrixWindowModelFactory implements IModelFactory {

	@Override
	public Class<?> getClass(String tableName)
	{
		if(tableName.startsWith("JP_Matrix"))
		{
			if(tableName.equals(MMatrixWindow.Table_Name)){
				return MMatrixWindow.class;
			}else if(tableName.equals(MMatrixField.Table_Name)){
				return MMatrixField.class;
			}else if(tableName.equals(MMatrixSearch.Table_Name)){
				return MMatrixSearch.class;
			}
		}
		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName)
	{
		if(tableName.startsWith("JP_Matrix"))
		{
			if(tableName.equals(MMatrixWindow.Table_Name)){
				return  new MMatrixWindow(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MMatrixField.Table_Name)){
				return  new MMatrixField(Env.getCtx(), Record_ID, trxName);
			}else if(tableName.equals(MMatrixSearch.Table_Name)){
				return  new MMatrixSearch(Env.getCtx(), Record_ID, trxName);
			}
		}
		return null;
	}
	
	@Override
	public PO getPO(String tableName, String Record_UU, String trxName)
	{
		if(tableName.startsWith("JP_Matrix"))
		{
			if(tableName.equals(MMatrixWindow.Table_Name)){
				return  new MMatrixWindow(Env.getCtx(), Record_UU, trxName);
			}else if(tableName.equals(MMatrixField.Table_Name)){
				return  new MMatrixField(Env.getCtx(), Record_UU, trxName);
			}else if(tableName.equals(MMatrixSearch.Table_Name)){
				return  new MMatrixSearch(Env.getCtx(), Record_UU, trxName);
			}
		}
		return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName)
	{
		if(tableName.startsWith("JP_Matrix"))
		{
			if(tableName.equals(MMatrixWindow.Table_Name)){
				return  new MMatrixWindow(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MMatrixField.Table_Name)){
				return  new MMatrixField(Env.getCtx(), rs, trxName);
			}else if(tableName.equals(MMatrixSearch.Table_Name)){
				return  new MMatrixSearch(Env.getCtx(), rs, trxName);
			}
		}
		return null;
	}

}
