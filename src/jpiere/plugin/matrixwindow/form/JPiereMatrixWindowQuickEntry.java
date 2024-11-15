/******************************************************************************
 * Product: JPiere                                                            *
 * Copyright (C) Hideaki Hagiwara (h.hagiwara@oss-erp.co.jp)                  *
 *                                                                            *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY.                          *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * JPiere is maintained by OSS ERP Solutions Co., Ltd.                        *
 * (http://www.oss-erp.co.jp)                                                 *
 *****************************************************************************/
package jpiere.plugin.matrixwindow.form;

import java.util.List;

import org.adempiere.webui.component.ConfirmPanel;
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

	private static final long serialVersionUID = 147788092065462088L;
	
	private JPiereMatrixWindow matrixWindow;

	public JPiereMatrixWindowQuickEntry(int WindowNo, int AD_Window_ID,JPiereMatrixWindow mw) {
		super(WindowNo, AD_Window_ID);
		matrixWindow = mw;
	}

	public JPiereMatrixWindowQuickEntry(int AD_Window_ID) {
		super(AD_Window_ID);
	}


	public List<WEditor> getQuickEditors()
	{
		return quickEditors;
	}

	public void onClose() {
		detach();
		try{
			matrixWindow.onEvent(new Event(ConfirmPanel.A_CANCEL));
		}catch(Exception ex){
			;
		}
	}

	@Override
	public void onEvent(Event e) throws Exception
	{

		if (e.getTarget().getId().equals(ConfirmPanel.A_OK))
		{
			boolean isOK =false;
			try{
				isOK = actionSave();
			}catch(Exception exception){
				detach();
				matrixWindow.onEvent(new Event(ConfirmPanel.A_CANCEL));//refresh
				throw exception;
			}

			detach();
			if(isOK)
			{
				matrixWindow.onEvent(new Event(ConfirmPanel.A_OK));
			}else{
				matrixWindow.onEvent(new Event(ConfirmPanel.A_CANCEL));//refresh
			}


		}else if (e.getTarget().getId().equals(ConfirmPanel.A_CANCEL)){
			detach();
			matrixWindow.onEvent(new Event(ConfirmPanel.A_CANCEL));//refresh

		}else{
			detach();
		}

	}


}
