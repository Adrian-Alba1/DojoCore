package org.dojocore.Repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.dojocore.Model.ResultadoEvento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResultadoEventoRepository extends JpaRepository<ResultadoEvento, Integer> {

    List<ResultadoEvento> findByTiposEventoNombreOrderById(String nombre);
}
