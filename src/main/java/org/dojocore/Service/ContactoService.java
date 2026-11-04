package org.dojocore.Service;

import org.dojocore.Dto.ContactoDto;
import org.dojocore.Model.*;
import org.dojocore.Repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ContactoService {

    private final ContactoEmergenciaRepository repo;
    private final ContactoPersonaRepository vinculoRepo;
    private final TutorAlumnoRepository tutorRepo;
    private final PersonaRepository personaRepo;
    private final AlumnoRepository alumnoRepo;

    public ContactoService(ContactoEmergenciaRepository repo, ContactoPersonaRepository vinculoRepo,
                           TutorAlumnoRepository tutorRepo, PersonaRepository personaRepo,
                           AlumnoRepository alumnoRepo){
        this.repo = repo;
        this.vinculoRepo = vinculoRepo;
        this.tutorRepo = tutorRepo;
        this.personaRepo = personaRepo;
        this.alumnoRepo = alumnoRepo;
    }

    public List<ContactoDto> listar(Integer idPersona) {
        buscarPersona(idPersona);
        Set<Integer> tutores = tutorRepo.findByAlumnoId(idPersona).stream()
                .map(t -> t.getId().getIdContacto())
                .collect(Collectors.toSet());
        return vinculoRepo.findByPersonaIdOrderByContactoId(idPersona).stream()
                .map(v -> toDto(v.getContacto(), tutores.contains(v.getContacto().getId())))
                .toList();
    }

    @Transactional
    public ContactoDto crear(Integer idPersona, ContactoDto dto) {
        Persona persona = buscarPersona(idPersona);
        boolean tutor = Boolean.TRUE.equals(dto.esTutor());
        Alumno alumno = null;
        if (tutor) {
            alumno = alumnoRepo.findById(idPersona).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo un alumno puede tener tutor"));
        }
        ContactoEmergencia c = new ContactoEmergencia();
        aplicar(c, dto);
        repo.save(c);
        vinculoRepo.save(new ContactoPersona(persona, c));
        if (tutor) {
            tutorRepo.save(new TutorAlumno(alumno, c));
        }
        return toDto(c, tutor);
    }

    /** Cambia nombre, telefono y parentesco del contacto (no el vinculo ni el tutor). */
    @Transactional
    public ContactoDto actualizar(Integer idContacto, ContactoDto dto) {
        ContactoEmergencia c = buscar(idContacto);
        aplicar(c, dto);
        return toDto(c, null);
    }

    /** Desvincula el contacto de la persona; si nadie mas lo usa, lo borra. */
    @Transactional
    public void quitar(Integer idPersona, Integer idContacto) {
        ContactoPersona vinculo = buscarVinculo(idPersona, idContacto);
        tutorRepo.findById(new TutorAlumnoId(idPersona, idContacto)).ifPresent(t -> {
            tutorRepo.delete(t);
            tutorRepo.flush();
        });
        vinculoRepo.delete(vinculo);
        vinculoRepo.flush();
        if (vinculoRepo.countByContactoId(idContacto) == 0) {
            repo.deleteById(idContacto);
        }
    }

    /** Marca como tutor a un contacto del alumno. Si ya lo era, no pasa nada. */
    @Transactional
    public ContactoDto marcarTutor(Integer idAlumno, Integer idContacto) {
        Alumno alumno = alumnoRepo.findById(idAlumno).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado: " + idAlumno));
        ContactoPersona vinculo = buscarVinculo(idAlumno, idContacto);
        TutorAlumnoId id = new TutorAlumnoId(idAlumno, idContacto);
        if (!tutorRepo.existsById(id)) {
            tutorRepo.save(new TutorAlumno(alumno, vinculo.getContacto()));
        }
        return toDto(vinculo.getContacto(), true);
    }

    @Transactional
    public void quitarTutor(Integer idAlumno, Integer idContacto) {
        TutorAlumno t = tutorRepo.findById(new TutorAlumnoId(idAlumno, idContacto)).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El contacto " + idContacto + " no es tutor del alumno " + idAlumno));
        tutorRepo.delete(t);
    }

    private void aplicar(ContactoEmergencia c, ContactoDto dto) {
        c.setNombre(dto.nombre().trim());
        c.setTelefono(dto.telefono().trim());
        c.setParentesco(dto.parentesco().trim());
    }

    private Persona buscarPersona(Integer id) {
        return personaRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona no encontrada: " + id));
    }

    private ContactoEmergencia buscar(Integer id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Contacto no encontrado: " + id));
    }

    private ContactoPersona buscarVinculo(Integer idPersona, Integer idContacto) {
        return vinculoRepo.findById(new ContactoPersonaId(idPersona, idContacto)).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El contacto " + idContacto + " no es contacto de la persona " + idPersona));
    }

    /** esTutor null = no se sabe en este contexto (por ejemplo al editar solo los datos). */
    private ContactoDto toDto(ContactoEmergencia c, Boolean esTutor) {
        return new ContactoDto(c.getId(), c.getNombre(), c.getTelefono(), c.getParentesco(), esTutor);
    }
}
