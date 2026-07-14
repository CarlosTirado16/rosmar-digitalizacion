package com.rosmar.digitalizacion.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@Service
public class ClaudeVisionService {

    @Value("${anthropic.api.key}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String extraerDatosFormato(MultipartFile imagen) throws Exception {
        // Convertir imagen a Base64
        byte[] imageBytes = imagen.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
        String mediaType = imagen.getContentType() != null ? imagen.getContentType() : "image/jpeg";

        // Construir el prompt
        String prompt = """
                Analiza esta imagen del formato SSOP-R-PO Pre-Operational Log y extrae TODOS los datos en formato JSON.
                
                El formato tiene dos secciones: PROCESSING AREA y PACKAGING AREA.
                Cada sección tiene una lista de ítems con los siguientes campos:
                - mps_reference_number: número de referencia (puede ser un número o estar vacío)
                - area: nombre del equipo o área
                - frecuencia: "DAILY" si el status está en la columna Daily, "ONCE_A_WEEK" si está en la columna Once a week
                - status: el valor marcado (puede ser "✓", "X", "NA", o vacío si no está marcado)
                - deviation: texto escrito en la columna Deviation (vacío si no hay)
                - corrective_action: objeto con los campos rewashed, rinsed, sanitized, not_in_use (true/false según si están marcados)
                
                También extrae:
                - fecha: la fecha al pie del formato (formato MM/DD/YY)
            
                
                IMPORTANTE:
                - Lista TODOS los ítems de cada sección, uno por uno, sin agrupar ni omitir ninguno
                - Si un campo está vacío o ilegible, usa null
                - Responde ÚNICAMENTE con el JSON, sin texto adicional, sin bloques de código
                
                Formato de respuesta:
                {
                  "fecha": "",
                  "processing_area": [
                    {
                      "mps_reference_number": "",
                      "area": "",
                      "frecuencia": "",
                      "status": "",
                      "deviation": "",
                      "corrective_action": {
                        "rewashed": false,
                        "rinsed": false,
                        "sanitized": false,
                        "not_in_use": false
                      }
                    }
                  ],
                  "packaging_area": []
                }
                """;

        // Construir el cuerpo de la petición
        String requestBody = objectMapper.writeValueAsString(
                objectMapper.createObjectNode()
                        .put("model", "claude-opus-4-5")
                        .put("max_tokens", 8192)
                        .set("messages", objectMapper.createArrayNode().add(
                                objectMapper.createObjectNode()
                                        .put("role", "user")
                                        .set("content", objectMapper.createArrayNode()
                                                .add(objectMapper.createObjectNode()
                                                        .put("type", "image")
                                                        .set("source", objectMapper.createObjectNode()
                                                                .put("type", "base64")
                                                                .put("media_type", mediaType)
                                                                .put("data", base64Image)))
                                                .add(objectMapper.createObjectNode()
                                                        .put("type", "text")
                                                        .put("text", prompt)))
                        ))
        );

        // Hacer la petición a la API
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.anthropic.com/v1/messages"))
                .header("Content-Type", "application/json")
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        // Parsear la respuesta
        JsonNode responseNode = objectMapper.readTree(response.body());
        return responseNode.get("content").get(0).get("text").asText();
    }
}