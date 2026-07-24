package com.rosmar.digitalizacion.controller;

import com.rosmar.digitalizacion.model.RegistroSSOP;
import com.rosmar.digitalizacion.service.RegistroSSOPService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/historial")
public class HistorialController {

    private final RegistroSSOPService registroSSOPService;

    public HistorialController(RegistroSSOPService registroSSOPService) {
        this.registroSSOPService = registroSSOPService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false)
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
                         @RequestParam(required = false)
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin,
                         Model model) {

        List<RegistroSSOP> registros;

        if (inicio != null && fin != null) {
            registros = registroSSOPService.listarPorRangoDeFechas(inicio, fin);
            model.addAttribute("inicio", inicio);
            model.addAttribute("fin", fin);
        } else {
            registros = registroSSOPService.listarTodos();
        }

        model.addAttribute("registros", registros);
        return "historial/lista";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        RegistroSSOP registro = registroSSOPService.buscarPorId(id).orElseThrow();
        model.addAttribute("registro", registro);
        return "historial/detalle";
    }
}