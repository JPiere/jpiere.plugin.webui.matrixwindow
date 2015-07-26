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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MField;
import org.compiere.model.MIndexColumn;
import org.compiere.model.MTab;
import org.compiere.model.MTable;
import org.compiere.model.MTableIndex;
import org.compiere.model.MWindow;
import org.compiere.model.Query;
import org.compiere.util.Env;
import org.compiere.util.Util;


/**
 * MMatrixWindow
 *
 * JPIERE-0098
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class MMatrixWindow extends X_JP_MatrixWindow {

	MMatrixField[] matrixFields ;

	MMatrixSearch[] matrixSearches ;

	MField[] contentFields;

	MTab mTab;

	public MMatrixWindow(Properties ctx, int JP_MatrixWindow_ID, String trxName) {
		super(ctx, JP_MatrixWindow_ID, trxName);
	}

	public MMatrixWindow(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}

	public MField[] getContentFields()
	{

		if (contentFields != null) {
			set_TrxName(contentFields, get_TrxName());
			return contentFields;
		}

		if(matrixFields ==null)
			getMatrixFields();

		ArrayList<MField> list = new ArrayList<MField>();
		for(int i = 0; i < matrixFields.length; i++)
		{
			list.add(new MField(Env.getCtx(),matrixFields[i].getAD_Field_ID(),get_TrxName()));
		}

		contentFields = list.toArray(new MField[list.size()]);

		return contentFields;
	}

	public MMatrixField[] getMatrixFields(String whereClause, String orderClause)
	{
		//red1 - using new Query class from Teo / Victor's MDDOrder.java implementation
		StringBuilder whereClauseFinal = new StringBuilder(MMatrixField.COLUMNNAME_JP_MatrixWindow_ID+"=? AND IsActive='Y'");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MMatrixField.COLUMNNAME_SeqNo;
		//
		List<MMatrixField> list = new Query(getCtx(), I_JP_MatrixField.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();
		//
		return list.toArray(new MMatrixField[list.size()]);
	}	//	getLines

	/**
	 * 	Get MatrixFields
	 * 	@param requery requery
	 * 	@param orderBy optional order by column
	 * 	@return MatrixFields
	 */
	public MMatrixField[] getMatrixFields(boolean requery, String orderBy)
	{
		if (matrixFields != null && !requery) {
			set_TrxName(matrixFields, get_TrxName());
			return matrixFields;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += "SeqNo";

		matrixFields = getMatrixFields(null, orderClause);
		return matrixFields;
	}	//	getLines

	/**
	 * 	Get Matrix Fields of Matrix Window.
	 */
	public MMatrixField[] getMatrixFields()
	{
		return getMatrixFields(false, null);
	}	//	getLines


	public MMatrixSearch[] getMatrixSearches(String whereClause, String orderClause)
	{
		//red1 - using new Query class from Teo / Victor's MDDOrder.java implementation
		StringBuilder whereClauseFinal = new StringBuilder(MMatrixSearch.COLUMNNAME_JP_MatrixWindow_ID + "=? AND IsActive='Y'");
		if (!Util.isEmpty(whereClause, true))
			whereClauseFinal.append(whereClause);
		if (orderClause.length() == 0)
			orderClause = MMatrixSearch.COLUMNNAME_SeqNo;
		//
		List<MMatrixSearch> list = new Query(getCtx(), I_JP_MatrixSearch.Table_Name, whereClauseFinal.toString(), get_TrxName())
										.setParameters(get_ID())
										.setOrderBy(orderClause)
										.list();
		//
		return list.toArray(new MMatrixSearch[list.size()]);
	}	//	getLines

	/**
	 * 	Get MatrixSearches of Matrix Window.
	 * 	@param requery requery
	 * 	@param orderBy optional order by column
	 * 	@return MatrixSearche
	 */
	public MMatrixSearch[] getMatrixSearches(boolean requery, String orderBy)
	{
		if (matrixSearches != null && !requery) {
			set_TrxName(matrixSearches, get_TrxName());
			return matrixSearches;
		}
		//
		String orderClause = "";
		if (orderBy != null && orderBy.length() > 0)
			orderClause += orderBy;
		else
			orderClause += "SeqNo";

		matrixSearches = getMatrixSearches(null, orderClause);
		return matrixSearches;
	}	//	getLines

	/**
	 * 	Get MatrixSearches of Matrix Window.
	 */
	public MMatrixSearch[] getMatrixSearches()
	{
		return getMatrixSearches(false, null);
	}

	@Override
	protected boolean beforeSave(boolean newRecord)
	{

		if(newRecord
				|| is_ValueChanged("JP_MatrixColumnKey_ID")
				|| is_ValueChanged("JP_MatrixRowKey_ID"))
		{

			if(getJP_MatrixColumnKey().getAD_Column_ID()==getJP_MatrixRowKey().getAD_Column_ID()){
				log.saveError("Error", "行キーと列キーが同じです");
				return false;
			}

			//Check unique Constraint
			MTable mTable = MTable.get(getCtx(), getAD_Tab().getAD_Table_ID());
			MTableIndex[] m_tableIndexes = MTableIndex.get(mTable);
			boolean isUniqueConstraint = false;
			for(int i = 0 ; i < m_tableIndexes.length; i++)
			{
				if(!m_tableIndexes[i].isUnique())
					continue;

				boolean isColumnKey = false;
				boolean isRowKey = false;

				MIndexColumn[] m_indexColumns = m_tableIndexes[i].getColumns(false);
				for(int j = 0; j < m_indexColumns.length; j++)
				{

					if(m_indexColumns[j].getAD_Column_ID()==getJP_MatrixColumnKey().getAD_Column_ID())
						isColumnKey = true;

					if(m_indexColumns[j].getAD_Column_ID()==getJP_MatrixRowKey().getAD_Column_ID())
						isRowKey = true;
				}

				if(isColumnKey && isRowKey)
				{
					isUniqueConstraint = true;
					break;
				}
			}

			if(!isUniqueConstraint)
			{
				log.saveError("Error", "列キー、行キーにユニーク制約が設定されていません。");
				return false;
			}

		}

		if(getJP_QuickEntryWindow_ID() > 0)
		{

			if(newRecord || is_ValueChanged("JP_QuickEntryWindow_ID"))
			{
				MWindow quickEntryWindow = MWindow.get(getCtx(), getJP_QuickEntryWindow_ID());
				MTab[] quickEntryTabs = quickEntryWindow.getTabs(false, null);

				if(quickEntryTabs[0].getAD_Table_ID() != getAD_Tab().getAD_Table_ID())
				{
					log.saveError("Error", "タブとクィックウィンドウの関係が正しくありません");
					return false;
				}
			}
		}





		return true;
	}

	private MTab getTab()
	{

		if (mTab == null) {
			mTab = new MTab(getCtx(), getAD_Tab_ID(), get_TrxName());
		}

		return mTab;
	}

	/**
	 * 	Get MMatrixWindow with Value
	 *	@param ctx context
	 *	@param Value value
	 *	@return MMatrixWindow or null
	 */
	public static MMatrixWindow get (Properties ctx, String Value)
	{
		if (Value == null || Value.length() == 0)
			return null;
		final String whereClause = "Value=?";
		MMatrixWindow retValue = new Query(ctx, I_JP_MatrixWindow.Table_Name, whereClause, null)
				.setParameters(Value)
				.firstOnly();
		return retValue;
	}	//	get


}
