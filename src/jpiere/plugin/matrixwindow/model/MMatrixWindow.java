package jpiere.plugin.matrixwindow.model;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.compiere.model.MField;
import org.compiere.model.MTab;
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

		if(getJP_MatrixColumnKey_ID() == getJP_MatrixRowKey_ID())
		{
			log.saveError("Error", "行キーと列キーが同じです");
			return false;
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
