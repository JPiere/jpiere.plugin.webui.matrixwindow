package jpiere.plugin.matrixwindow.model;

import java.sql.ResultSet;
import java.util.Properties;

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
