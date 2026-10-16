package org.dojocore.Model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "alumno")
public class Alumno {

    @Id
    @Column(name = "id_persona")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sensei_principal")
    private Sensei senseiPrincipal;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "alumno_horario",
            joinColumns = @JoinColumn(name = "id_alumno"),
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
    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }
    public void setFechaIngreso(LocalDate fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }
    public Sensei getSenseiPrincipal() {
        return senseiPrincipal;
    }
    public void setSenseiPrincipal(Sensei senseiPrincipal) {
        this.senseiPrincipal = senseiPrincipal;
    }
    public Set<Horario> getHorarios() { return horarios; }
}

