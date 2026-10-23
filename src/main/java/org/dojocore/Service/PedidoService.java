package org.dojocore.Service;

import org.dojocore.Dto.*;
import org.dojocore.Model.*;
import org.dojocore.Repository.*;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PedidoService {

    private static final String PENDIENTE = "PENDIENTE";
    private static final String CONFIRMADO = "CONFIRMADO";
    private static final String ENTREGADO = "ENTREGADO";
    private static final String CANCELADO = "CANCELADO";

    private final PedidoRepository repo;
    private final PersonaRepository personaRepo;
    private final VarianteProductoRepository varianteRepo;
    private final InventarioRepository inventarioRepo;
    private final MovimientoInventarioRepository movimientoRepo;

    public PedidoService(PedidoRepository repo, PersonaRepository personaRepo,
                         VarianteProductoRepository varianteRepo, InventarioRepository inventarioRepo,
                         MovimientoInventarioRepository movimientoRepo) {
        this.repo = repo;
        this.personaRepo = personaRepo;
        this.varianteRepo = varianteRepo;
        this.inventarioRepo = inventarioRepo;
        this.movimientoRepo = movimientoRepo;
    }

    public List<PedidoDto> listar(Integer idPersona, String estado) {
        String est = (estado == null || estado.isBlank()) ? null : estado.trim().toUpperCase(Locale.ROOT);
        List<Pedido> lista;
        if (idPersona != null && est != null) {
            lista = repo.findByPersonaIdAndEstadoOrderById(idPersona, est);
        } else if (idPersona != null) {
            lista = repo.findByPersonaIdOrderById(idPersona);
        } else if (est != null) {
            lista = repo.findByEstadoOrderById(est);
        } else {
            lista = repo.findAll(Sort.by("id"));
        }
        return lista.stream().map(this::toDto).toList();
    }

    public PedidoDto obtener(Integer id) {
        return toDto(buscar(id));
    }

    @Transactional
    public PedidoDto crear(PedidoCrearDto dto) {
        Persona persona = personaRepo.findById(dto.idPersona()).orElseThrow(() ->
                invalida("Persona no existe: " + dto.idPersona()));
        if (!Boolean.TRUE.equals(persona.getActivo())) {
            throw invalida("La persona está inactiva: " + dto.idPersona());
        }
        Pedido p = new Pedido();
        p.setPersona(persona);
        p.setFecha(LocalDateTime.now());
        p.setEstado(PENDIENTE);
        aplicarItems(p, dto.items());
        return toDto(repo.save(p));
    }

    @Transactional
    public PedidoDto actualizarItems(Integer id, PedidoItemsDto dto) {
        Pedido p = buscar(id);
        exigirPendiente(p, "modificar");
        aplicarItems(p, dto.items());
        return toDto(p);
    }

    @Transactional
    public PedidoDto cambiarEstado(Integer id, CambioEstadoPedidoDto dto) {
        Pedido p = buscar(id);
        String nuevo = dto.estado().trim().toUpperCase(Locale.ROOT);
        Persona responsable = personaRepo.findById(dto.idResponsable()).orElseThrow(() ->
                invalida("Persona no existe: " + dto.idResponsable()));
        if (!Boolean.TRUE.equals(responsable.getActivo())) {
            throw invalida("El responsable está inactivo: " + dto.idResponsable());
        }
        String actual = p.getEstado();
        switch (nuevo) {
            case CONFIRMADO -> {
                exigirTransicion(PENDIENTE.equals(actual), actual, nuevo);
                descontarStock(p, responsable);
            }
            case ENTREGADO -> exigirTransicion(CONFIRMADO.equals(actual), actual, nuevo);
            case CANCELADO -> {
                exigirTransicion(PENDIENTE.equals(actual) || CONFIRMADO.equals(actual), actual, nuevo);
                if (CONFIRMADO.equals(actual)) {
                    devolverStock(p, responsable);
                }
            }
            default -> throw invalida("Estado no permitido: " + dto.estado()
                    + " (use CONFIRMADO, ENTREGADO o CANCELADO)");
        }
        p.setEstado(nuevo);
        return toDto(p);
    }

    @Transactional
    public void eliminar(Integer id) {
        Pedido p = buscar(id);
        if (!PENDIENTE.equals(p.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Solo se pueden borrar pedidos PENDIENTE; cancela este pedido en su lugar");
        }
        repo.delete(p);
    }

    private void aplicarItems(Pedido p, List<ItemPedidoDto> items) {
        Set<Integer> ids = new HashSet<>();
        for (ItemPedidoDto item : items) {
            if (!ids.add(item.idVariante())) {
                throw invalida("La variante " + item.idVariante() + " está repetida; suma las cantidades en una sola línea");
            }
        }
        p.getDetalles().removeIf(d -> !ids.contains(d.getVariante().getId()));
        Map<Integer, DetallePedido> actuales = p.getDetalles().stream()
                .collect(Collectors.toMap(d -> d.getVariante().getId(), Function.identity()));
        for (ItemPedidoDto item : items) {
            VarianteProducto v = varianteRepo.findById(item.idVariante()).orElseThrow(() ->
                    invalida("Variante no existe: " + item.idVariante()));
            if (!Boolean.TRUE.equals(v.getActivo()) || !Boolean.TRUE.equals(v.getProducto().getActivo())) {
                throw invalida("La variante " + v.getId() + " no está disponible (inactiva)");
            }
            DetallePedido d = actuales.get(v.getId());
            if (d == null) {
                d = new DetallePedido();
                d.setVariante(v);
                p.agregarDetalle(d);
            }
            d.setCantidad(item.cantidad());
            d.setPrecioUnitario(v.getPrecio());
        }
    }

    private void descontarStock(Pedido p, Persona responsable) {
        for (DetallePedido d : detallesOrdenados(p)) {
            VarianteProducto v = d.getVariante();
            if (!Boolean.TRUE.equals(v.getActivo())) {
                throw invalida("La variante " + v.getId() + " está inactiva; edita el pedido antes de confirmar");
            }
            Inventario inv = bloquear(v);
            if (d.getCantidad() > inv.getStockActual()) {
                throw invalida("Stock insuficiente de la variante " + v.getId() + ": hay "
                        + inv.getStockActual() + " y el pedido pide " + d.getCantidad());
            }
            inv.setStockActual(inv.getStockActual() - d.getCantidad());
            registrar(p, d, "SALIDA_PEDIDO", "Salida por pedido", responsable);
        }
    }

    private void devolverStock(Pedido p, Persona responsable) {
        for (DetallePedido d : detallesOrdenados(p)) {
            Inventario inv = bloquear(d.getVariante());
            inv.setStockActual(inv.getStockActual() + d.getCantidad());
            registrar(p, d, "ENTRADA_CANCELACION", "Cancelación de pedido", responsable);
        }
    }

    private static List<DetallePedido> detallesOrdenados(Pedido p) {
        return p.getDetalles().stream()
                .sorted(Comparator.comparing((DetallePedido d) -> d.getVariante().getId()))
                .toList();
    }

    private Inventario bloquear(VarianteProducto v) {
        return inventarioRepo.buscarParaActualizar(v.getId()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.CONFLICT, "La variante " + v.getId() + " no tiene inventario"));
    }

    private void registrar(Pedido p, DetallePedido d, String tipo, String motivo, Persona responsable) {
        MovimientoInventario m = new MovimientoInventario();
        m.setVariante(d.getVariante());
        m.setTipo(tipo);
        m.setCantidad(d.getCantidad());
        m.setFecha(LocalDateTime.now());
        m.setPersona(responsable);
        m.setMotivo(motivo);
        m.setIdPedido(p.getId());
        movimientoRepo.save(m);
    }

    private void exigirPendiente(Pedido p, String accion) {
        if (!PENDIENTE.equals(p.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede " + accion + " un pedido " + p.getEstado() + "; solo PENDIENTE");
        }
    }

    private static void exigirTransicion(boolean valida, String actual, String nuevo) {
        if (!valida) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede pasar un pedido de " + actual + " a " + nuevo);
        }
    }

    private Pedido buscar(Integer id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado: " + id));
    }

    private static ResponseStatusException invalida(String mensaje) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }

    private PedidoDto toDto(Pedido p) {
        List<DetallePedidoDto> detalles = detallesOrdenados(p).stream().map(this::toDto).toList();
        BigDecimal total = detalles.stream().map(DetallePedidoDto::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new PedidoDto(p.getId(), p.getPersona().getId(), p.getFecha(), p.getEstado(), detalles, total);
    }

    private DetallePedidoDto toDto(DetallePedido d) {
        VarianteProducto v = d.getVariante();
        BigDecimal subtotal = d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad()));
        return new DetallePedidoDto(d.getId(), v.getId(), v.getProducto().getNombre(),
                VarianteService.describir(v.getValores()), d.getCantidad(), d.getPrecioUnitario(), subtotal);

    }
}

