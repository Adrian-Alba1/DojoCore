package org.dojocore.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;


@Embeddable
public class TutorAlumnoId implements Serializable {

    @Column(name = "id_alumno")
    private Integer idAlumno;

    @Column(name = "id_contacto")
    private Integer idContacto;

    public TutorAlumnoId(){
    }

    public TutorAlumnoId(Integer idAlumno, Integer idContacto){
        this.idAlumno = idAlumno;
        this.idContacto = idContacto;
    }

    public Integer getIdAlumno(){
        return idAlumno;
    }

    public  Integer getIdContacto(){
        return idContacto;
    }

    @Override
    public boolean equals(Object o){
        return o instanceof TutorAlumnoId other
                && Objects.equals(idAlumno, other.idAlumno) && Objects.equals(idContacto, other.idContacto);
    }

    @Override
    public int hashCode(){
        return Objects.hash(idAlumno, idContacto);
    }
}
