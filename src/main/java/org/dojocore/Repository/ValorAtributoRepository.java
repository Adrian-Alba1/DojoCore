package org.dojocore.Repository;

import org.dojocore.Model.ValorAtributo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ValorAtributoRepository extends JpaRepository<ValorAtributo, Integer> {

    List<ValorAtributo> findByAtributoIdOrderById(Integer idAtributo);

    boolean existsByAtributoIdAndValorIgnoreCase(Integer idAtributo, String valor);

    boolean existsByAtributoIdAndValorIgnoreCaseAndIdNot(Integer idAtributo, String valor, Integer id);
}
