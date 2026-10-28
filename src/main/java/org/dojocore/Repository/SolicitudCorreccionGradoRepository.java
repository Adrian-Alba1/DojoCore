package org.dojocore.Repository;

import org.dojocore.Model.SolicitudCorreccionGrado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudCorreccionGradoRepository extends JpaRepository<SolicitudCorreccionGrado, Integer> {

    List<SolicitudCorreccionGrado> findByAlumnoIdOrderById(Integer idAlumno);

    List<SolicitudCorreccionGrado> findByEstadoOrderById(String estado);

    List<SolicitudCorreccionGrado> findByAlumnoIdAndEstadoOrderById(Integer idAlumno, String estado);

    boolean existsByAlumnoIdAndEstado(Integer idAlumno, String estado);
}
