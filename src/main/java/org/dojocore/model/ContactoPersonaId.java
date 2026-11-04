package org.dojocore.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class ContactoPersonaId {

    @Column(name = "id_persona")
    private Integer idPersona;

    @Column(name = "id_contacto")
    private Integer idContacto;

    public ContactoPersonaId(){
    }

    public ContactoPersonaId(Integer idPersona, Integer idContacto){
        this.idPersona = idPersona;
        this.idContacto = idContacto;
    }

    public Integer getIdPersona() {
        return idPersona;
    }
    public Integer getIdContacto() {
        return idContacto;
    }

    @Override
    public boolean equals(Object o){
        return o instanceof ContactoPersonaId other
                && Objects.equals(idPersona, other.idPersona) && Objects.equals(idContacto, other.idContacto);
    }

    @Override
    public int hashCode(){
        return Objects.hash(idPersona, idContacto);
    }
}
