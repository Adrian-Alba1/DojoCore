package org.dojocore.Repository;

import org.dojocore.Model.CategoriaTorneo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaTorneoRepository extends JpaRepository<CategoriaTorneo, Integer> {

    List<CategoriaTorneo> findByTorneoIdOrderById(Integer idEvento);
    boolean existsByTorneoIdAndTipoIgnoreCase(Integer idEvento, String tipo);
}
