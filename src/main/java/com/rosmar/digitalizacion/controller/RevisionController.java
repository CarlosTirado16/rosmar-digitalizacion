package com.rosmar.digitalizacion.controller;

import com.rosmar.digitalizacion.model.ItemSSOP;
import com.rosmar.digitalizacion.service.ItemSSOPService;
import com.rosmar.digitalizacion.service.RegistroSSOPService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/revision")
public class RevisionController {

    private final ItemSSOPService itemSSOPService;
    private final RegistroSSOPService registroSSOPService;

    public RevisionController(ItemSSOPService itemSSOPService,
                              RegistroSSOPService registroSSOPService) {
        this.itemSSOPService = itemSSOPService;
        this.registroSSOPService = registroSSOPService;
    }

    @PostMapping("/confirmar")
    public String confirmar(@RequestParam Map<String, String> params,
                            RedirectAttributes redirectAttributes) {

        Long registroId = Long.parseLong(params.get("registroId"));
        List<ItemSSOP> items = itemSSOPService.listarPorRegistro(registroId);

        // Actualizar cada item con los valores del formulario
        for (int i = 0; i < items.size(); i++) {
            ItemSSOP item = items.get(i);

            String area = params.get("items[" + i + "].area");
            String status = params.get("items[" + i + "].status");
            String deviation = params.get("items[" + i + "].deviation");
            boolean rewashed = params.containsKey("items[" + i + "].rewashed");
            boolean rinsed = params.containsKey("items[" + i + "].rinsed");
            boolean sanitized = params.containsKey("items[" + i + "].sanitized");
            boolean notInUse = params.containsKey("items[" + i + "].notInUse");

            if (area != null) item.setArea(area);
            if (status != null) item.setStatus(status);
            item.setDeviation(deviation != null && !deviation.isEmpty() ? deviation : null);
            item.setRewashed(rewashed);
            item.setRinsed(rinsed);
            item.setSanitized(sanitized);
            item.setNotInUse(notInUse);

            itemSSOPService.guardar(item);
        }

        redirectAttributes.addFlashAttribute("mensaje", "Registro confirmado y guardado correctamente.");
        return "redirect:/dashboard";
    }
}