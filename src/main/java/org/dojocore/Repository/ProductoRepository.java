package org.dojocore.Repository;

import org.dojocore.Model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    List<Producto> findByActivoOrderById(Boolean activo);
}
