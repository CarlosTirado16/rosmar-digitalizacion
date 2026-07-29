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

            // Estilos
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font headerFont = workbook.createFont();
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
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

            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);

            // Crear hoja por cada registro
            for (RegistroSSOP registro : registros) {
                registro.setItems(itemSSOPRepository.findByRegistroId(registro.getId()));
                System.out.println("Registro " + registro.getId() + " tiene " + registro.getItems().size() + " items");
                String sheetName = "Registro " + registro.getId();
                Sheet sheet = workbook.createSheet(sheetName);

                int rowNum = 0;

                // Título
                Row titleRow = sheet.createRow(rowNum++);
                Cell titleCell = titleRow.createCell(0);
                titleCell.setCellValue("SSOP-R-PO Pre-Operational Log — Registro #" + registro.getId());
                titleCell.setCellStyle(titleStyle);
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

                // Info del registro
                Row infoRow = sheet.createRow(rowNum++);
                infoRow.createCell(0).setCellValue("Fecha: " + registro.getFecha());
                infoRow.createCell(3).setCellValue("Registrado por: " +
                        registro.getRegistradoPor().getNombre() + " " +
                        registro.getRegistradoPor().getApellido());
                sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 2));
                sheet.addMergedRegion(new CellRangeAddress(1, 1, 3, 8));

                rowNum++; // espacio

                // Processing Area
                Row processingTitle = sheet.createRow(rowNum++);
                Cell ptCell = processingTitle.createCell(0);
                ptCell.setCellValue("PROCESSING AREA");
                CellStyle ptStyle = workbook.createCellStyle();
                ptStyle.cloneStyleFrom(headerStyle);
                ptCell.setCellStyle(ptStyle);
                sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 8));

                rowNum = crearEncabezados(sheet, workbook, rowNum, headerStyle);
                rowNum = crearFilasItems(sheet, workbook, rowNum, registro.getItems(),
                        ItemSSOP.Seccion.PROCESSING_AREA, dataStyle, altStyle);

                rowNum++; // espacio

                // Packaging Area
                Row packagingTitle = sheet.createRow(rowNum++);
                Cell pakCell = packagingTitle.createCell(0);
                pakCell.setCellValue("PACKAGING AREA");
                pakCell.setCellStyle(ptStyle);
                sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 8));

                rowNum = crearEncabezados(sheet, workbook, rowNum, headerStyle);
                rowNum = crearFilasItems(sheet, workbook, rowNum, registro.getItems(),
                        ItemSSOP.Seccion.PACKAGING_AREA, dataStyle, altStyle);
                System.out.println("Items totales para procesar: " + registro.getItems().size());
                long processingCount = registro.getItems().stream()
                        .filter(i -> i.getSeccion() == ItemSSOP.Seccion.PROCESSING_AREA)
                        .count();
                System.out.println("Items Processing Area: " + processingCount);

                // Ajustar ancho de columnas
                sheet.autoSizeColumn(0);
                sheet.autoSizeColumn(1);
                sheet.setColumnWidth(2, 3000);
                sheet.setColumnWidth(3, 3000);
                sheet.setColumnWidth(4, 6000);
                sheet.autoSizeColumn(5);
                sheet.autoSizeColumn(6);
                sheet.autoSizeColumn(7);
                sheet.autoSizeColumn(8);
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