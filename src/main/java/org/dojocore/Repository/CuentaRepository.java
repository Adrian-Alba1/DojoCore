package org.dojocore.Repository;

import org.dojocore.Model.Cuenta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {

    @EntityGraph(attributePaths = {"persona", "rol"})
    Optional<Cuenta> findByUsername(String username);
}
