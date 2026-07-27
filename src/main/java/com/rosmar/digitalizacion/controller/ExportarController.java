package com.rosmar.digitalizacion.controller;

import com.rosmar.digitalizacion.model.RegistroSSOP;
import com.rosmar.digitalizacion.service.ExportarExcelService;
import com.rosmar.digitalizacion.service.RegistroSSOPService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/exportar")
public class ExportarController {

    private final ExportarExcelService exportarExcelService;
    private final RegistroSSOPService registroSSOPService;

    public ExportarController(ExportarExcelService exportarExcelService,
                              RegistroSSOPService registroSSOPService) {
        this.exportarExcelService = exportarExcelService;
        this.registroSSOPService = registroSSOPService;
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) throws Exception {

        List<RegistroSSOP> registros;


        if (inicio != null && fin != null) {
            registros = registroSSOPService.listarPorRangoDeFechas(inicio, fin);
        } else {
            registros = registroSSOPService.listarTodos();
        }

        System.out.println("Exportando " + registros.size() + " registros");

        byte[] excelBytes = exportarExcelService.exportarRegistros(registros);

        String filename = "rosmar-ssop-" + LocalDate.now() + ".xlsx";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }
}