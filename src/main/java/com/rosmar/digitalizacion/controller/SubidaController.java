package com.rosmar.digitalizacion.controller;

import com.rosmar.digitalizacion.model.ItemSSOP;
import com.rosmar.digitalizacion.model.RegistroSSOP;
import com.rosmar.digitalizacion.model.Usuario;
import com.rosmar.digitalizacion.repository.UsuarioRepository;
import com.rosmar.digitalizacion.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/subida")
public class SubidaController {

    private final ArchivoService archivoService;
    private final RegistroSSOPService registroSSOPService;
    private final UsuarioRepository usuarioRepository;
    private final ClaudeVisionService claudeVisionService;
    private final ProcesadorSSOPService procesadorSSOPService;
    private final ItemSSOPService itemSSOPService;

    public SubidaController(ArchivoService archivoService,
                            RegistroSSOPService registroSSOPService,
                            UsuarioRepository usuarioRepository,
                            ClaudeVisionService claudeVisionService,
                            ProcesadorSSOPService procesadorSSOPService,
                            ItemSSOPService itemSSOPService) {
        this.archivoService = archivoService;
        this.registroSSOPService = registroSSOPService;
        this.usuarioRepository = usuarioRepository;
        this.claudeVisionService = claudeVisionService;
        this.procesadorSSOPService = procesadorSSOPService;
        this.itemSSOPService = itemSSOPService;
    }

    @GetMapping
    public String mostrarFormulario() {
        return "subida/formulario";
    }

    @PostMapping
    public String procesarSubida(@RequestParam("archivo") MultipartFile archivo,
                                 Principal principal,
                                 Model model) {
        try {
            // Guardar imagen en disco
            String nombreArchivo = archivoService.guardarImagen(archivo);

            // Obtener usuario autenticado
            Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                    .orElseThrow();

            // Crear registro inicial
            RegistroSSOP registro = new RegistroSSOP();
            registro.setFecha(LocalDate.now());
            registro.setImagenPath(nombreArchivo);
            registro.setRegistradoPor(usuario);
            RegistroSSOP registroGuardado = registroSSOPService.guardar(registro);

            // Extraer datos con Claude Vision API
            String jsonRespuesta = claudeVisionService.extraerDatosFormato(archivo);

            // Procesar y guardar los ítems
            List<ItemSSOP> items = procesadorSSOPService.procesarRespuestaIA(jsonRespuesta, registroGuardado);
            itemSSOPService.guardarTodos(items);

            // Actualizar fecha del registro si fue extraída
            registroSSOPService.guardar(registroGuardado);

            model.addAttribute("mensaje", "Formato procesado correctamente.");
            model.addAttribute("imagenPath", nombreArchivo);
            model.addAttribute("items", items);
            model.addAttribute("registro", registroGuardado);
            return "subida/confirmacion";

        } catch (Exception e) {
            model.addAttribute("error", "Error al procesar el formato: " + e.getMessage());
            return "subida/formulario";
        }
    }
}