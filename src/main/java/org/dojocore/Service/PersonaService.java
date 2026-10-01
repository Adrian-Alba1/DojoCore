package org.dojocore.Service;

import org.springframework.transaction.annotation.Transactional;
import org.dojocore.Dto.PersonaDto;
import org.dojocore.Model.Grado;
import org.dojocore.Model.Persona;
import org.dojocore.Repository.GradoRepository;
import org.dojocore.Repository.PersonaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PersonaService {

    private final PersonaRepository repo;
    private final GradoRepository gradorepo;

    public PersonaService(PersonaRepository repo, GradoRepository gradorepo){
        this.repo = repo;
        this.gradorepo = gradorepo;
    }

    public List<PersonaDto> listar() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public PersonaDto obtener(Integer id){
        return toDto(buscar(id));
    }
    @Transactional
    public PersonaDto crear(PersonaDto dto) {
        if (repo.existsByEmail(dto.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una persona con ese email");
        }
        Persona p = new Persona();
        aplicar(p, dto);
        return toDto(repo.save(p));
    }

    @Transactional
    public PersonaDto actualizar(Integer id, PersonaDto dto) {
        Persona p = buscar(id);
        if (!p.getEmail().equalsIgnoreCase(dto.email()) && repo.existsByEmail(dto.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una persona con ese email");
        }
        aplicar(p, dto);
        return toDto(p);
    }

    @Transactional
    public void eliminar(Integer id) {
        repo.delete(buscar(id));
    }

    private Persona buscar(Integer id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada: " + id));
    }

    private void aplicar(Persona p, PersonaDto dto) {
        Grado grado = gradorepo.findById(dto.idGradoActual()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Grado no existe: " + dto.idGradoActual()));
        p.setNombre(dto.nombre());
        p.setFechaNacimiento(dto.fechaNacimiento());
        p.setTelefono(dto.telefono());
        p.setEmail(dto.email());
        p.setFoto(dto.foto());
        p.setActivo(dto.activo() != null ? dto.activo() : Boolean.TRUE);
        p.setGradoActual(grado);
    }

    private PersonaDto toDto(Persona p) {
        return new PersonaDto(p.getId(), p.getNombre(), p.getFechaNacimiento(), p.getTelefono(),
                p.getEmail(), p.getFoto(), p.getActivo(), p.getGradoActual().getId());
    }
}
