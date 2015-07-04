package jpiere.plugin.matrixwindow.model;

import java.sql.ResultSet;
import java.util.Properties;

import org.compiere.model.MIndexColumn;
import org.compiere.model.MTable;
import org.compiere.model.MTableIndex;

public class MMatrixSearch extends X_JP_MatrixSearch {

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
			if(getAD_Field().getAD_Column().isParent())
			{
				log.saveError("Error", "カラムのIsParentフラグをOFFにして下さい。");
				return false;
			}

			if(getAD_Field().getAD_Column().isUpdateable())
			{
				log.saveError("Error", "カラムのisUpdateableフラグをONにして下さい。");
				return false;
			}


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

			MMatrixField[] fields = m_matrixWindow.getMatrixFields();
			for(int i = 0 ; i < fields.length; i++)
			{
				if(fields[i].getAD_Field_ID()==getAD_Field_ID())
				{
					log.saveError("Error", "フィールドは編集フィールドに設定されています。");
					return false;
				}

			}

		}//if

		return true;
	}

	@Override
	protected boolean afterSave(boolean newRecord, boolean success) {

		//ユニークキーのチェック処理
		if(success || newRecord || is_ValueChanged("AD_Field_ID") || is_ValueChanged("IsMandatory"))
		{
			MTable mTable = MTable.get(getCtx(), getParent().getAD_Tab().getAD_Table_ID());
			MTableIndex[] m_tableIndexes = MTableIndex.get(mTable);
			MMatrixSearch[] matrixSearches = getParent().getMatrixSearches(" AND IsMandatory='Y' ", " SeqNo ");
			boolean isUniqueConstraint = false;
			int uniqueConstraintCounter = 0;
			for(int i = 0 ; i < m_tableIndexes.length; i++)
			{
				MIndexColumn[] m_indexColumns = m_tableIndexes[i].getColumns(false);
				for(int j = 0; j < m_indexColumns.length; j++)
				{
					if(m_indexColumns[j].getAD_Column_ID()==getParent().getJP_MatrixColumnKey().getAD_Column_ID())
					{
						uniqueConstraintCounter++;
						continue;
					}

					if(m_indexColumns[j].getAD_Column_ID()==getParent().getJP_MatrixRowKey().getAD_Column_ID())
					{
						uniqueConstraintCounter++;
						continue;
					}

					for(int k = 0 ; k < matrixSearches.length; k++)
					{
						if(m_indexColumns[j].getAD_Column_ID()==matrixSearches[k].getAD_Field().getAD_Column_ID())
						{
							uniqueConstraintCounter++;
							break;
						}
					}//for k
				}//for j

				if(uniqueConstraintCounter==(matrixSearches.length + 2))
				{
					isUniqueConstraint = true;
					break;
				}else{
					uniqueConstraintCounter=0;
				}

			}//for i

			if(!isUniqueConstraint)
			{
				log.saveError("Error", "列キー、行キー、必須検索フィールドにはユニーク制約を設定して下さい。");
				return false;
			}

		}//if

		return success;
	}



}
