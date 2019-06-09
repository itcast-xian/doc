package cn.itcast.demo;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class WriteExcelDemo {

    public static void main(String[] args) {

        Workbook workbook = new HSSFWorkbook();

        Sheet sheet = workbook.createSheet("DATA1");

        Row row = sheet.createRow(0);

        Cell cell = row.createCell(0);
        cell.setCellValue("金毛狮王");

        Cell cell1 = row.createCell(1);
        cell1.setCellValue("金毛狮王6666");

        try {
            workbook.write(new FileOutputStream(new File("D:/1.xls")));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
