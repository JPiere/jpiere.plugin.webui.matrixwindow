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
package jpiere.plugin.matrixwindow.model;

import java.sql.ResultSet;
import java.util.Properties;


/**
 * MMatrixField
 *
 * JPIERE-0098
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class MMatrixField extends X_JP_MatrixField {

	MMatrixWindow m_matrixWindow;

	public MMatrixField(Properties ctx, int JP_MatrixField_ID, String trxName)
	{
		super(ctx, JP_MatrixField_ID, trxName);
	}

	public MMatrixField(Properties ctx, ResultSet rs, String trxName)
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

			if( getAD_Field().getAD_Column_ID()
				== m_matrixWindow.getJP_MatrixColumnKey().getAD_Column_ID())
			{
				log.saveError("Error", "フィールドは列キーに設定されています。");
				return false;
			}

			if( getAD_Field().getAD_Column_ID()
					== m_matrixWindow.getJP_MatrixRowKey().getAD_Column_ID())
			{
				log.saveError("Error", "フィールドは行キーに設定されています。");
				return false;
			}

			MMatrixSearch[] fields = m_matrixWindow.getMatrixSearches();
			for(int i = 0 ; i < fields.length; i++)
			{
				if(fields[i].getAD_Field_ID()==getAD_Field_ID())
				{
					log.saveError("Error", "フィールドは検索フィールドに設定されています。");
					return false;
				}

			}

		}//if

		return true;
	}


}
