package com.rosmar.digitalizacion.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rosmar.digitalizacion.model.ItemSSOP;
import com.rosmar.digitalizacion.model.RegistroSSOP;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProcesadorSSOPService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<ItemSSOP> procesarRespuestaIA(String jsonRespuesta, RegistroSSOP registro) throws Exception {
        // Limpiar bloques de código markdown si los hay
        jsonRespuesta = jsonRespuesta.trim();
        if (jsonRespuesta.startsWith("```")) {
            jsonRespuesta = jsonRespuesta.replaceAll("```json", "").replaceAll("```", "").trim();
        }
        JsonNode root = objectMapper.readTree(jsonRespuesta);
        List<ItemSSOP> items = new ArrayList<>();

        // Actualizar fecha del registro si está disponible
        if (root.has("fecha") && !root.get("fecha").isNull()) {
            try {
                String fechaStr = root.get("fecha").asText();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");
                registro.setFecha(LocalDate.parse(fechaStr, formatter));
            } catch (Exception e) {
                // Si no se puede parsear la fecha, se deja la fecha actual
            }
        }

        // Procesar Processing Area
        if (root.has("processing_area")) {
            for (JsonNode itemNode : root.get("processing_area")) {
                ItemSSOP item = crearItem(itemNode, registro, ItemSSOP.Seccion.PROCESSING_AREA);
                items.add(item);
            }
        }

        // Procesar Packaging Area
        if (root.has("packaging_area")) {
            for (JsonNode itemNode : root.get("packaging_area")) {
                ItemSSOP item = crearItem(itemNode, registro, ItemSSOP.Seccion.PACKAGING_AREA);
                items.add(item);
            }
        }

        return items;
    }

    private ItemSSOP crearItem(JsonNode itemNode, RegistroSSOP registro, ItemSSOP.Seccion seccion) {
        ItemSSOP item = new ItemSSOP();
        item.setRegistro(registro);
        item.setSeccion(seccion);

        if (itemNode.has("mps_reference_number") && !itemNode.get("mps_reference_number").isNull()) {
            item.setMpsReferenceNumber(itemNode.get("mps_reference_number").asText());
        }

        if (itemNode.has("area") && !itemNode.get("area").isNull()) {
            item.setArea(itemNode.get("area").asText());
        }

        if (itemNode.has("status") && !itemNode.get("status").isNull()) {
            item.setStatus(itemNode.get("status").asText());
        }

        if (itemNode.has("deviation") && !itemNode.get("deviation").isNull()) {
            String deviation = itemNode.get("deviation").asText();
            if (!deviation.isEmpty()) {
                item.setDeviation(deviation);
            }
        }

        if (itemNode.has("frecuencia") && !itemNode.get("frecuencia").isNull()) {
            String frecuencia = itemNode.get("frecuencia").asText();
            if (frecuencia.equals("ONCE_A_WEEK")) {
                item.setFrecuencia(ItemSSOP.Frecuencia.ONCE_A_WEEK);
            } else {
                item.setFrecuencia(ItemSSOP.Frecuencia.DAILY);
            }
        }

        if (itemNode.has("corrective_action")) {
            JsonNode ca = itemNode.get("corrective_action");
            if (ca.has("rewashed")) item.setRewashed(ca.get("rewashed").asBoolean());
            if (ca.has("rinsed")) item.setRinsed(ca.get("rinsed").asBoolean());
            if (ca.has("sanitized")) item.setSanitized(ca.get("sanitized").asBoolean());
            if (ca.has("not_in_use")) item.setNotInUse(ca.get("not_in_use").asBoolean());
        }

        return item;
    }
}