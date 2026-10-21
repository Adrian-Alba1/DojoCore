package org.dojocore.Repository;

import jakarta.persistence.LockModeType;
import org.dojocore.Model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Integer> {

    Optional<Inventario> findByVarianteId(Integer idVariante);

    List<Inventario> findByVarianteActivoOrderByVarianteId(Boolean activo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventario i where i.variante.id = :idVariante")
    Optional<Inventario> buscarParaActualizar(@Param("idVariante") Integer idVariante);
}
