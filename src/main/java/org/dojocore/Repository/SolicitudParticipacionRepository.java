package org.dojocore.Repository;

import org.dojocore.Model.SolicitudParticipacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitudParticipacionRepository extends JpaRepository<SolicitudParticipacion, Integer> {

    List<SolicitudParticipacion> findByPersonaIdOrderById(Integer idPersona);

    List<SolicitudParticipacion> findByEstadoOrderById(String estado);

    List<SolicitudParticipacion> findByPersonaIdAndEstadoOrderById(Integer idPersona, String estado);

    boolean existsByEventoIdAndPersonaIdAndCategoriaIdAndEstado(Integer idEvento, Integer idPersona,
                                                                Integer idCategoria, String estado);

    boolean existsByEventoIdAndPersonaIdAndCategoriaIsNullAndEstado(Integer idEvento, Integer idPersona,
                                                                    String estado);
}
