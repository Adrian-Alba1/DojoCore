package org.dojocore.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "inventario")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_inventario")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_variante", nullable = false, unique = true)
    private VarianteProducto variante;

    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual = 0;

    public Integer getId() {
        return id;
    }
    public VarianteProducto getVariante() {
        return variante;
    }
    public void setVariante(VarianteProducto variante) {
        this.variante = variante;
    }
    public Integer getStockActual() {
        return stockActual;
    }
    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }
}
