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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFFooter;
import org.apache.poi.hssf.usermodel.HSSFHeader;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.compiere.model.GridField;
import org.compiere.util.Env;


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

		for(Object obj : ColumnKeys)
		{
			cell = row.createCell(colCounter);
			String name = columnKeyNameMap.get(obj);
			cell.setCellValue(name);

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
		for(Object rowKey : rowKeys)
		{
			objectList = viewModel.get(rowKey);
			row = sheet.createRow(rowCounter + 2);
			for (int col = 0; col < objectList.size(); col++)
			{
				Object value = objectList.get(col);
				if(value==null)
					continue;

				cell = row.createCell(col);
				gField = columnGridFieldMap.get(col);
				String  text = renderer.getDisplayText(value,gField, rowCounter + 2,false);
				cell.setCellValue(text);

			}

			rowCounter++;

		}//for

	}



}
