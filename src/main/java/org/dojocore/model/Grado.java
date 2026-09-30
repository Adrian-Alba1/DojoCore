package org.dojocore.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "grado")

    public class Grado {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_grado")
        private Integer id;

        @Column(nullable = false, length = 10)
        private String nivel;

        @Column(nullable = false, length = 50)
        private String nombre;

        @Column(nullable = false, length = 30)
        private String color;

        @Column(nullable = false)
        private Integer orden;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNivel() {
        return nivel;
    }
    public void setNivel(String nivel) {
        this.nivel = nivel;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public Integer getOrden() {
        return orden;
    }
    public void setOrden(Integer orden) {
        this.orden = orden;
    }
}
