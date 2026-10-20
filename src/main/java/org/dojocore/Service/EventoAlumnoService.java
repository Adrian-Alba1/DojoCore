package org.dojocore.Service;

import org.dojocore.Dto.AlumnoDto;
import org.dojocore.Dto.EventoDto;
import org.dojocore.Model.Alumno;
import org.dojocore.Model.Evento;
import org.dojocore.Model.Grado;
import org.dojocore.Repository.AlumnoRepository;
import org.dojocore.Repository.EventoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EventoAlumnoService {

    private static final String PUBLICADO = "PUBLICADO";
    private static final String REALIZADO = "REALIZADO";
    private static final String RESTRINGIDO = "RESTRINGIDO";

    private final AlumnoRepository alumnoRepo;
    private final EventoRepository eventoRepo;
    private final AlumnoService alumnoService;
    private final EventoService eventoService;

    public EventoAlumnoService(AlumnoRepository alumnoRepo,
                               EventoRepository eventoRepo,
                               AlumnoService alumnoService,
                               EventoService eventoService){
        this.alumnoRepo = alumnoRepo;
        this.eventoRepo = eventoRepo;
        this.alumnoService = alumnoService;
        this.eventoService = eventoService;
    }

    public List<AlumnoDto> alumnosDeEvento(Integer idEvento, boolean incluirInactivos){
        buscarEvento(idEvento);
        List<Alumno> alumnos = incluirInactivos
                ? alumnoRepo.findByEventosIdOrderById(idEvento)
                : alumnoRepo.findByEventosIdAndPersonaActivoOrderById(idEvento, true);
        return alumnos.stream().map(alumnoService::toDto).toList();
    }

    public List<EventoDto> eventosDeAlumno(Integer idAlumno) {
        return buscarAlumno(idAlumno).getEventos().stream()
                .sorted(Comparator.comparing(Evento::getId))
                .map(eventoService::toDto)
                .toList();
    }

    public List<EventoDto> eventosDisponibles(Integer idAlumno) {
        Alumno alumno = buscarAlumno(idAlumno);
        return eventoRepo.findByEstadoOrderById(PUBLICADO).stream()
                .filter(e -> visiblePara(e, alumno))
                .map(eventoService::toDto)
                .toList();
    }

    private boolean visiblePara(Evento evento, Alumno alumno) {
        if (evento.tieneRangoDeGrados()) {
            return evento.admiteGrado(alumno.getPersona().getGradoActual());
        }
        if (RESTRINGIDO.equalsIgnoreCase(evento.getVisibilidad())) {
            return alumno.getEventos().contains(evento);
        }
        return true;
    }

    @Transactional
    public AlumnoDto agregarAlumno(Integer idEvento, Integer idAlumno) {
        Evento evento = buscarEvento(idEvento);
        Alumno alumno = buscarAlumno(idAlumno);
        if (!Boolean.TRUE.equals(alumno.getPersona().getActivo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El alumno está inactivo: " + idAlumno);
        }
        if (REALIZADO.equals(evento.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El evento ya fue realizado; no se pueden agregar alumnos");
        }
        if (!PUBLICADO.equals(evento.getEstado())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo se pueden agregar alumnos a eventos publicados (estado actual: " + evento.getEstado() + ")");
        }
        Grado gradoAlumno = alumno.getPersona().getGradoActual();
        if (evento.tieneRangoDeGrados() && !evento.admiteGrado(gradoAlumno)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El grado del alumno (" + gradoAlumno.getNombre() + ") está fuera del rango permitido para este evento");
        }
        if (!alumno.getEventos().add(evento)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El alumno ya está vinculado a ese evento");
        }
        return alumnoService.toDto(alumno);
    }

    @Transactional
    public void quitarAlumno(Integer idEvento, Integer idAlumno) {
        Alumno alumno = buscarAlumno(idAlumno);
        if (!alumno.getEventos().removeIf(e -> e.getId().equals(idEvento))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "El alumno no está vinculado al evento " + idEvento);
        }
    }

    private Evento buscarEvento(Integer id) {
        return eventoRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado: " + id));
    }

    private Alumno buscarAlumno(Integer id) {
        return alumnoRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado: " + id));
    }
}
