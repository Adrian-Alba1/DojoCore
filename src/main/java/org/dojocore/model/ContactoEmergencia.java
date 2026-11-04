package org.dojocore.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "contacto_emergencia")
public class ContactoEmergencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contacto")
    private Integer id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 20)
    private String telefono;

    @Column(nullable = false, length = 50)
    private String parentesco;

    public Integer getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public String getParentesco() {
        return parentesco;
    }
    public void setParentesco(String parenteco) {
        this.parentesco = parenteco;
    }
}
