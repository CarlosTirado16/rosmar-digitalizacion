package com.rosmar.digitalizacion.repository;

import com.rosmar.digitalizacion.model.RegistroSSOP;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface RegistroSSOPRepository extends JpaRepository<RegistroSSOP, Long> {
    List<RegistroSSOP> findByFechaBetween(LocalDate inicio, LocalDate fin);
    List<RegistroSSOP> findByRegistradoPorId(Long usuarioId);
}