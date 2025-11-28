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
/** Generated Model - DO NOT CHANGE */
package jpiere.plugin.matrixwindow.model;

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Model for JP_MatrixWindow
 *  @author iDempiere (generated)
 *  @version Release 12 - $Id$ */
@org.adempiere.base.Model(table="JP_MatrixWindow")
public class X_JP_MatrixWindow extends PO implements I_JP_MatrixWindow, I_Persistent
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20251128L;

    /** Standard Constructor */
    public X_JP_MatrixWindow (Properties ctx, int JP_MatrixWindow_ID, String trxName)
    {
      super (ctx, JP_MatrixWindow_ID, trxName);
      /** if (JP_MatrixWindow_ID == 0)
        {
			setAD_Tab_ID (0);
			setAD_Window_ID (0);
			setFieldLength (0);
// 100
			setJP_MatrixColumnKey_ID (0);
			setJP_MatrixRowKey_ID (0);
			setJP_MatrixWindow_ID (0);
			setJP_PageSize (0);
// 20
			setName (null);
			setValue (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_MatrixWindow (Properties ctx, int JP_MatrixWindow_ID, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_MatrixWindow_ID, trxName, virtualColumns);
      /** if (JP_MatrixWindow_ID == 0)
        {
			setAD_Tab_ID (0);
			setAD_Window_ID (0);
			setFieldLength (0);
// 100
			setJP_MatrixColumnKey_ID (0);
			setJP_MatrixRowKey_ID (0);
			setJP_MatrixWindow_ID (0);
			setJP_PageSize (0);
// 20
			setName (null);
			setValue (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_MatrixWindow (Properties ctx, String JP_MatrixWindow_UU, String trxName)
    {
      super (ctx, JP_MatrixWindow_UU, trxName);
      /** if (JP_MatrixWindow_UU == null)
        {
			setAD_Tab_ID (0);
			setAD_Window_ID (0);
			setFieldLength (0);
// 100
			setJP_MatrixColumnKey_ID (0);
			setJP_MatrixRowKey_ID (0);
			setJP_MatrixWindow_ID (0);
			setJP_PageSize (0);
// 20
			setName (null);
			setValue (null);
        } */
    }

    /** Standard Constructor */
    public X_JP_MatrixWindow (Properties ctx, String JP_MatrixWindow_UU, String trxName, String ... virtualColumns)
    {
      super (ctx, JP_MatrixWindow_UU, trxName, virtualColumns);
      /** if (JP_MatrixWindow_UU == null)
        {
			setAD_Tab_ID (0);
			setAD_Window_ID (0);
			setFieldLength (0);
// 100
			setJP_MatrixColumnKey_ID (0);
			setJP_MatrixRowKey_ID (0);
			setJP_MatrixWindow_ID (0);
			setJP_PageSize (0);
// 20
			setName (null);
			setValue (null);
        } */
    }

    /** Load Constructor */
    public X_JP_MatrixWindow (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 4 - System
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuilder sb = new StringBuilder ("X_JP_MatrixWindow[")
        .append(get_ID()).append(",Name=").append(getName()).append("]");
      return sb.toString();
    }

	public org.compiere.model.I_AD_Tab getAD_Tab() throws RuntimeException
	{
		return (org.compiere.model.I_AD_Tab)MTable.get(getCtx(), org.compiere.model.I_AD_Tab.Table_ID)
			.getPO(getAD_Tab_ID(), get_TrxName());
	}

	/** Set Tab.
		@param AD_Tab_ID Tab within a Window
	*/
	public void setAD_Tab_ID (int AD_Tab_ID)
	{
		if (AD_Tab_ID < 1)
			set_Value (COLUMNNAME_AD_Tab_ID, null);
		else
			set_Value (COLUMNNAME_AD_Tab_ID, Integer.valueOf(AD_Tab_ID));
	}

	/** Get Tab.
		@return Tab within a Window
	  */
	public int getAD_Tab_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Tab_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_Window getAD_Window() throws RuntimeException
	{
		return (org.compiere.model.I_AD_Window)MTable.get(getCtx(), org.compiere.model.I_AD_Window.Table_ID)
			.getPO(getAD_Window_ID(), get_TrxName());
	}

	/** Set Window.
		@param AD_Window_ID Data entry or display window
	*/
	public void setAD_Window_ID (int AD_Window_ID)
	{
		if (AD_Window_ID < 1)
			set_Value (COLUMNNAME_AD_Window_ID, null);
		else
			set_Value (COLUMNNAME_AD_Window_ID, Integer.valueOf(AD_Window_ID));
	}

	/** Get Window.
		@return Data entry or display window
	  */
	public int getAD_Window_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_Window_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Description.
		@param Description Optional short description of the record
	*/
	public void setDescription (String Description)
	{
		set_Value (COLUMNNAME_Description, Description);
	}

	/** Get Description.
		@return Optional short description of the record
	  */
	public String getDescription()
	{
		return (String)get_Value(COLUMNNAME_Description);
	}

	/** Set Length.
		@param FieldLength Length of the column in the database
	*/
	public void setFieldLength (int FieldLength)
	{
		set_Value (COLUMNNAME_FieldLength, Integer.valueOf(FieldLength));
	}

	/** Get Length.
		@return Length of the column in the database
	  */
	public int getFieldLength()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_FieldLength);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set SQL JOIN.
		@param JP_JoinClause SQL JOIN
	*/
	public void setJP_JoinClause (String JP_JoinClause)
	{
		set_Value (COLUMNNAME_JP_JoinClause, JP_JoinClause);
	}

	/** Get SQL JOIN.
		@return SQL JOIN	  */
	public String getJP_JoinClause()
	{
		return (String)get_Value(COLUMNNAME_JP_JoinClause);
	}

	public org.compiere.model.I_AD_Field getJP_MatrixColumnKey() throws RuntimeException
	{
		return (org.compiere.model.I_AD_Field)MTable.get(getCtx(), org.compiere.model.I_AD_Field.Table_ID)
			.getPO(getJP_MatrixColumnKey_ID(), get_TrxName());
	}

	/** Set Column Key Field.
		@param JP_MatrixColumnKey_ID Column Key Field
	*/
	public void setJP_MatrixColumnKey_ID (int JP_MatrixColumnKey_ID)
	{
		if (JP_MatrixColumnKey_ID < 1)
			set_Value (COLUMNNAME_JP_MatrixColumnKey_ID, null);
		else
			set_Value (COLUMNNAME_JP_MatrixColumnKey_ID, Integer.valueOf(JP_MatrixColumnKey_ID));
	}

	/** Get Column Key Field.
		@return Column Key Field	  */
	public int getJP_MatrixColumnKey_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_MatrixColumnKey_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_Field getJP_MatrixRowKey() throws RuntimeException
	{
		return (org.compiere.model.I_AD_Field)MTable.get(getCtx(), org.compiere.model.I_AD_Field.Table_ID)
			.getPO(getJP_MatrixRowKey_ID(), get_TrxName());
	}

	/** Set Row Key Field.
		@param JP_MatrixRowKey_ID Row Key Field
	*/
	public void setJP_MatrixRowKey_ID (int JP_MatrixRowKey_ID)
	{
		if (JP_MatrixRowKey_ID < 1)
			set_Value (COLUMNNAME_JP_MatrixRowKey_ID, null);
		else
			set_Value (COLUMNNAME_JP_MatrixRowKey_ID, Integer.valueOf(JP_MatrixRowKey_ID));
	}

	/** Get Row Key Field.
		@return Row Key Field	  */
	public int getJP_MatrixRowKey_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_MatrixRowKey_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Matrix Window.
		@param JP_MatrixWindow_ID Matrix Window
	*/
	public void setJP_MatrixWindow_ID (int JP_MatrixWindow_ID)
	{
		if (JP_MatrixWindow_ID < 1)
			set_ValueNoCheck (COLUMNNAME_JP_MatrixWindow_ID, null);
		else
			set_ValueNoCheck (COLUMNNAME_JP_MatrixWindow_ID, Integer.valueOf(JP_MatrixWindow_ID));
	}

	/** Get Matrix Window.
		@return Matrix Window	  */
	public int getJP_MatrixWindow_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_MatrixWindow_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set JPiere Matrix Window UU.
		@param JP_MatrixWindow_UU JPiere Matrix Window UU
	*/
	public void setJP_MatrixWindow_UU (String JP_MatrixWindow_UU)
	{
		set_ValueNoCheck (COLUMNNAME_JP_MatrixWindow_UU, JP_MatrixWindow_UU);
	}

	/** Get JPiere Matrix Window UU.
		@return JPiere Matrix Window UU	  */
	public String getJP_MatrixWindow_UU()
	{
		return (String)get_Value(COLUMNNAME_JP_MatrixWindow_UU);
	}

	/** Set Page Size.
		@param JP_PageSize Page Size
	*/
	public void setJP_PageSize (int JP_PageSize)
	{
		set_Value (COLUMNNAME_JP_PageSize, Integer.valueOf(JP_PageSize));
	}

	/** Get Page Size.
		@return Page Size	  */
	public int getJP_PageSize()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_PageSize);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Column info only = 01 */
	public static final String JP_QUICKENTRYCONF_ColumnInfoOnly = "01";
	/** Row info Only = 02 */
	public static final String JP_QUICKENTRYCONF_RowInfoOnly = "02";
	/** Column and Row info = 03 */
	public static final String JP_QUICKENTRYCONF_ColumnAndRowInfo = "03";
	/** Set Quick Entry Conf.
		@param JP_QuickEntryConf Quick Entry Conf
	*/
	public void setJP_QuickEntryConf (String JP_QuickEntryConf)
	{

		set_Value (COLUMNNAME_JP_QuickEntryConf, JP_QuickEntryConf);
	}

	/** Get Quick Entry Conf.
		@return Quick Entry Conf	  */
	public String getJP_QuickEntryConf()
	{
		return (String)get_Value(COLUMNNAME_JP_QuickEntryConf);
	}

	public org.compiere.model.I_AD_Window getJP_QuickEntryWindow() throws RuntimeException
	{
		return (org.compiere.model.I_AD_Window)MTable.get(getCtx(), org.compiere.model.I_AD_Window.Table_ID)
			.getPO(getJP_QuickEntryWindow_ID(), get_TrxName());
	}

	/** Set Quick Entry Window.
		@param JP_QuickEntryWindow_ID Quick Entry Window
	*/
	public void setJP_QuickEntryWindow_ID (int JP_QuickEntryWindow_ID)
	{
		if (JP_QuickEntryWindow_ID < 1)
			set_Value (COLUMNNAME_JP_QuickEntryWindow_ID, null);
		else
			set_Value (COLUMNNAME_JP_QuickEntryWindow_ID, Integer.valueOf(JP_QuickEntryWindow_ID));
	}

	/** Get Quick Entry Window.
		@return Quick Entry Window	  */
	public int getJP_QuickEntryWindow_ID()
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_JP_QuickEntryWindow_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Name.
		@param Name Alphanumeric identifier of the entity
	*/
	public void setName (String Name)
	{
		set_Value (COLUMNNAME_Name, Name);
	}

	/** Get Name.
		@return Alphanumeric identifier of the entity
	  */
	public String getName()
	{
		return (String)get_Value(COLUMNNAME_Name);
	}

	/** Set Search Key.
		@param Value Search key for the record in the format required - must be unique
	*/
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue()
	{
		return (String)get_Value(COLUMNNAME_Value);
	}

    /** Get Record ID/ColumnName
        @return ID/ColumnName pair
      */
    public KeyNamePair getKeyNamePair()
    {
        return new KeyNamePair(get_ID(), getValue());
    }

	/** Set Sql WHERE.
		@param WhereClause Fully qualified SQL WHERE clause
	*/
	public void setWhereClause (String WhereClause)
	{
		set_Value (COLUMNNAME_WhereClause, WhereClause);
	}

	/** Get Sql WHERE.
		@return Fully qualified SQL WHERE clause
	  */
	public String getWhereClause()
	{
		return (String)get_Value(COLUMNNAME_WhereClause);
	}
}