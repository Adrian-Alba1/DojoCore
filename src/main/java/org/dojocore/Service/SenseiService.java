package org.dojocore.Service;

import org.dojocore.Dto.SenseiDto;
import org.dojocore.Model.Persona;
import org.dojocore.Model.Sensei;
import org.dojocore.Repository.PersonaRepository;
import org.dojocore.Repository.SenseiRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SenseiService {

    private final SenseiRepository repo;
    private final PersonaRepository personaRepo;

    public SenseiService(SenseiRepository repo, PersonaRepository personaRepo) {
        this.repo = repo;
        this.personaRepo = personaRepo;
    }

    public List<SenseiDto> listar() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public SenseiDto obtener(Integer id) {
        return toDto(buscar(id));
    }

    /** Convierte una persona existente en sensei. */
    @Transactional
    public SenseiDto crear(SenseiDto dto) {
        Persona persona = personaRepo.findById(dto.idPersona()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Persona no existe: " + dto.idPersona()));
        if (repo.existsById(persona.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esa persona ya es sensei");
        }
        Sensei s = new Sensei();
        s.setPersona(persona);
        return toDto(repo.save(s));
    }

    @Transactional
    public void eliminar(Integer id) {
        repo.delete(buscar(id));
    }

    Sensei buscar(Integer id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensei no encontrado: " + id));
    }

    private SenseiDto toDto(Sensei s) {
        return new SenseiDto(s.getId(), s.getPersona().getNombre());
    }
}
