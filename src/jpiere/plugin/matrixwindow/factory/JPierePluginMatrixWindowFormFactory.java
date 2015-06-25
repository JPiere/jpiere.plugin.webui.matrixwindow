/******************************************************************************
 * Product: iDempiere - Lab				 			                          *
 * Copyright (C) Hideaki Hagiwara All Rights Reserved.                        *
 * このプログラムはGNU Gneral Public Licens Version2です。                    *
 * このプログラムは萩原秀明が個人の研究用に作成しているもので、			      *
 * いかなる保証もしていません。                                               *
 * 著作権は萩原秀明(h.hagiwara@oss-erp.co.jp)が保持しています。				  *
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
 *  @author Hideaki Hagiwara（萩原 秀明:h.hagiwara@oss-erp.co.jp）
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
