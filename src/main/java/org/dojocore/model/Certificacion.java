package org.dojocore.Model;


import jakarta.persistence.*;

@Entity
@Table(name = "certificacion")
@PrimaryKeyJoinColumn(name = "id_evento")

public class Certificacion extends Evento {

    @Column(name = "tipo_certificacion", nullable = false, length = 100)
    private String tipoCertificacion;

    public String getTipoCertificacion(){
        return tipoCertificacion;
    }
    public void setTipoCertificacion(String tipoCertificacion){
        this.tipoCertificacion = tipoCertificacion;
    }
}
