package org.dojocore.Repository;

import org.dojocore.Model.ContactoPersona;
import org.dojocore.Model.ContactoPersonaId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContactoPersonaRepository extends JpaRepository<ContactoPersona, ContactoPersonaId> {

    @EntityGraph(attributePaths = "contacto")
    List<ContactoPersona> findByPersonaIdOrderByContactoId(Integer idPersona);

    long countByContactoId(Integer idContacto);
}
