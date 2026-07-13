package com.rosmar.digitalizacion.controller;

import com.rosmar.digitalizacion.model.RegistroSSOP;
import com.rosmar.digitalizacion.model.Usuario;
import com.rosmar.digitalizacion.repository.UsuarioRepository;
import com.rosmar.digitalizacion.service.ArchivoService;
import com.rosmar.digitalizacion.service.RegistroSSOPService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;

@Controller
@RequestMapping("/subida")
public class SubidaController {

    private final ArchivoService archivoService;
    private final RegistroSSOPService registroSSOPService;
    private final UsuarioRepository usuarioRepository;

    public SubidaController(ArchivoService archivoService,
                            RegistroSSOPService registroSSOPService,
                            UsuarioRepository usuarioRepository) {
        this.archivoService = archivoService;
        this.registroSSOPService = registroSSOPService;
        this.usuarioRepository = usuarioRepository;
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

            // Crear registro
            RegistroSSOP registro = new RegistroSSOP();
            registro.setFecha(LocalDate.now());
            registro.setImagenPath(nombreArchivo);
            registro.setRegistradoPor(usuario);

            // Guardar en BD
            registroSSOPService.guardar(registro);

            model.addAttribute("mensaje", "Imagen subida correctamente.");
            model.addAttribute("imagenPath", nombreArchivo);
            return "subida/confirmacion";

        } catch (Exception e) {
            model.addAttribute("error", "Error al subir la imagen: " + e.getMessage());
            return "subida/formulario";
        }
    }
}