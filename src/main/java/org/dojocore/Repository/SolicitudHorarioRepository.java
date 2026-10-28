package org.dojocore.Repository;

import org.dojocore.Model.SolicitudHorario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudHorarioRepository extends JpaRepository<SolicitudHorario, Integer> {

    List<SolicitudHorario> findByAlumnoIdOrderById(Integer idAlumno);

    List<SolicitudHorario> findByEstadoOrderById(String estado);

    List<SolicitudHorario> findByAlumnoIdAndEstadoOrderById(Integer idAlumno, String estado);

    boolean existsByAlumnoIdAndHorarioIdAndEstado(Integer idAlumno, Integer idHorario, String estado);
}
