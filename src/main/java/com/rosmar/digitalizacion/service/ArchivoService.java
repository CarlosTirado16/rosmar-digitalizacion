package com.rosmar.digitalizacion.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ArchivoService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public String guardarImagen(MultipartFile archivo) throws IOException {
        // Crear carpeta si no existe
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generar nombre único para el archivo
        String extension = obtenerExtension(archivo.getOriginalFilename());
        String nombreArchivo = UUID.randomUUID().toString() + "." + extension;

        // Guardar el archivo
        Path rutaArchivo = uploadPath.resolve(nombreArchivo);
        Files.copy(archivo.getInputStream(), rutaArchivo);

        return nombreArchivo;
    }

    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || !nombreArchivo.contains(".")) {
            return "jpg";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf(".") + 1);
    }
}