package org.dojocore.Repository;

import org.dojocore.Model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaRepository extends JpaRepository<Persona, Integer> {
    boolean existsByEmail(String email);
}
