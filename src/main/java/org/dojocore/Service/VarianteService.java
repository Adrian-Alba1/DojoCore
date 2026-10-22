package org.dojocore.Service;

import org.dojocore.Dto.VarianteDto;
import org.dojocore.Model.Inventario;
import org.dojocore.Model.Producto;
import org.dojocore.Model.ValorAtributo;
import org.dojocore.Model.VarianteProducto;
import org.dojocore.Repository.InventarioRepository;
import org.dojocore.Repository.ProductoRepository;
import org.dojocore.Repository.ValorAtributoRepository;
import org.dojocore.Repository.VarianteProductoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class VarianteService {

    private final VarianteProductoRepository repo;
    private final ProductoRepository productoRepo;
    private final ValorAtributoRepository valorRepo;
    private final InventarioRepository inventarioRepo;

    public VarianteService(VarianteProductoRepository repo, ProductoRepository productoRepo, ValorAtributoRepository
                            valorRepo,InventarioRepository inventarioRepo){
        this.repo = repo;
        this.productoRepo = productoRepo;
        this.valorRepo = valorRepo;
        this.inventarioRepo = inventarioRepo;
    }

    public List<VarianteDto> listar(Integer idProducto, boolean incluirInactivos){
        List<VarianteProducto> lista;
        if (idProducto != null){
            buscarProducto(idProducto);
            lista = incluirInactivos
                    ? repo.findByProductoIdOrderById(idProducto)
                    : repo.findByProductoIdAndActivoOrderById(idProducto,true);
        }else{
            lista = incluirInactivos ? repo.findAll(Sort.by("id")) : repo.findByActivoOrderById(true);
        }
        return lista.stream().map(this::toDto).toList();
    }

    public VarianteDto obtener(Integer id) {
        return toDto(buscar(id));
    }

    @Transactional
    public VarianteDto crear(Integer idProducto, VarianteDto dto){
        Producto p =buscarProducto(idProducto);
        if (!Boolean.TRUE.equals(p.getActivo())){
            throw invalida("El Producto Esta Inactivo: " + idProducto);
        }
        Set<ValorAtributo> valores = resolverValores(dto.idsValores());
        validarCombinacion(idProducto, null, valores);
        VarianteProducto v = new VarianteProducto();
        v.setProducto(p);
        v.setPrecio(dto.precio());
        v.setActivo(dto.activo() == null || dto.activo());
        v.setValores(valores);
        repo.save(v);
        Inventario inv = new Inventario();
        inv.setVariante(v);
        inv.setStockActual(0);
        inventarioRepo.save(inv);
        return toDto(v);
    }

    @Transactional
    public VarianteDto actualizar(Integer id, VarianteDto dto){
        VarianteProducto v = buscar(id);
        if (dto.idsValores() != null){
            Set<ValorAtributo> valores = resolverValores(dto.idsValores());
            validarCombinacion(v.getProducto().getId(), id, valores);
            v.getValores().clear();
            v.getValores().addAll(valores);
        }
        v.setPrecio(dto.precio());
        if (dto.activo() != null && !dto.activo().equals(v.getActivo())){
            if (dto.activo() && !Boolean.TRUE.equals(v.getProducto().getActivo())){
                throw invalida("No Se Puede Activar La Variante: El Producto Está Inactivo");
            }
        v.setActivo(dto.activo());
    }
    return toDto(v);
    }

    @Transactional
    public void eliminar(Integer id){
        buscar(id).setActivo(false);
    }

    private Set<ValorAtributo> resolverValores(Set<Integer> ids){
        Set<ValorAtributo> valores = new HashSet<>();
        if (ids == null){
            return valores;
        }
        Set<Integer> atributosVistos = new HashSet<>();
        for (Integer idValor : ids){
            ValorAtributo va = valorRepo.findById(idValor).orElseThrow(()->
                    invalida("Valor De Atributo No Existe: " + idValor));
            if (!atributosVistos.add(va.getAtributo().getId())){
                throw invalida("Solo se Permite Un Valor Por Atributo (Repetido: " + va.getAtributo().getNombre() + ")");
            }
            valores.add(va);
        }
        return valores;
    }

    private void validarCombinacion(Integer idProducto, Integer idExcluida, Set<ValorAtributo> valores){
        if (valores.isEmpty()){
            return;
        }
        Set<Integer> ids = valores.stream().map(ValorAtributo::getId).collect(Collectors.toSet());
        for (VarianteProducto otra : repo.findByProductoIdOrderById(idProducto)){
            if (otra.getId().equals(idExcluida)){
                continue;
            }
            Set<Integer> idsOtra = otra.getValores().stream().map(ValorAtributo::getId).collect(Collectors.toSet());
            if (idsOtra.equals(ids)){
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Ya Existe La Variante " + otra.getId() + " De Este Producto Con Esos Atributos");
            }
        }
    }

    private VarianteProducto buscar(Integer id){
        return repo.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Variante No Encontrada: " + id));
    }

    private Producto buscarProducto(Integer id){
        return productoRepo.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto No Encontrado: "+ id));
    }

    private static ResponseStatusException invalida(String mensaje){
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }

    public static List<String> describir(Set<ValorAtributo> valores){
        return valores.stream()
                .sorted(Comparator.comparing((ValorAtributo va) -> va.getAtributo().getId()))
                .map(va -> va.getAtributo().getNombre() + ": " + va.getValor())
                .toList();
    }

    public VarianteDto toDto(VarianteProducto v){
        int stock = inventarioRepo.findByVarianteId(v.getId()).map(Inventario::getStockActual).orElse(0);
        Set<Integer> ids = v.getValores().stream().map(ValorAtributo::getId).collect(Collectors.toCollection(java.util.TreeSet::new));
        return new VarianteDto(v.getId(), v.getProducto().getId(), v.getProducto().getNombre(),
                v.getPrecio(), v.getActivo(), ids, describir(v.getValores()), stock);
    }
}
