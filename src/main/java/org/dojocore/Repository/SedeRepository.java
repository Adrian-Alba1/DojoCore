package org.dojocore.Repository;

import org.dojocore.Model.Sede;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SedeRepository extends JpaRepository<Sede, Integer> {

    List<Sede> findByActivoOrderById(Boolean activo);

    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Integer id);
    boolean existsByEsCentralTrue();
    boolean existsByEsCentralTrueAndIdNot(Integer id);
}
