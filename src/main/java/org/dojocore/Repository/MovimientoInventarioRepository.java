package org.dojocore.Repository;

import org.dojocore.Model.MovimientoInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Integer> {

    List<MovimientoInventario> findByVarianteIdOrderByIdDesc(Integer idVariante);
}
