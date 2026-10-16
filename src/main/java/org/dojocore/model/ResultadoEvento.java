package org.dojocore.Model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "resultado_evento")

public class ResultadoEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resultado")
    private Integer id;

    @Column(nullable = false, unique = true, length = 30)
    private String nombre;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "resultado_tipo_evento",
            joinColumns = @JoinColumn(name = "id_resultado"),
            inverseJoinColumns = @JoinColumn(name = "id_tipo_evento"))
    private Set<TipoEvento> tiposEvento = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Set<TipoEvento> getTiposEvento() {
        return tiposEvento;
    }
}
