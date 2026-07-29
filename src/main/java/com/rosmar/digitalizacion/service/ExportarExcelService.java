package com.rosmar.digitalizacion.service;

import com.rosmar.digitalizacion.model.ItemSSOP;
import com.rosmar.digitalizacion.model.RegistroSSOP;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import com.rosmar.digitalizacion.repository.ItemSSOPRepository;

@Service
public class ExportarExcelService {

    private final ItemSSOPRepository itemSSOPRepository;

    public ExportarExcelService(ItemSSOPRepository itemSSOPRepository) {
        this.itemSSOPRepository = itemSSOPRepository;
    }

    public byte[] exportarRegistros(List<RegistroSSOP> registros) throws IOException {
        System.setProperty("java.awt.headless", "true");

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            CellStyle altStyle = workbook.createCellStyle();
            altStyle.cloneStyleFrom(dataStyle);
            altStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            altStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (RegistroSSOP registro : registros) {
                registro.setItems(itemSSOPRepository.findByRegistroId(registro.getId()));

                String sheetName = "Reg-" + registro.getId();
                Sheet sheet = workbook.createSheet(sheetName);
                int rowNum = 0;

                Row titleRow = sheet.createRow(rowNum++);
                titleRow.createCell(0).setCellValue("Registro #" + registro.getId() +
                        " | Fecha: " + registro.getFecha() +
                        " | Por: " + registro.getRegistradoPor().getNombre() +
                        " " + registro.getRegistradoPor().getApellido());

                rowNum++;

                Row paTitle = sheet.createRow(rowNum++);
                paTitle.createCell(0).setCellValue("PROCESSING AREA");

                rowNum = crearEncabezados(sheet, workbook, rowNum, headerStyle);
                rowNum = crearFilasItems(sheet, workbook, rowNum, registro.getItems(),
                        ItemSSOP.Seccion.PROCESSING_AREA, dataStyle, altStyle);

                rowNum++;

                Row pakTitle = sheet.createRow(rowNum++);
                pakTitle.createCell(0).setCellValue("PACKAGING AREA");

                rowNum = crearEncabezados(sheet, workbook, rowNum, headerStyle);
                rowNum = crearFilasItems(sheet, workbook, rowNum, registro.getItems(),
                        ItemSSOP.Seccion.PACKAGING_AREA, dataStyle, altStyle);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private int crearEncabezados(Sheet sheet, Workbook workbook, int rowNum, CellStyle headerStyle) {
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"#", "Área / Equipo", "Frecuencia", "Status",
                "Deviation", "Rewashed", "Rinsed", "Sanitized", "Not in use"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        return rowNum;
    }

    private int crearFilasItems(Sheet sheet, Workbook workbook, int rowNum,
                                List<ItemSSOP> items, ItemSSOP.Seccion seccion,
                                CellStyle dataStyle, CellStyle altStyle) {
        System.out.println("Procesando seccion: " + seccion + " de " + items.size() + " items");
        int count = 0;
        for (ItemSSOP item : items) {
            System.out.println("Item: " + item.getArea() + " seccion: " + item.getSeccion());
            if (item.getSeccion() != seccion) continue;

            Row row = sheet.createRow(rowNum++);
            System.out.println("Creando fila en row: " + (rowNum-1) + " para: " + item.getArea());
            CellStyle style = count % 2 == 0 ? dataStyle : altStyle;

            crearCelda(row, 0, item.getMpsReferenceNumber(), style);
            crearCelda(row, 1, item.getArea(), style);
            crearCelda(row, 2, item.getFrecuencia() != null ? item.getFrecuencia().name() : "", style);
            crearCelda(row, 3, item.getStatus() != null ? item.getStatus() : "", style);
            crearCelda(row, 4, item.getDeviation() != null ? item.getDeviation() : "", style);
            crearCelda(row, 5, item.getRewashed() != null && item.getRewashed() ? "✓" : "", style);
            crearCelda(row, 6, item.getRinsed() != null && item.getRinsed() ? "✓" : "", style);
            crearCelda(row, 7, item.getSanitized() != null && item.getSanitized() ? "✓" : "", style);
            crearCelda(row, 8, item.getNotInUse() != null && item.getNotInUse() ? "✓" : "", style);

            count++;
        }
        return rowNum;
    }

    private void crearCelda(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value != null ? value : "");
        cell.setCellStyle(style);
    }
}