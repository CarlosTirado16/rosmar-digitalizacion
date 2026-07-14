package com.rosmar.digitalizacion.repository;

import com.rosmar.digitalizacion.model.ItemSSOP;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemSSOPRepository extends JpaRepository<ItemSSOP, Long> {
    List<ItemSSOP> findByRegistroId(Long registroId);
    List<ItemSSOP> findByRegistroIdAndSeccion(Long registroId, ItemSSOP.Seccion seccion);
}