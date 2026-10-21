package org.dojocore.Model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "variante_producto")
public class VarianteProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_variante")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private Boolean activo = true;

    @ManyToMany
    @JoinTable(name = "variante_atributo",
            joinColumns = @JoinColumn(name = "id_variante"),
            inverseJoinColumns = @JoinColumn(name = "id_valor"))
    private Set<ValorAtributo> valores = new HashSet<>();

    public Integer getId() {
        return id;
    }
    public Producto getProducto() {
        return producto;
    }
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    public BigDecimal getPrecio() {
        return precio;
    }
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    public Boolean getActivo() {
        return activo;
    }
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    public Set<ValorAtributo> getValores() {
        return valores;
    }
    public void setValores(Set<ValorAtributo> valores) {
        this.valores = valores;
    }
}
