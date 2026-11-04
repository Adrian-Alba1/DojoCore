package org.dojocore.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historial_grado")
public class HistorialGrado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_persona", nullable = false)
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_grado_anterior", nullable = false)
    private Grado gradoAnterior;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_grado_nuevo", nullable = false)
    private Grado gradoNuevo;

    @Column(name = "id_examen", nullable = false)
    private Integer idExamen;

    @Column(nullable = false)
    private LocalDateTime fecha;

    protected HistorialGrado(){
    }

     public HistorialGrado(Persona persona, Grado gradoAnterior, Grado gradoNuevo, Integer idExamen, LocalDateTime fecha){
        this.persona = persona;
        this.gradoAnterior = gradoAnterior;
        this.gradoNuevo = gradoNuevo;
        this.idExamen = idExamen;
        this.fecha = fecha;
     }

    public Integer getId() {
        return id;
    }
    public Persona getPersona() {
        return persona;
    }
    public Grado getGradoAnterior() {
        return gradoAnterior;
    }
    public Grado getGradoNuevo() {
        return gradoNuevo;
    }
    public Integer getIdExamen() {
        return idExamen;
    }
    public LocalDateTime getFecha() {
        return fecha;
    }
}
