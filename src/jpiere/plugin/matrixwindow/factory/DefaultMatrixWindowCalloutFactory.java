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

import jpiere.plugin.matrixwindow.base.IMatrixWindowCallout;
import jpiere.plugin.matrixwindow.base.IMatrixWindowCalloutFactory;
import jpiere.plugin.matrixwindow.callout.MatrixWindowSampleCallout;

/**
 * Dafault Matrix Window Callout Factory
 *
 * JPIERE-0098
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class DefaultMatrixWindowCalloutFactory implements IMatrixWindowCalloutFactory {

	@Override
	public IMatrixWindowCallout getCallout(String tableName, String columnName) {

		if(tableName.equals("JP_ReferenceTest") && columnName.equals("C_BPartner_ID"))
		{
			return new MatrixWindowSampleCallout();
		}

		return null;

	}

}
