package org.dojocore.Service;

import org.dojocore.Dto.ProductoDto;
import org.dojocore.Model.Producto;
import org.dojocore.Model.VarianteProducto;
import org.dojocore.Repository.ProductoRepository;
import org.dojocore.Repository.VarianteProductoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductoService {

    private final ProductoRepository repo;
    private final VarianteProductoRepository varianteRepo;

    public ProductoService(ProductoRepository repo, VarianteProductoRepository varianteRepo){
        this.repo = repo;
        this.varianteRepo = varianteRepo;
    }

    public List<ProductoDto> listar(boolean incluirInactivos){
        List<Producto> lista = incluirInactivos ? repo.findAll(Sort.by("id")) : repo.findByActivoOrderById(true);
        return lista.stream().map(this::toDto).toList();
    }

    public ProductoDto obtener(Integer id){
        return toDto(buscar(id));
    }

    @Transactional
    public ProductoDto crear(ProductoDto dto){
        Producto p = new Producto();
        p.setNombre(dto.nombre().trim());
        p.setDescripcion(dto.descripcion());
        p.setActivo(dto.activo() == null || dto.activo());
        return toDto(repo.save(p));
    }

    @Transactional
    public ProductoDto actualizar(Integer id, ProductoDto dto){
        Producto p = buscar(id);
        p.setNombre(dto.nombre().trim());
        p.setDescripcion(dto.descripcion());
        if (dto.activo() != null && !dto.activo().equals(p.getActivo())){
            if (dto.activo()){
                p.setActivo(true);
            }else{
                desactivar(p);
            }
        }
        return toDto(p);
    }

    @Transactional
    public void eliminar(Integer id){
        desactivar(buscar(id));
    }

    private void desactivar(Producto p){
        p.setActivo(false);
        for (VarianteProducto v : varianteRepo.findByProductoIdOrderById(p.getId())){
            v.setActivo(false);
        }
    }

    private Producto buscar(Integer id){
        return repo.findById(id).orElseThrow(()->
                 new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto No Encontrado: " + id));
    }

    private ProductoDto toDto(Producto p){
        return new ProductoDto(p.getId(), p.getNombre(), p.getDescripcion(), p.getActivo());
    }
}
