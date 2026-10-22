package org.dojocore.Service;

import org.dojocore.Dto.AtributoDto;
import org.dojocore.Dto.ValorAtributoDto;
import org.dojocore.Model.AtributoProducto;
import org.dojocore.Model.ValorAtributo;
import org.dojocore.Repository.AtributoProductoRepository;
import org.dojocore.Repository.ValorAtributoRepository;
import org.dojocore.Repository.VarianteProductoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AtributoService {

    private final AtributoProductoRepository repo;
    private final ValorAtributoRepository valorRepo;
    private final VarianteProductoRepository varianteRepo;

    public AtributoService(AtributoProductoRepository repo, ValorAtributoRepository valorRepo,
                           VarianteProductoRepository varianteRepo) {
        this.repo = repo;
        this.valorRepo = valorRepo;
        this.varianteRepo = varianteRepo;
    }

    public List<AtributoDto> listar() {
        return repo.findAll(Sort.by("id")).stream().map(this::toDto).toList();
    }

    public AtributoDto obtener(Integer id) {
        return toDto(buscar(id));
    }

    @Transactional
    public AtributoDto crear(AtributoDto dto) {
        String nombre = dto.nombre().trim();
        if (repo.existsByNombreIgnoreCase(nombre)) {
            throw conflicto("Ya existe un atributo con ese nombre");
        }
        AtributoProducto a = new AtributoProducto();
        a.setNombre(nombre);
        return toDto(repo.save(a));
    }

    @Transactional
    public AtributoDto actualizar(Integer id, AtributoDto dto) {
        AtributoProducto a = buscar(id);
        String nombre = dto.nombre().trim();
        if (repo.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw conflicto("Ya existe un atributo con ese nombre");
        }
        a.setNombre(nombre);
        return toDto(a);
    }

    @Transactional
    public void eliminar(Integer id) {
        AtributoProducto a = buscar(id);
        if (varianteRepo.existeUsoDeAtributo(id)) {
            throw conflicto("Hay variantes que usan valores de este atributo");
        }
        try {
            repo.delete(a);
            repo.flush();
        } catch (DataIntegrityViolationException ex) {
            throw conflicto("El atributo tiene registros relacionados y no se puede eliminar");
        }
    }

    public List<ValorAtributoDto> listarValores(Integer idAtributo) {
        buscar(idAtributo);
        return valorRepo.findByAtributoIdOrderById(idAtributo).stream().map(this::toDto).toList();
    }

    @Transactional
    public ValorAtributoDto crearValor(Integer idAtributo, ValorAtributoDto dto) {
        AtributoProducto a = buscar(idAtributo);
        String valor = dto.valor().trim();
        if (valorRepo.existsByAtributoIdAndValorIgnoreCase(idAtributo, valor)) {
            throw conflicto("Ese atributo ya tiene el valor '" + valor + "'");
        }
        ValorAtributo v = new ValorAtributo();
        v.setAtributo(a);
        v.setValor(valor);
        return toDto(valorRepo.save(v));
    }

    @Transactional
    public ValorAtributoDto actualizarValor(Integer idValor, ValorAtributoDto dto) {
        ValorAtributo v = buscarValor(idValor);
        String valor = dto.valor().trim();
        if (valorRepo.existsByAtributoIdAndValorIgnoreCaseAndIdNot(v.getAtributo().getId(), valor, idValor)) {
            throw conflicto("Ese atributo ya tiene el valor '" + valor + "'");
        }
        v.setValor(valor);
        return toDto(v);
    }

    @Transactional
    public void eliminarValor(Integer idValor) {
        ValorAtributo v = buscarValor(idValor);
        if (varianteRepo.existeUsoDeValor(idValor)) {
            throw conflicto("Hay variantes que usan este valor");
        }
        valorRepo.delete(v);
    }

    private AtributoProducto buscar(Integer id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Atributo no encontrado: " + id));
    }

    private ValorAtributo buscarValor(Integer id) {
        return valorRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Valor no encontrado: " + id));
    }

    private static ResponseStatusException conflicto(String mensaje) {
        return new ResponseStatusException(HttpStatus.CONFLICT, mensaje);
    }

    private AtributoDto toDto(AtributoProducto a) {
        return new AtributoDto(a.getId(), a.getNombre());
    }

    private ValorAtributoDto toDto(ValorAtributo v) {
        return new ValorAtributoDto(v.getId(), v.getAtributo().getId(), v.getAtributo().getNombre(), v.getValor());
    }
}
