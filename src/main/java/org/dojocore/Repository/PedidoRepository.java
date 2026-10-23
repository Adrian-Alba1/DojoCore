package org.dojocore.Repository;

import org.dojocore.Model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByPersonaIdOrderById(Integer idPersona);

    List<Pedido> findByEstadoOrderById(String estado);

    List<Pedido> findByPersonaIdAndEstadoOrderById(Integer idPersona, String estado);
}
