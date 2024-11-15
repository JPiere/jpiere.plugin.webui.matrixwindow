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
package jpiere.plugin.matrixwindow.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.util.Msg;

/**
 * MMatrixSearch
 *
 * JPIERE-0098
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class MMatrixSearch extends X_JP_MatrixSearch {

	private static final long serialVersionUID = -8030318858132594493L;
	
	MMatrixWindow m_matrixWindow;

	public MMatrixSearch(Properties ctx, int JP_MatrixSearch_ID, String trxName)
	{
		super(ctx, JP_MatrixSearch_ID, trxName);
	}

	public MMatrixSearch(Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}

	public MMatrixWindow getParent()
	{
		if(m_matrixWindow == null)
		{
			m_matrixWindow = new MMatrixWindow(getCtx(),getJP_MatrixWindow_ID(),get_TrxName());
		}

		return m_matrixWindow;
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{
		if(newRecord || is_ValueChanged("AD_Field_ID"))
		{

			getParent();

			//Column key Check
			if( getAD_Field().getAD_Column_ID() == m_matrixWindow.getJP_MatrixColumnKey().getAD_Column_ID())
			{
				Object[] objects = {Msg.getElement(getCtx(), getAD_Field().getAD_Column().getColumnName())
						,Msg.getElement(getCtx(), "JP_MatrixSearch_ID")
						,Msg.getElement(getCtx(), "JP_MatrixColumnKey_ID")};
				String errorMessage = Msg.getMsg(getCtx(), "JP_CanNotUseAasB", objects);

				log.saveError("Error", errorMessage);
				return false;
			}

			//Row key Check
			if( getAD_Field().getAD_Column_ID() == m_matrixWindow.getJP_MatrixRowKey().getAD_Column_ID())
			{
				Object[] objects = {Msg.getElement(getCtx(), getAD_Field().getAD_Column().getColumnName())
						,Msg.getElement(getCtx(), "JP_MatrixSearch_ID")
						,Msg.getElement(getCtx(), "JP_MatrixRowKey_ID")};
				String errorMessage = Msg.getMsg(getCtx(), "JP_CanNotUseAasB", objects);

				log.saveError("Error", errorMessage);
				return false;
			}

			//Contents(Edit) Field Check
			MMatrixField[] fields = m_matrixWindow.getMatrixFields();
			for(int i = 0 ; i < fields.length; i++)
			{
				if(fields[i].getAD_Field_ID()==getAD_Field_ID())
				{
					Object[] objects = {Msg.getElement(getCtx(), getAD_Field().getAD_Column().getColumnName())
							,Msg.getElement(getCtx(), "JP_MatrixSearch_ID")
							,Msg.getElement(getCtx(), "JP_MatrixField_ID")};
					String errorMessage = Msg.getMsg(getCtx(), "JP_CanNotUseAasB", objects);

					log.saveError("Error", errorMessage);
					return false;
				}

			}

		}//if

		return true;
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {

		//Check unique Constraint
//		if(success || newRecord || is_ValueChanged("AD_Field_ID") || is_ValueChanged("IsMandatory"))
//		{
//			MTable mTable = MTable.get(getCtx(), getParent().getAD_Tab().getAD_Table_ID());
//			MTableIndex[] m_tableIndexes = MTableIndex.get(mTable);
//			MMatrixSearch[] matrixSearches = getParent().getMatrixSearches(" AND IsMandatory='Y' ", " SeqNo ");
//			boolean isUniqueConstraint = false;
//			int uniqueConstraintCounter = 0;
//			for(int i = 0 ; i < m_tableIndexes.length; i++)
//			{
//				MIndexColumn[] m_indexColumns = m_tableIndexes[i].getColumns(false);
//				for(int j = 0; j < m_indexColumns.length; j++)
//				{
//					if(m_indexColumns[j].getAD_Column_ID()==getParent().getJP_MatrixColumnKey().getAD_Column_ID())
//					{
//						uniqueConstraintCounter++;
//						continue;
//					}
//
//					if(m_indexColumns[j].getAD_Column_ID()==getParent().getJP_MatrixRowKey().getAD_Column_ID())
//					{
//						uniqueConstraintCounter++;
//						continue;
//					}
//
//					for(int k = 0 ; k < matrixSearches.length; k++)
//					{
//						if(m_indexColumns[j].getAD_Column_ID()==matrixSearches[k].getAD_Field().getAD_Column_ID())
//						{
//							uniqueConstraintCounter++;
//							break;
//						}
//					}//for k
//				}//for j
//
//				if(uniqueConstraintCounter==(matrixSearches.length + 2))
//				{
//					isUniqueConstraint = true;
//					break;
//				}else{
//					uniqueConstraintCounter=0;
//				}
//
//			}//for i
//
//			if(!isUniqueConstraint)
//			{
//				Object[] objects = {Msg.getElement(getCtx(),"JP_MatrixColumnKey_ID")
//									+ "," + Msg.getElement(getCtx(), "JP_MatrixRowKey_ID")
//									+ "," + Msg.getElement(getCtx(), "JP_MatrixSearch_ID")
//									+ "(" + Msg.getElement(getCtx(), "IsMandatory") + ")"};
//				String errorMessage = Msg.getMsg(getCtx(), "JP_UniqueConstraintNecessary", objects);
//
//				log.saveError("Error", errorMessage);
//				return false;
//			}
//
//		}//if

		return success;
	}



}
