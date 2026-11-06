package org.dojocore.Repository;

import org.dojocore.Model.HistorialGrado;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistorialGradoRepository extends JpaRepository<HistorialGrado, Integer> {

    @EntityGraph(attributePaths = {"gradoAnterior", "gradoNuevo"})
    List<HistorialGrado> findByPersonaIdOrderByFechaDescIdDesc(Integer idPersona);

    boolean existsByPersonaIdAndIdExamen(Integer idPersona, Integer idExamen);
}
