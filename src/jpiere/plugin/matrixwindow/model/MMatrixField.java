/******************************************************************************
 * Product: JPiere(Localization Japan of iDempiere)   - Plugins               *
 * Plugin Name:Window X(Matrix Window)                                        *
 * Copyright (C) Hideaki Hagiwara All Rights Reserved.                        *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/******************************************************************************
 * JPiereはiDempiereの日本商慣習対応のディストリビューションであり、          *
 * プラグイン群です。                                                         *
 * このプログラムはGNU Gneral Public Licens Version2のもと公開しています。    *
 * このプログラムは自由に活用してもらう事を期待して公開していますが、         *
 * いかなる保証もしていません。                                               *
 * 著作権は萩原秀明(h.hagiwara@oss-erp.co.jp)が保有し、サポートサービスは     *
 * 株式会社オープンソース・イーアールピー・ソリューションズで                 *
 * 提供しています。サポートをご希望の際には、                                 *
 * 株式会社オープンソース・イーアールピー・ソリューションズまでご連絡下さい。 *
 * http://www.oss-erp.co.jp/                                                  *
 *****************************************************************************/
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
