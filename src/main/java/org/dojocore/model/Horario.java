package org.dojocore.Model;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "horario")
public class Horario {

    @Id
    @GeneratedValue
    @Column(name = "id_horario")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_sede", nullable = false)
    private Sede sede;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horainIcio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(columnDefinition = "text")
    private String descripcion;

    @Column(nullable = false)
    private boolean activo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "horario_dia",
        joinColumns = @JoinColumn(name = "id_horario"),
        inverseJoinColumns = @JoinColumn(name = "id_dia"))
    private Set<DiaSemana> dias = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Sede getSede() {
        return sede;
    }

    public void setSede(Sede sede) {
        this.sede = sede;
    }

    public LocalTime getHorainIcio() {
        return horainIcio;
    }

    public void setHorainIcio(LocalTime horainIcio) {
        this.horainIcio = horainIcio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Set<DiaSemana> getDias() {
        return dias;
    }
}
