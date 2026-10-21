package org.dojocore.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "atributo_producto")
public class AtributoProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_atributo")
    private Integer id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    public Integer getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
