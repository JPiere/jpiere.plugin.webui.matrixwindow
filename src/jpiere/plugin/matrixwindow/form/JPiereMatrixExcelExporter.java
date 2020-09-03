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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.compiere.model.GridField;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;


/**
 * Matrix Window Excel Exporter
 *
 * JPIERE-0098
 *
 * @author Hideaki Hagiwara(h.hagiwara@oss-erp.co.jp)
 *
 */
public class JPiereMatrixExcelExporter {

	private JPiereMatrixWindow matrixWindow;
	private HSSFWorkbook m_workbook;

	public JPiereMatrixExcelExporter(JPiereMatrixWindow matrixWindow)
	{
		this.matrixWindow = matrixWindow;
		m_workbook = new HSSFWorkbook();
	}


	public void export(File file) throws IOException
	{
		HSSFSheet sheet= m_workbook.createSheet();
		createTableHeader(sheet);
		addRecords(sheet);

		FileOutputStream out;
		try {
			out = new FileOutputStream(file);
			m_workbook.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}


	protected void createHeaderFooter(HSSFSheet sheet)
	{
		// Sheet Header
		HSSFHeader header = sheet.getHeader();
		header.setRight(HSSFHeader.page()+ " / "+HSSFHeader.numPages());
		// Sheet Footer
		HSSFFooter footer = sheet.getFooter();
		footer.setLeft(Env.getStandardReportFooterTrademarkText());
		footer.setCenter(Env.getHeader(Env.getCtx(), 0));
	}

	private void createTableHeader(HSSFSheet sheet)
	{
		int colCounter = 0;

		HashMap<Object,String> columnKeyNameMap = matrixWindow.getColumnKeyNameMap();
		ArrayList<Object>  ColumnKeys = matrixWindow.getColumnKeys();

		//First Row
		HSSFRow row = sheet.createRow(0);
		HSSFCell cell = row.createCell(colCounter);//Spaece
		colCounter++;

		int cellLength = matrixWindow.getContentFields().length;
		for(Object obj : ColumnKeys)
		{
			cell = row.createCell(colCounter);
			String name = columnKeyNameMap.get(obj);
			cell.setCellValue(name);

			if(cellLength > 1)
			{
				sheet.addMergedRegion(new CellRangeAddress(0,0, colCounter, (colCounter + matrixWindow.getContentFields().length-1) ));
			}
			colCounter = colCounter + matrixWindow.getContentFields().length;
			;
		}

		//Second Row
		colCounter = 0;
		row = sheet.createRow(1);
		HashMap<Integer, String> columnNameMap = matrixWindow.getColumnNameMap();
		for(int i = 0; i < columnNameMap.size(); i++)
		{
			cell = row.createCell(colCounter);
			String name = columnNameMap.get(i);
			cell.setCellValue(name);
			colCounter++;
		}


	}

	private void addRecords(HSSFSheet sheet)
	{
		LinkedHashMap<Object,TreeMap<Integer,Object>> viewModel = matrixWindow.getViewModel();
		ArrayList<Object> rowKeys = matrixWindow.getRowKeys();
		HashMap<Integer,GridField> columnGridFieldMap = matrixWindow.getColumnGridFieldMap();

		JPMatrixGridRowRenderer renderer = matrixWindow.getRowRenderer();

		TreeMap<Integer,Object> objectList = null;
		HSSFRow row = null;
		HSSFCell cell = null;
		GridField gField = null;
		int rowCounter = 0;
		int displayType = 0;
		for(Object rowKey : rowKeys)
		{
			objectList = viewModel.get(rowKey);
			row = sheet.createRow(rowCounter + 2);
			for (int col = 0; col < objectList.size(); col++)
			{
				Object obj_Value = objectList.get(col);
				if(obj_Value==null)
					continue;

				cell = row.createCell(col);
				gField = columnGridFieldMap.get(col);
				displayType = gField.getDisplayType();


				if (DisplayType.isDate(displayType))
				{
					Timestamp value = null;
					if (obj_Value instanceof Date)
						value = new Timestamp(((Date)obj_Value).getTime());
					else
						value = (Timestamp)obj_Value;
					cell.setCellValue(value);

				}else if (DisplayType.isNumeric(displayType)) {

					double value = 0;
					if (obj_Value instanceof Number) {
						value = ((Number)obj_Value).doubleValue();
					}
					cell.setCellValue(value);

				}else if (DisplayType.YesNo == displayType) {

					boolean value = false;
					if (obj_Value instanceof Boolean)
						value = (Boolean)obj_Value;
					else
						value = "Y".equals(obj_Value);
					cell.setCellValue(new HSSFRichTextString(Msg.getMsg(Env.getAD_Language(Env.getCtx()), value == true ? "Y" : "N")));

				}else if(DisplayType.isID(displayType) || DisplayType.List == displayType){

					String  text = renderer.getDisplayText(obj_Value, gField, rowCounter + 2,false);
					cell.setCellValue(new HSSFRichTextString(text));


				}else {
					String value = fixString(obj_Value.toString());	//	formatted
					cell.setCellValue(new HSSFRichTextString(value));
				}


				HSSFCellStyle style = getStyle(col, displayType);
				cell.setCellStyle(style);


			}

			rowCounter++;

		}//for

	}

	private String fixString(String str)
	{
		// ms excel doesn't support UTF8 charset
		return Util.stripDiacritics(str);
	}

	private HashMap<String, HSSFCellStyle> m_styles = new HashMap<String, HSSFCellStyle>();

	private HSSFCellStyle getStyle(int col, int displayType)
	{
		String key = "cell-"+col+"-"+displayType;
		HSSFCellStyle cs = m_styles.get(key);
		if (cs == null)
		{
			cs = m_workbook.createCellStyle();
//			HSSFFont font = getFont(false);
//			cs.setFont(font);
			// Border
//			cs.setBorderLeft(BorderStyle.THIN);   //JPIERE-0463 would not like to import "org.apache.poi.ss.usermodel"
//			cs.setBorderTop(BorderStyle.THIN);    //JPIERE-0463 would not like to import "org.apache.poi.ss.usermodel"
//			cs.setBorderRight(BorderStyle.THIN);  //JPIERE-0463 would not like to import "org.apache.poi.ss.usermodel"
//			cs.setBorderBottom(BorderStyle.THIN); //JPIERE-0463 would not like to import "org.apache.poi.ss.usermodel"
			//
			String cellFormat = getCellFormat(displayType);
//			if (cellFormat != null)
//				cs.setDataFormat(m_dataFormat.getFormat(cellFormat));
			m_styles.put(key, cs);
		}
		return cs;

	}

	protected String getCellFormat(int displayType) {
		boolean isHighlightNegativeNumbers = true;

		String cellFormat = null;

//		if (DisplayType.isDate(displayType)) {
//			cellFormat = DisplayType.getDateFormat(getLanguage()).toPattern();
//		} else

		if (DisplayType.isNumeric(displayType)) {
			DecimalFormat df = DisplayType.getNumberFormat(displayType, Env.getLanguage(Env.getCtx()));
			cellFormat = getFormatString(df, isHighlightNegativeNumbers);
		}

		return cellFormat;
	}

	private String getFormatString(NumberFormat df, boolean isHighlightNegativeNumbers) {
		StringBuffer format = new StringBuffer();
		int integerDigitsMin = df.getMinimumIntegerDigits();
		int integerDigitsMax = df.getMaximumIntegerDigits();
		for (int i = 0; i < integerDigitsMax; i++) {
			if (i < integerDigitsMin)
				format.insert(0, "0");
			else
				format.insert(0, "#");
			if (i == 2) {
				format.insert(0, ",");
			}
		}
		int fractionDigitsMin = df.getMinimumFractionDigits();
		int fractionDigitsMax = df.getMaximumFractionDigits();
		for (int i = 0; i < fractionDigitsMax; i++) {
			if (i == 0)
				format.append(".");
			if (i < fractionDigitsMin)
				format.append("0");
			else
				format.append("#");
		}
		if (isHighlightNegativeNumbers) {
			String f = format.toString();
			format = new StringBuffer(f).append(";[RED]-").append(f);
		}
		//
		//if (log.isLoggable(Level.FINEST)) log.finest("NumberFormat: "+format);

		return format.toString();

	}
}
