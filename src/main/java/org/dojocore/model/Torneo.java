package org.dojocore.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "torneo")
@PrimaryKeyJoinColumn(name = "id_evento")
public class Torneo extends Evento{
}
