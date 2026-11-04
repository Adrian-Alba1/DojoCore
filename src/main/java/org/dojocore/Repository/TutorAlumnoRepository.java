package org.dojocore.Repository;

import org.dojocore.Model.TutorAlumno;
import org.dojocore.Model.TutorAlumnoId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TutorAlumnoRepository extends JpaRepository<TutorAlumno, TutorAlumnoId> {

    List<TutorAlumno> findByAlumnoId(Integer idAlumno);
}
