package org.dojocore.Repository;

import org.dojocore.Model.Sensei;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SenseiRepository extends JpaRepository<Sensei, Integer> {
    List<Sensei> findByPersonaActivo(Boolean activo);
}
