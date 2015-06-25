/******************************************************************************
 * Product: JPiere(ジェイピエール) - JPiere Base Plugin                       *
 * Copyright (C) Hideaki Hagiwara All Rights Reserved.                        *
 * このプログラムはGNU Gneral Public Licens Version2のもと公開しています。    *
 * このプログラムは自由に活用してもらう事を期待して公開していますが、         *
 * いかなる保証もしていません。                                               *
 * 著作権は萩原秀明(h.hagiwara@oss-erp.co.jp)が保持し、サポートサービスは     *
 * 株式会社オープンソース・イーアールピー・ソリューションズで                 *
 * 提供しています。サポートをご希望の際には、                                 *
 * 株式会社オープンソース・イーアールピー・ソリューションズまでご連絡下さい。 *
 * http://www.oss-erp.co.jp/                                                  *
 *****************************************************************************/

package jpiere.plugin.matrixwindow.factory;

import java.sql.ResultSet;

import jpiere.plugin.matrixwindow.model.MMatrixField;
import jpiere.plugin.matrixwindow.model.MMatrixWindow;

import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.compiere.util.Env;

/**
 *  JPiere Base Plugin Model Factory
 *
 *  @author Hideaki Hagiwara（萩原 秀明:h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPierePluginMatrixWindowModelFactory implements IModelFactory {

	@Override
	public Class<?> getClass(String tableName) {
		if(tableName.equals(MMatrixWindow.Table_Name)){
			return MMatrixWindow.class;
		}else if(tableName.equals(MMatrixField.Table_Name)){
			return MMatrixField.class;
		}
		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {
		if(tableName.equals(MMatrixWindow.Table_Name)){
			return  new MMatrixWindow(Env.getCtx(), Record_ID, trxName);
		}else if(tableName.equals(MMatrixField.Table_Name)){
			return  new MMatrixField(Env.getCtx(), Record_ID, trxName);
		}
		return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {
		if(tableName.equals(MMatrixWindow.Table_Name)){
			return  new MMatrixWindow(Env.getCtx(), rs, trxName);
		}else if(tableName.equals(MMatrixField.Table_Name)){
			return  new MMatrixField(Env.getCtx(), rs, trxName);
		}

		return null;
	}

}
