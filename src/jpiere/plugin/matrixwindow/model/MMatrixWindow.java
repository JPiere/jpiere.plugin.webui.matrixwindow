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

public class MMatrixWindow extends X_JP_MatrixWindow {

	MMatrixField[] matrixFields ;
	MField[] contentFields;
	MTab mTab;

	public MMatrixWindow(Properties ctx, int JP_MatrixWindow_ID, String trxName) {
		super(ctx, JP_MatrixWindow_ID, trxName);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public MMatrixWindow(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public MField[] getContentFields(){

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
	 * 	Get Lines of Order
	 * 	@param requery requery
	 * 	@param orderBy optional order by column
	 * 	@return lines
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

	@Override
	protected boolean beforeSave(boolean newRecord) {

		if(newRecord || is_ValueChanged("AD_Tab_ID"))
		{
			if(getTab().getTabLevel() == 0)
			{
				log.saveError("Error", "タブレベルが0のタブは設定する事ができません");
				return false;
			}
		}


		//リンクカラム、列キー、行キーにユニーク制約がかかっている事の確認
		if(newRecord
				|| is_ValueChanged("AD_Tab_ID")
				|| is_ValueChanged("JP_MatrixColumnKey_ID")
				|| is_ValueChanged("JP_MatrixRowKey_ID"))
		{

			if(getAD_Tab().getAD_Column_ID()==getJP_MatrixColumnKey().getAD_Column_ID())
			{
				log.saveError("Error", "タブのリンクカラムの設定と列キーが同じです");
				return false;
			}else if(getAD_Tab().getAD_Column_ID()==getJP_MatrixRowKey().getAD_Column_ID()){
				log.saveError("Error", "タブのリンクカラムの設定と行キーが同じです");
				return false;
			}else if(getJP_MatrixColumnKey().getAD_Column_ID()==getJP_MatrixRowKey().getAD_Column_ID()){
				log.saveError("Error", "行キーと列キーが同じです");
				return false;
			}

			MTable mTable = MTable.get(getCtx(), getAD_Tab().getAD_Table_ID());
			MTableIndex[] m_tableIndexes = MTableIndex.get(mTable);
			boolean isUniqueConstraint = false;
			for(int i = 0 ; i < m_tableIndexes.length; i++)
			{
				if(!m_tableIndexes[i].isUnique())
					continue;

				boolean isLinkColumn = false;
				boolean isColumnKey = false;
				boolean isRowKey = false;

				MIndexColumn[] m_indexColumns = m_tableIndexes[i].getColumns(false);
				for(int j = 0; j < m_indexColumns.length; j++)
				{
					if(m_indexColumns[j].getAD_Column_ID()==getAD_Tab().getAD_Column_ID())
						isLinkColumn = true;

					if(m_indexColumns[j].getAD_Column_ID()==getJP_MatrixColumnKey().getAD_Column_ID())
						isColumnKey = true;

					if(m_indexColumns[j].getAD_Column_ID()==getJP_MatrixRowKey().getAD_Column_ID())
						isRowKey = true;
				}

				if(isLinkColumn && isColumnKey && isRowKey)
				{
					isUniqueConstraint = true;
					break;
				}
			}

			if(!isUniqueConstraint)
			{
				log.saveError("Error", "タブのリンクカラム、列キー、行キーにユニーク制約が設定されていません。");
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
