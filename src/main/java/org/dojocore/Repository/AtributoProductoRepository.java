package org.dojocore.Repository;

import org.dojocore.Model.AtributoProducto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtributoProductoRepository extends JpaRepository<AtributoProducto, Integer> {

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Integer id);
}
