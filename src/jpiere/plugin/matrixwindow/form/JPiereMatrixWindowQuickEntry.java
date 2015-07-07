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
package jpiere.plugin.matrixwindow.form;

import java.util.List;

import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.grid.WQuickEntry;
import org.zkoss.zk.ui.event.Event;

public class JPiereMatrixWindowQuickEntry extends WQuickEntry {

	private JPiereMatrixWindow matrixWindow;

	public JPiereMatrixWindowQuickEntry(int WindowNo, int AD_Window_ID,JPiereMatrixWindow mw) {
		super(WindowNo, AD_Window_ID);
		matrixWindow = mw;
	}

	public JPiereMatrixWindowQuickEntry(int AD_Window_ID) {
		super(AD_Window_ID);
		// TODO 自動生成されたコンストラクター・スタブ
	}


	public List<WEditor> getQuickEditors()
	{
		return quickEditors;
	}

	@Override
	public void onEvent(Event e) throws Exception
	{
		super.onEvent(e);

		if (e.getTarget().getId().equals("Ok"))
		{
			matrixWindow.onEvent(e);
		}


	}


}
