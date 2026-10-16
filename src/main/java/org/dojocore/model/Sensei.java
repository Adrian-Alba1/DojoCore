package org.dojocore.Model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "sensei_horario",
            joinColumns = @JoinColumn(name = "id_sensei"),
            inverseJoinColumns = @JoinColumn(name = "id_horario"))
    private Set<Horario> horarios = new HashSet<>();

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
    public Set<Horario> getHorarios() { return horarios; }
}
