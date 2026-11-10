package org.dojocore.Model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "archivo_evento")
public class ArchivoEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_archivo")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_evento", nullable = false)
    private Evento evento;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 500)
    private String ruta;

    @Column(length = 50)
    private String tipo;

    @Column(name = "fecha_subida", nullable = false)
    private LocalDateTime fechaSubida;

    public Integer getId() {
        return id;
    }
    public Evento getEvento() {
        return evento;
    }
    public void setEvento(Evento evento) {
        this.evento = evento;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getRuta() {
        return ruta;
    }
    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public LocalDateTime getFechaSubida() {
        return fechaSubida;
    }
    public void setFechaSubida(LocalDateTime fechaSubida) {
        this.fechaSubida = fechaSubida;
    }

    public boolean esEnlaceExterno(){
        return ruta != null && (ruta.startsWith("http://") || ruta.startsWith("https://"));
    }
}
