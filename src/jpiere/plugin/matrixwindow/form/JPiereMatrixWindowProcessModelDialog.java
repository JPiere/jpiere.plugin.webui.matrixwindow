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

import org.adempiere.webui.apps.ProcessModalDialog;
import org.compiere.process.ProcessInfo;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;


/**
 * JPiereMatrixWindowProcessModelDialog
 *
 * JPIERE-0098
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class JPiereMatrixWindowProcessModelDialog extends ProcessModalDialog {

	private JPiereMatrixWindow matrixWindow;

	public JPiereMatrixWindowProcessModelDialog(int WindowNo, ProcessInfo pi,
			boolean autoStart) {
		super(WindowNo, pi, autoStart);
	}

	public JPiereMatrixWindowProcessModelDialog(EventListener<Event> listener,
			int WindowNo, ProcessInfo pi, boolean autoStart) {
		super(listener, WindowNo, pi, autoStart);
	}

	public JPiereMatrixWindowProcessModelDialog(int WindowNo,
			int AD_Process_ID, int tableId, int recordId, boolean autoStart,JPiereMatrixWindow matrixWindow) {
		super(WindowNo, AD_Process_ID, tableId, recordId, autoStart );

		this.matrixWindow = matrixWindow;
	}

	public JPiereMatrixWindowProcessModelDialog(EventListener<Event> listener,
			int WindowNo, int AD_Process_ID, int tableId, int recordId,
			boolean autoStart) {
		super(listener, WindowNo, AD_Process_ID, tableId, recordId, autoStart);
	}

	@Override
	public void onEvent(Event event)
	{
		super.onEvent(event);

		if(event.getName().equals("onComplete"))
		{
			try{
				matrixWindow.onEvent(event);
			}catch(Exception e){
				;
			}
		}
	}



}
