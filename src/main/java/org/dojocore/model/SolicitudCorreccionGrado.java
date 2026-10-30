package org.dojocore.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "solicitud_correccion_grado")
public class SolicitudCorreccionGrado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitud")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_alumno", nullable = false)
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_grado_solicitud", nullable = false)
    private Grado gradoSolicitado;

    @Column(name = "fecha_solicitud", nullable = false)
    private LocalDateTime fechaSolicitud;

    @Column(nullable = false, length = 20)
    private String estado;

    public Integer getId() {
        return id;
    }
    public Alumno getAlumno() {
        return alumno;
    }
    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }
    public Grado getGradoSolicitado() {
        return gradoSolicitado;
    }
    public void setGradoSolicitado(Grado gradoSolicitado) {
        this.gradoSolicitado = gradoSolicitado;
    }
    public LocalDateTime getFechaSolicitud() {
        return fechaSolicitud;
    }
    public void setFechaSolicitud(LocalDateTime fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
}
