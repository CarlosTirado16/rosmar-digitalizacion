package com.rosmar.digitalizacion.service;

import com.rosmar.digitalizacion.model.ItemSSOP;
import com.rosmar.digitalizacion.repository.ItemSSOPRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemSSOPService {

    private final ItemSSOPRepository itemSSOPRepository;

    public ItemSSOPService(ItemSSOPRepository itemSSOPRepository) {
        this.itemSSOPRepository = itemSSOPRepository;
    }

    public ItemSSOP guardar(ItemSSOP item) {
        return itemSSOPRepository.save(item);
    }

    public List<ItemSSOP> guardarTodos(List<ItemSSOP> items) {
        return itemSSOPRepository.saveAll(items);
    }

    public List<ItemSSOP> listarPorRegistro(Long registroId) {
        return itemSSOPRepository.findByRegistroId(registroId);
    }

    public List<ItemSSOP> listarPorRegistroYSeccion(Long registroId, ItemSSOP.Seccion seccion) {
        return itemSSOPRepository.findByRegistroIdAndSeccion(registroId, seccion);
    }
}