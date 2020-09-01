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

import org.adempiere.webui.component.Window;
import org.adempiere.webui.util.ZKUpdateUtil;
import org.zkoss.zul.Button;
import org.zkoss.zul.Vbox;

/**
 *
 * JPIERE-0098: Matrix Window
 *
 * MatrixWindowButtonPopup copid from : ProcessButtonPopup.java(@author hengsin)
 *
 * @author Hideaki Hagiwara
 *
 */
public class MatrixWindowButtonPopup extends Window{

	/**
	 * generated serial id
	 */
	private static final long serialVersionUID = 304878472233552113L;

	public void render(List<Button> buttons) {
		setSclass("toolbar-popup-window process-buttons-popup");
		//setWidth("250px");
		ZKUpdateUtil.setHflex(this, "min");
		setBorder("normal");

		Vbox vbox = new Vbox();
		ZKUpdateUtil.setHflex(vbox, "true");
		appendChild(vbox);
		vbox.setSclass("toolbar-popup-window-cnt");
		vbox.setAlign("stretch");

		for(Button button : buttons) {
			if (button.getPage() != null) {
				button.detach();
			}
			ZKUpdateUtil.setHflex(button, "1");
			vbox.appendChild(button);
		}
	}



}
