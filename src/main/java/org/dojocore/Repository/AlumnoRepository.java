package org.dojocore.Repository;

import org.dojocore.Model.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlumnoRepository extends JpaRepository<Alumno, Integer> {
    List<Alumno> findByPersonaActivo(Boolean activo);
}
