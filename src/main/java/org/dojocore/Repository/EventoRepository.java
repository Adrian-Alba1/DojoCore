package org.dojocore.Repository;

import org.dojocore.Model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Integer> {

    //filtro por tipo de evento
    List<Evento> findByTipoNombre(String nombre);
}
