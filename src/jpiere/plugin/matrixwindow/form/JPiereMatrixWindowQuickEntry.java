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
package jpiere.plugin.matrixwindow.form;

import java.util.List;

import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.grid.WQuickEntry;
import org.zkoss.zk.ui.event.Event;

/**
 * JPiereMatrixWindowQuickEntry
 *
 * JPIERE-0098
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
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

		try{
			super.onEvent(e);
		}catch(Exception ex){
			this.detach();
			throw ex;
		}

		if (e.getTarget().getId().equals("Ok"))
		{
			matrixWindow.onEvent(e);
		}

	}


}
