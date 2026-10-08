package org.dojocore.Model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "evento")
@Inheritance(strategy = InheritanceType.JOINED)

    public class Evento {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column
        private Integer id;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "id_tipo_evento", nullable = false)
        private TipoEvento tipo;

        @Column(nullable = false, length = 150)
        private String nombre;

        @Column(name = "fecha_inicio", nullable = false)
        private LocalDateTime fechaInicio;

        @Column(name = "fecha_fin", nullable = false)
        private LocalDateTime fechaFin;

        @Column(nullable = false, length = 200)
        private String lugar;

        @Column(columnDefinition = "text")
        private String descripcion;

        @Column(nullable = false, precision = 10, scale = 2)
        private BigDecimal costo = BigDecimal.ZERO;

        @Column(nullable = false, length = 20)
        private String estado;

        @Column(name = "fecha_publicacion", nullable = false)
        private LocalDateTime fechaPublicacion;

        @Column(nullable = false, length = 20)
        private String visibilidad;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "id_sensei_responsable", nullable = false)
        private Sensei senseiResponsable;

        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        public TipoEvento getTipo() { return tipo; }
        public void setTipo(TipoEvento tipo) { this.tipo = tipo; }
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public LocalDateTime getFechaInicio() { return fechaInicio; }
        public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }
        public LocalDateTime getFechaFin() { return fechaFin; }
        public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }
        public String getLugar() { return lugar; }
        public void setLugar(String lugar) { this.lugar = lugar; }
        public String getDescripcion() { return descripcion; }
        public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
        public BigDecimal getCosto() { return costo; }
        public void setCosto(BigDecimal costo) { this.costo = costo; }
        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
        public LocalDateTime getFechaPublicacion() { return fechaPublicacion; }
        public void setFechaPublicacion(LocalDateTime fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
        public String getVisibilidad() { return visibilidad; }
        public void setVisibilidad(String visibilidad) { this.visibilidad = visibilidad; }
        public Sensei getSenseiResponsable() { return senseiResponsable; }
        public void setSenseiResponsable(Sensei senseiResponsable) { this.senseiResponsable = senseiResponsable; }
}
