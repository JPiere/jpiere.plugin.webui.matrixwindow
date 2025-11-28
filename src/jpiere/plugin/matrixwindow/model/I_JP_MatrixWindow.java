/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
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
package jpiere.plugin.matrixwindow.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for JP_MatrixWindow
 *  @author iDempiere (generated) 
 *  @version Release 12
 */
@SuppressWarnings("all")
public interface I_JP_MatrixWindow 
{

    /** TableName=JP_MatrixWindow */
    public static final String Table_Name = "JP_MatrixWindow";

    /** AD_Table_ID=1000026 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 4 - System 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(4);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Tenant.
	  * Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within tenant
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within tenant
	  */
	public int getAD_Org_ID();

    /** Column name AD_Tab_ID */
    public static final String COLUMNNAME_AD_Tab_ID = "AD_Tab_ID";

	/** Set Tab.
	  * Tab within a Window
	  */
	public void setAD_Tab_ID (int AD_Tab_ID);

	/** Get Tab.
	  * Tab within a Window
	  */
	public int getAD_Tab_ID();

	public org.compiere.model.I_AD_Tab getAD_Tab() throws RuntimeException;

    /** Column name AD_Window_ID */
    public static final String COLUMNNAME_AD_Window_ID = "AD_Window_ID";

	/** Set Window.
	  * Data entry or display window
	  */
	public void setAD_Window_ID (int AD_Window_ID);

	/** Get Window.
	  * Data entry or display window
	  */
	public int getAD_Window_ID();

	public org.compiere.model.I_AD_Window getAD_Window() throws RuntimeException;

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name Description */
    public static final String COLUMNNAME_Description = "Description";

	/** Set Description.
	  * Optional short description of the record
	  */
	public void setDescription (String Description);

	/** Get Description.
	  * Optional short description of the record
	  */
	public String getDescription();

    /** Column name FieldLength */
    public static final String COLUMNNAME_FieldLength = "FieldLength";

	/** Set Length.
	  * Length of the column in the database
	  */
	public void setFieldLength (int FieldLength);

	/** Get Length.
	  * Length of the column in the database
	  */
	public int getFieldLength();

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name JP_JoinClause */
    public static final String COLUMNNAME_JP_JoinClause = "JP_JoinClause";

	/** Set SQL JOIN	  */
	public void setJP_JoinClause (String JP_JoinClause);

	/** Get SQL JOIN	  */
	public String getJP_JoinClause();

    /** Column name JP_MatrixColumnKey_ID */
    public static final String COLUMNNAME_JP_MatrixColumnKey_ID = "JP_MatrixColumnKey_ID";

	/** Set Column Key Field	  */
	public void setJP_MatrixColumnKey_ID (int JP_MatrixColumnKey_ID);

	/** Get Column Key Field	  */
	public int getJP_MatrixColumnKey_ID();

	public org.compiere.model.I_AD_Field getJP_MatrixColumnKey() throws RuntimeException;

    /** Column name JP_MatrixRowKey_ID */
    public static final String COLUMNNAME_JP_MatrixRowKey_ID = "JP_MatrixRowKey_ID";

	/** Set Row Key Field	  */
	public void setJP_MatrixRowKey_ID (int JP_MatrixRowKey_ID);

	/** Get Row Key Field	  */
	public int getJP_MatrixRowKey_ID();

	public org.compiere.model.I_AD_Field getJP_MatrixRowKey() throws RuntimeException;

    /** Column name JP_MatrixWindow_ID */
    public static final String COLUMNNAME_JP_MatrixWindow_ID = "JP_MatrixWindow_ID";

	/** Set Matrix Window	  */
	public void setJP_MatrixWindow_ID (int JP_MatrixWindow_ID);

	/** Get Matrix Window	  */
	public int getJP_MatrixWindow_ID();

    /** Column name JP_MatrixWindow_UU */
    public static final String COLUMNNAME_JP_MatrixWindow_UU = "JP_MatrixWindow_UU";

	/** Set JPiere Matrix Window UU	  */
	public void setJP_MatrixWindow_UU (String JP_MatrixWindow_UU);

	/** Get JPiere Matrix Window UU	  */
	public String getJP_MatrixWindow_UU();

    /** Column name JP_PageSize */
    public static final String COLUMNNAME_JP_PageSize = "JP_PageSize";

	/** Set Page Size	  */
	public void setJP_PageSize (int JP_PageSize);

	/** Get Page Size	  */
	public int getJP_PageSize();

    /** Column name JP_QuickEntryConf */
    public static final String COLUMNNAME_JP_QuickEntryConf = "JP_QuickEntryConf";

	/** Set Quick Entry Conf	  */
	public void setJP_QuickEntryConf (String JP_QuickEntryConf);

	/** Get Quick Entry Conf	  */
	public String getJP_QuickEntryConf();

    /** Column name JP_QuickEntryWindow_ID */
    public static final String COLUMNNAME_JP_QuickEntryWindow_ID = "JP_QuickEntryWindow_ID";

	/** Set Quick Entry Window	  */
	public void setJP_QuickEntryWindow_ID (int JP_QuickEntryWindow_ID);

	/** Get Quick Entry Window	  */
	public int getJP_QuickEntryWindow_ID();

	public org.compiere.model.I_AD_Window getJP_QuickEntryWindow() throws RuntimeException;

    /** Column name Name */
    public static final String COLUMNNAME_Name = "Name";

	/** Set Name.
	  * Alphanumeric identifier of the entity
	  */
	public void setName (String Name);

	/** Get Name.
	  * Alphanumeric identifier of the entity
	  */
	public String getName();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();

    /** Column name Value */
    public static final String COLUMNNAME_Value = "Value";

	/** Set Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value);

	/** Get Search Key.
	  * Search key for the record in the format required - must be unique
	  */
	public String getValue();

    /** Column name WhereClause */
    public static final String COLUMNNAME_WhereClause = "WhereClause";

	/** Set Sql WHERE.
	  * Fully qualified SQL WHERE clause
	  */
	public void setWhereClause (String WhereClause);

	/** Get Sql WHERE.
	  * Fully qualified SQL WHERE clause
	  */
	public String getWhereClause();
}
