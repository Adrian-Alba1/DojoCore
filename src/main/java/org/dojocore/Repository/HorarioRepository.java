package org.dojocore.Repository;

import org.dojocore.Model.Horario;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HorarioRepository extends JpaRepository<Horario, Integer> {

    @Override
    @EntityGraph(attributePaths = {"sede", "dias"})
    List<Horario> findAll(Sort sort);

    @EntityGraph(attributePaths = {"sede", "dias"})
    List<Horario> findByActivoOrderById(Boolean activo);

    @EntityGraph(attributePaths = {"sede", "dias"})
    List<Horario> findBySedeIdOrderById(Integer activo);

    @EntityGraph(attributePaths = {"sede", "dias"})
    List<Horario> findBySedeIdAndActivoOrderById(Integer idSede, Boolean activo);

    boolean existsBySedeIdAndActivoTrue(Integer idSede);
}
