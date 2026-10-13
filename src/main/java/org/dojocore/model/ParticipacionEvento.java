package org.dojocore.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "participacion_evento")

public class ParticipacionEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_participacion")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evento", nullable = false)
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_persona", nullable = false)
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria")
    private CategoriaTorneo categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_resultado")
    private ResultadoEvento resultado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_grado_anterior")
    private Grado gradoAnterior;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_grado_nuevo")
    private Grado gradoNuevo;

    @Column(length = 30)
    private String mencion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public CategoriaTorneo getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaTorneo categoria) {
        this.categoria = categoria;
    }

    public ResultadoEvento getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoEvento resultado) {
        this.resultado = resultado;
    }

    public Grado getGradoAnterior() {
        return gradoAnterior;
    }

    public void setGradoAnterior(Grado gradoAnterior) {
        this.gradoAnterior = gradoAnterior;
    }

    public Grado getGradoNuevo() {
        return gradoNuevo;
    }

    public void setGradoNuevo(Grado gradoNuevo) {
        this.gradoNuevo = gradoNuevo;
    }

    public String getMencion() {
        return mencion;
    }

    public void setMencion(String mencion) {
        this.mencion = mencion;
    }
}
