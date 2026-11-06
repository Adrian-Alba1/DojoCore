package org.dojocore.Repository;

import org.dojocore.Model.ArchivoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ArchivoEventoRepository extends JpaRepository<ArchivoEvento, Integer> {

    List<ArchivoEvento> findByEventoIdOrderById(Integer idEvento);
}
