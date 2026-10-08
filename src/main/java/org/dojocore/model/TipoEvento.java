package org.dojocore.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "tipo_evento")

    public class TipoEvento {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_tipo_evento")
        private Integer id;

        @Column(nullable = false, unique = true, length = 30)
        private String nombre;

        public Integer getId(){
            return id;
        }
        public void setId(Integer id) {
            this.id = id;
        }
        public String getNombre() {
            return nombre;
        }
        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
}
