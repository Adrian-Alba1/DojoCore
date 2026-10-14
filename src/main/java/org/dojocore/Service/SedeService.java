package org.dojocore.Service;

import org.dojocore.Dto.SedeDto;
import org.dojocore.Model.Sede;
import org.dojocore.Repository.HorarioRepository;
import org.dojocore.Repository.SedeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SedeService {

    private final SedeRepository repo;
    private final HorarioRepository horarioRepo;

    public SedeService(SedeRepository repo, HorarioRepository horarioRepo){
        this.repo = repo;
        this.horarioRepo = horarioRepo;
    }

    public List<SedeDto> listar(boolean incluirInactivos){
        List<Sede> sedes = incluirInactivos ? repo.findAll(Sort.by("id")) : repo.findByActivoOrderById(true);
        return sedes.stream().map(this::toDto).toList();
    }

    public SedeDto obtener(Integer id){
        return toDto(buscar(id));
    }

    @Transactional
    public SedeDto crear(SedeDto dto){
        String nombre = dto.nombre().trim();
        if (repo.existsByNombreIgnoreCase(nombre)){
            throw conflicto("Ya Existe Una Sede Con Ese Nombre");
        }
        boolean central = Boolean.TRUE.equals(dto.esCentral());
        if (central && repo.existsByEsCentralTrue()){
            throw conflicto("Ya Existe Una Sede Central; Solo Pede Existir Una");
        }
        Sede s = new Sede();
        s.setNombre(nombre);
        s.setDireccion(dto.direccion().trim());
        s.setEsCentral(central);
        s.setActivo(dto.esActivo()==null || dto.esActivo());
        return toDto(repo.save(s));
    }

    @Transactional
    public SedeDto actualizar(Integer id, SedeDto dto) {
        Sede s = buscar(id);
        String nombre = dto.nombre().trim();
        if (repo.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw conflicto("Ya existe una sede con ese nombre");
        }
        boolean central = dto.esCentral() != null ? dto.esCentral() : s.getEsCentral();
        if (central && repo.existsByEsCentralTrueAndIdNot(id)) {
            throw conflicto("Ya existe otra sede central; solo puede haber una");
        }
        s.setNombre(nombre);
        s.setDireccion(dto.direccion().trim());
        s.setEsCentral(central);
        if (dto.esActivo() != null && !dto.esActivo().equals(s.getActivo())) {
            if (dto.esActivo()) {
                s.setActivo(true);
            } else {
                desactivar(s);
            }
        }
        return toDto(s);
    }

    @Transactional
    public void eliminar(Integer id){
        desactivar(buscar(id));
    }

    public void desactivar(Sede s){
        if (horarioRepo.existsBySedeIdAndActivoTrue(s.getId())){
            throw conflicto("La Sede Tiene Horarios Activos; desactívalos Primero");
        }
        s.setActivo(false);
    }

    private Sede buscar(Integer id){
        return repo.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Sede No Encontrada: "+ id));
    }

    private static ResponseStatusException conflicto(String mensaje){
        return new ResponseStatusException(HttpStatus.CONFLICT, mensaje);
    }

    private SedeDto toDto(Sede s){
        return new SedeDto(s.getId(), s.getNombre(), s.getDireccion(), s.getEsCentral(), s.getActivo());
    }
}
