package org.dojocore.Service;

import org.dojocore.Dto.AlumnoDto;
import org.dojocore.Model.Alumno;
import org.dojocore.Model.Persona;
import org.dojocore.Model.Sensei;
import org.dojocore.Repository.AlumnoRepository;
import org.dojocore.Repository.PersonaRepository;
import org.dojocore.Repository.SenseiRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class AlumnoService {

    private final AlumnoRepository repo;
    private final PersonaRepository personaRepo;
    private final SenseiRepository senseiRepo;

    public AlumnoService(AlumnoRepository repo, PersonaRepository personaRepo, SenseiRepository senseiRepo) {
        this.repo = repo;
        this.personaRepo = personaRepo;
        this.senseiRepo = senseiRepo;
    }

    public List<AlumnoDto> listar(boolean incluirInactivos) {
        List<Alumno> alumnos = incluirInactivos ? repo.findAll() : repo.findByPersonaActivo(true);
        return alumnos.stream().map(this::toDto).toList();
    }

    public AlumnoDto obtener(Integer id) {
        return toDto(buscar(id));
    }

    //De persona a Alumno
    @Transactional
    public AlumnoDto crear(AlumnoDto dto) {
        Persona persona = personaRepo.findById(dto.idPersona()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Persona no existe: " + dto.idPersona()));
        if (!Boolean.TRUE.equals(persona.getActivo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La persona está inactiva: " + persona.getId());
        }
        if (repo.existsById(persona.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esa persona ya es alumno");
        }
        Alumno a = new Alumno();
        a.setPersona(persona);
        a.setFechaIngreso(dto.fechaIngreso());
        a.setSenseiPrincipal(resolversensei(dto.idSenseiPrincipal()));
        return toDto(repo.save(a));
    }

    @Transactional
    public AlumnoDto actualizar(Integer id, AlumnoDto dto){
        Alumno a = buscar(id);
        a.setFechaIngreso(dto.fechaIngreso());
        Integer idActual = a.getSenseiPrincipal() != null ? a.getSenseiPrincipal().getId() : null;
        if (!Objects.equals(idActual, dto.idSenseiPrincipal())) {
            a.setSenseiPrincipal(resolversensei(dto.idSenseiPrincipal()));
        }
        return toDto(a);
    }

    @Transactional
    public void eliminar(Integer id){
        repo.delete(buscar(id));
    }

    private Alumno buscar(Integer id){
        return repo.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no Encontrado: "+ id));
    }

    private Sensei resolversensei(Integer idSensei){
        if (idSensei == null) {
            return null;
        }
        Sensei sensei = senseiRepo.findById(idSensei).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sensei no existe: " + idSensei));
        if (!Boolean.TRUE.equals(sensei.getPersona().getActivo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El sensei está inactivo: " + idSensei);
        }
        return sensei;
    }

    private AlumnoDto toDto(Alumno a){
        Integer idSensei = a.getSenseiPrincipal() != null ? a.getSenseiPrincipal().getId() : null;
        return new AlumnoDto(a.getId(), a.getPersona().getNombre(), a.getFechaIngreso(), idSensei);
    }
}