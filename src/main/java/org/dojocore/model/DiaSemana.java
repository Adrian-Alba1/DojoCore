package org.dojocore.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "dia_semana")
public class DiaSemana {

    @Id
    @Column(name = "id_dia")
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String nombre;

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
}
