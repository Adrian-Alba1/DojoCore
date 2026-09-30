package org.dojocore.Model;


import jakarta.persistence.*;

@Entity
@Table(name = "sensei")

    public class Sensei {

        @Id
        @Column(name = "id_persona")
        private Integer id;

        @OneToOne(fetch = FetchType.LAZY, optional = false)
        @MapsId
        @JoinColumn(name = "id_persona")
        private Persona persona;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Persona getPersona() {
        return persona;
    }
    public void setPersona(Persona persona) {
        this.persona = persona;
    }
}
