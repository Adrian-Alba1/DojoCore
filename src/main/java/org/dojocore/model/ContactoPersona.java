package org.dojocore.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "contacto_persona")
public class ContactoPersona {

    @EmbeddedId
    private ContactoPersonaId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("id_persona")
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idContacto")
    @JoinColumn(name = "id_contacto")
    private ContactoEmergencia contacto;

    protected ContactoPersona(){
    }

    public ContactoPersona(Persona persona, ContactoEmergencia contacto){
        this.id = new ContactoPersonaId(persona.getId(), contacto.getId());
        this.persona = persona;
        this.contacto = contacto;
    }

    public ContactoPersonaId getId(){
        return id;
    }
    public Persona getPersona(){
        return persona;
    }
    public ContactoEmergencia getContacto(){
        return contacto;
    }
}
