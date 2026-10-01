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

    public List<AlumnoDto> listar() {
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public AlumnoDto obtener(Integer id) {
        return toDto(buscar(id));
    }

    //De persona a Alumno
    @Transactional
    public AlumnoDto crear(AlumnoDto dto) {
        Persona persona = personaRepo.findById(dto.idPersona()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Persona No encontrada: " + dto.idPersona()));
        if (repo.existsById(persona.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta persona ya es un Alumno");
        }
        Alumno a = new Alumno();
        a.setPersona(persona);
        a.setFechaIngreso(dto.fechaIngreso());
        a.setSenseiPrincipal(resolversensei(dto.idSenseiPrincipal()));
        return toDto(a);
    }
    //

    @Transactional
    public AlumnoDto actualizar(Integer id, AlumnoDto dto){
        Alumno a = buscar(id);
        a.setFechaIngreso(dto.fechaIngreso());
        a.setSenseiPrincipal(resolversensei(dto.idSenseiPrincipal()));
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
        if (idSensei == null){
            return null;
        }
        return senseiRepo.findById(idSensei).orElseThrow(()->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "El Sensei no existe: "+idSensei));
    }

    private AlumnoDto toDto(Alumno a){
        Integer idSensei = a.getSenseiPrincipal() != null ? a.getSenseiPrincipal().getId() : null;
        return new AlumnoDto(a.getId(), a.getPersona().getNombre(), a.getFechaIngreso(), idSensei);
    }
}