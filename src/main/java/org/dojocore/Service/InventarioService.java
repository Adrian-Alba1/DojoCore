package org.dojocore.Service;

import org.dojocore.Dto.InventarioDto;
import org.dojocore.Dto.MovimientoInventarioDto;
import org.dojocore.Dto.MovimientoManualDto;
import org.dojocore.Model.Inventario;
import org.dojocore.Model.MovimientoInventario;
import org.dojocore.Model.Persona;
import org.dojocore.Model.VarianteProducto;
import org.dojocore.Repository.InventarioRepository;
import org.dojocore.Repository.MovimientoInventarioRepository;
import org.dojocore.Repository.PersonaRepository;
import org.dojocore.Repository.VarianteProductoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class InventarioService {

    private static final String ENTRADA_MANUAL = "ENTRADA_MANUAL";
    private static final String SALIDA_MANUAL = "SALIDA_MANUAL";
    private static final Set<String> TIPOS_MANUALES = Set.of(ENTRADA_MANUAL, SALIDA_MANUAL);

    private final InventarioRepository repo;
    private final MovimientoInventarioRepository movimientoRepo;
    private final VarianteProductoRepository varianteRepo;
    private final PersonaRepository personaRepo;

    public InventarioService(InventarioRepository repo, MovimientoInventarioRepository movimientoRepo,
                             VarianteProductoRepository varianteRepo, PersonaRepository personaRepo){
        this.repo = repo;
        this.movimientoRepo = movimientoRepo;
        this.varianteRepo = varianteRepo;
        this.personaRepo = personaRepo;
    }

    public List<InventarioDto> listar(boolean incluirInactivos) {
        List<Inventario> lista = incluirInactivos
                ? repo.findAllByOrderByVarianteId()
                : repo.findByVarianteActivoOrderByVarianteId(true);
        return lista.stream().map(this::toDto).toList();
    }

    public InventarioDto obtener(Integer idVariante) {
        buscarVariante(idVariante);
        return toDto(repo.findByVarianteId(idVariante).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "La variante no tiene inventario: " + idVariante)));
    }

    /** Historial de la variante, del mas reciente al mas antiguo. */
    public List<MovimientoInventarioDto> movimientos(Integer idVariante) {
        buscarVariante(idVariante);
        return movimientoRepo.findByVarianteIdOrderByIdDesc(idVariante).stream().map(this::toDto).toList();
    }

    @Transactional
    public MovimientoInventarioDto registrarMovimiento(Integer idVariante, MovimientoManualDto dto) {
        VarianteProducto variante = buscarVariante(idVariante);
        String tipo = dto.tipo().trim().toUpperCase();
        if (!TIPOS_MANUALES.contains(tipo)) {
            throw invalida("Tipo no permitido: " + dto.tipo() + " (use ENTRADA_MANUAL o SALIDA_MANUAL)");
        }
        Persona persona = personaRepo.findById(dto.idPersona()).orElseThrow(() ->
                invalida("Persona no existe: " + dto.idPersona()));
        if (!Boolean.TRUE.equals(persona.getActivo())) {
            throw invalida("La persona está inactiva: " + dto.idPersona());
        }
        Inventario inv = repo.buscarParaActualizar(idVariante).orElseGet(() -> {
            Inventario nuevo = new Inventario();
            nuevo.setVariante(variante);
            nuevo.setStockActual(0);
            return repo.save(nuevo);
        });
        int stock = inv.getStockActual();
        if (ENTRADA_MANUAL.equals(tipo)) {
            inv.setStockActual(stock + dto.cantidad());
        } else {
            if (dto.cantidad() > stock) {
                throw invalida("Stock insuficiente: hay " + stock + " y se piden " + dto.cantidad());
            }
            inv.setStockActual(stock - dto.cantidad());
        }
        MovimientoInventario m = new MovimientoInventario();
        m.setVariante(variante);
        m.setTipo(tipo);
        m.setCantidad(dto.cantidad());
        m.setFecha(LocalDateTime.now());
        m.setPersona(persona);
        m.setMotivo(dto.motivo());
        return toDto(movimientoRepo.save(m));
    }

    private VarianteProducto buscarVariante(Integer id) {
        return varianteRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Variante no encontrada: " + id));
    }

    private static ResponseStatusException invalida(String mensaje) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }

    private InventarioDto toDto(Inventario i) {
        VarianteProducto v = i.getVariante();
        return new InventarioDto(v.getId(), v.getProducto().getId(), v.getProducto().getNombre(),
                VarianteService.describir(v.getValores()), i.getStockActual(), v.getActivo());
    }

    private MovimientoInventarioDto toDto(MovimientoInventario m) {
        return new MovimientoInventarioDto(m.getId(), m.getVariante().getId(), m.getTipo(), m.getCantidad(),
                m.getFecha(), m.getPersona() != null ? m.getPersona().getId() : null, m.getMotivo(), m.getIdPedido());
    }
}
