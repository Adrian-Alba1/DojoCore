package org.dojocore.Model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "examen")
@PrimaryKeyJoinColumn(name = "id_evento")
public class Examen extends Evento{
}