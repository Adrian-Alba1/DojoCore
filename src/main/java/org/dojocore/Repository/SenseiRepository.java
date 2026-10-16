package org.dojocore.Repository;

import org.dojocore.Model.Sensei;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SenseiRepository extends JpaRepository<Sensei, Integer> {

    List<Sensei> findByPersonaActivo(Boolean activo);

    //sensei - horario
    @EntityGraph(attributePaths = {"persona"})
    List<Sensei> findByHorariosIdOrderById(Integer idHorario);

    //sensei activo - horario
    @EntityGraph(attributePaths = {"persona"})
    List<Sensei> findByHorariosIdAndPersonaActivoOrderById(Integer idHorario, Boolean activo);
}
