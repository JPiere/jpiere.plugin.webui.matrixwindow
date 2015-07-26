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
package jpiere.plugin.matrixwindow.factory;

import java.util.logging.Level;

import jpiere.plugin.matrixwindow.form.AbstractMatrixWindowForm;

import org.adempiere.webui.factory.IFormFactory;
import org.adempiere.webui.panel.ADForm;
import org.compiere.util.CLogger;


/**
 *  JPiere Plugin Matrix Window Form Factory
 *
 *  JPIERE-0098
 *
 *  @author Hideaki Hagiwara（h.hagiwara@oss-erp.co.jp）
 *
 */
public class JPierePluginMatrixWindowFormFactory implements IFormFactory{

	private static final CLogger log = CLogger.getCLogger(JPierePluginMatrixWindowFormFactory.class);

	@Override
	public ADForm newFormInstance(String formName) {
		Object form = null;
	     if (formName.startsWith("jpiere.plugin.matrixwindow.form"))
	     {

	    	String matrixWindowValue = formName.substring("jpiere.plugin.matrixwindow.form.".length());

	       	ClassLoader loader = this.getClass().getClassLoader();
	       	Class<?> clazz = null;

			try
    		{
    			//	Create instance w/o parameters
        		clazz = loader.loadClass("jpiere.plugin.matrixwindow.form.JPiereMatrixWindow");
    		}
    		catch (Exception e)
    		{
    			if (log.isLoggable(Level.INFO))
    				log.log(Level.INFO, e.getLocalizedMessage(), e);
    		}


			if (clazz != null) {
				try
	    		{
	    			form = clazz.newInstance();
	    		}
	    		catch (Exception e)
	    		{
	    			if (log.isLoggable(Level.WARNING))
	    				log.log(Level.WARNING, e.getLocalizedMessage(), e);
	    		}
			}

		      if (form != null) {
					if (form instanceof AbstractMatrixWindowForm )
					{
						AbstractMatrixWindowForm  controller = (AbstractMatrixWindowForm) form;
						controller.createMatrixWindow(matrixWindowValue);
						ADForm adForm = controller.getForm();
						adForm.setICustomForm(controller);
						return adForm;
					}
		     }
	     }
	     return null;
	}


}
