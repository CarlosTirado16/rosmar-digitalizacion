package com.rosmar.digitalizacion.service;

import com.rosmar.digitalizacion.model.RegistroSSOP;
import com.rosmar.digitalizacion.repository.RegistroSSOPRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class RegistroSSOPService {

    private final RegistroSSOPRepository registroSSOPRepository;

    public RegistroSSOPService(RegistroSSOPRepository registroSSOPRepository) {
        this.registroSSOPRepository = registroSSOPRepository;
    }

    public RegistroSSOP guardar(RegistroSSOP registro) {
        return registroSSOPRepository.save(registro);
    }

    public List<RegistroSSOP> listarTodos() {
        return registroSSOPRepository.findAll();
    }

    public List<RegistroSSOP> listarPorRangoDeFechas(LocalDate inicio, LocalDate fin) {
        return registroSSOPRepository.findByFechaBetween(inicio, fin);
    }
}