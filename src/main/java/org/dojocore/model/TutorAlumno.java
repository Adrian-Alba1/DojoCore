package org.dojocore.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "tutor_alumno")
public class TutorAlumno{

    @EmbeddedId
    private TutorAlumnoId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idAlumno")
    @JoinColumn(name = "id_alumno")
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idContacto")
    @JoinColumn(name = "id_contacto")
    private ContactoEmergencia contacto;

    protected TutorAlumno(){
    }

    public TutorAlumno(Alumno alumno, ContactoEmergencia contacto){
        this.id = new TutorAlumnoId(alumno.getPersona().getId(), contacto.getId());
        this.alumno = alumno;
        this.contacto = contacto;
    }

    public TutorAlumnoId getId(){
        return id;
    }
    public Alumno getAlumno(){
        return alumno;
    }
    public ContactoEmergencia getContacto(){
        return contacto;
    }
}
