package org.dojocore.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "valor_atributo")
public class ValorAtributo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_valor")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_atributo", nullable = false)
    private AtributoProducto atributo;

    @Column(nullable = false, length = 100)
    private String valor;

    public Integer getId() {
        return id;
    }
    public AtributoProducto getAtributo() {
        return atributo;
    }
    public void setAtributo(AtributoProducto atributo) {
        this.atributo = atributo;
    }
    public String getValor() {
        return valor;
    }
    public void setValor(String valor) {
        this.valor = valor;
    }
}
