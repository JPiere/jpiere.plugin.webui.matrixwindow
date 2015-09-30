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
package jpiere.plugin.matrixwindow.callout;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import jpiere.plugin.matrixwindow.base.IMatrixWindowCallout;
import jpiere.plugin.matrixwindow.form.JPMatrixDataBinder;

import org.compiere.model.GridField;
import org.compiere.util.DB;

public class MatrixWindowSampleCallout implements IMatrixWindowCallout {

	@Override
	public String start(JPMatrixDataBinder dataBinder, int x, int y , Object newValue, Object oldValue)
	{

		GridField gridField = dataBinder.getColumnGridFieldMap().get(x);
		int tabNo = gridField.getGridTab().getTabNo();

		for(int i = 0; i < dataBinder.getColumnGridFieldMap().size(); i++)
		{

			gridField = dataBinder.getColumnGridFieldMap().get(i);
			if(gridField.getGridTab().getTabNo() == tabNo && gridField.getColumnName().equals("C_BPartner_Location_ID"))
			{

				Integer C_BPartner_ID = (Integer)newValue;
				if (C_BPartner_ID == null || C_BPartner_ID.intValue() == 0)
					return "";
				String sql = "SELECT p.AD_Language,p.C_PaymentTerm_ID,"
					+ " COALESCE(p.M_PriceList_ID,g.M_PriceList_ID) AS M_PriceList_ID, p.PaymentRule,p.POReference,"
					+ " p.SO_Description,p.IsDiscountPrinted,"
					+ " p.InvoiceRule,p.DeliveryRule,p.FreightCostRule,DeliveryViaRule,"
					+ " p.SO_CreditLimit, p.SO_CreditLimit-p.SO_CreditUsed AS CreditAvailable,"
					+ " lship.C_BPartner_Location_ID,c.AD_User_ID,"
					+ " COALESCE(p.PO_PriceList_ID,g.PO_PriceList_ID) AS PO_PriceList_ID, p.PaymentRulePO,p.PO_PaymentTerm_ID,"
					+ " lbill.C_BPartner_Location_ID AS Bill_Location_ID, p.SOCreditStatus, "
					+ " p.SalesRep_ID "
					+ "FROM C_BPartner p"
					+ " INNER JOIN C_BP_Group g ON (p.C_BP_Group_ID=g.C_BP_Group_ID)"
					+ " LEFT OUTER JOIN C_BPartner_Location lbill ON (p.C_BPartner_ID=lbill.C_BPartner_ID AND lbill.IsBillTo='Y' AND lbill.IsActive='Y')"
					+ " LEFT OUTER JOIN C_BPartner_Location lship ON (p.C_BPartner_ID=lship.C_BPartner_ID AND lship.IsShipTo='Y' AND lship.IsActive='Y')"
					+ " LEFT OUTER JOIN AD_User c ON (p.C_BPartner_ID=c.C_BPartner_ID AND c.IsActive='Y') "
					+ "WHERE p.C_BPartner_ID=? AND p.IsActive='Y'";		//	#1

				PreparedStatement pstmt = null;
				ResultSet rs = null;
				try
				{

					pstmt = DB.prepareStatement(sql, null);
					pstmt.setInt(1, C_BPartner_ID.intValue());
					rs = pstmt.executeQuery();
					if (rs.next())
					{
						Integer bill_Location_ID = rs.getInt("Bill_Location_ID");
						dataBinder.setValue(i, y, bill_Location_ID);

					}


				}catch (SQLException e){

					return e.getLocalizedMessage();
				}
				finally
				{
					DB.close(rs, pstmt);
					rs = null; pstmt = null;
				}

			}//if

		}//for

		return "";
	}

}
