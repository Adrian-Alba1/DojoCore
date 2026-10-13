package org.dojocore.Repository;

import org.dojocore.Model.ParticipacionEvento;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipacionEventoRepository extends JpaRepository<ParticipacionEvento, Integer> {

    @Override
    @EntityGraph(attributePaths = {"persona", "categoria", "resultado"})
    List<ParticipacionEvento> findAll();

    @EntityGraph(attributePaths = {"persona", "categoria", "resultado"})
    List<ParticipacionEvento> findByEventoId(Integer idEvento);

    @EntityGraph(attributePaths = {"persona", "categoria", "resultado"})
    List<ParticipacionEvento> findByPersonaId(Integer idPersona);

    @EntityGraph(attributePaths = {"persona", "categoria", "resultado"})
    List<ParticipacionEvento> findByEventoIdAndPersonaId(Integer idEvento, Integer idPersona);

    boolean existsByEventoIdAndPersonaIdAndCategoriaId(Integer idEvento, Integer idPersona, Integer idCategoria);
    boolean existsByEventoIdAndPersonaIdAndCategoriaIsNull(Integer idEvento, Integer idPersona);
}
