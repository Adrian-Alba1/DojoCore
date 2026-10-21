package org.dojocore.Repository;

import org.dojocore.Model.VarianteProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VarianteProductoRepository extends JpaRepository<VarianteProducto, Integer> {

    List<VarianteProducto> findByActivoOrderById(Boolean activo);

    List<VarianteProducto> findByProductoIdOrderById(Integer idProducto);

    List<VarianteProducto> findByProductoIdAndActivoOrderById(Integer idProducto, Boolean activo);

    @Query("select count(v) > 0 from VarianteProducto v join v.valores val where val.id = :idValor")
    boolean existeUsoDeValor(@Param("idValor") Integer idValor);

    @Query("select count(v) > 0 from VarianteProducto v join v.valores val where val.atributo.id = :idAtributo")
    boolean existeUsoDeAtributo(@Param("idAtributo") Integer idAtributo);
}
