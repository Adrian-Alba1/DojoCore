package org.dojocore.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.dojocore.Dto.ResolucionDto;
import org.dojocore.Dto.SolicitudHorarioCrearDto;
import org.dojocore.Dto.SolicitudHorarioDto;
import org.dojocore.Model.Alumno;
import org.dojocore.Model.Horario;
import org.dojocore.Model.SolicitudHorario;
import org.dojocore.Repository.AlumnoRepository;
import org.dojocore.Repository.HorarioRepository;
import org.dojocore.Repository.SolicitudHorarioRepository;
import org.dojocore.Security.Usuario;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class SolicitudHorarioService {

    private final SolicitudHorarioRepository repo;
    private final AlumnoRepository alumnoRepo;
    private final HorarioRepository horarioRepo;
    private final AsignacionHorarioService asignacion;

    public SolicitudHorarioService(SolicitudHorarioRepository repo, AlumnoRepository alumnoRepo,
                                   HorarioRepository horarioRepo, AsignacionHorarioService asignacion) {
        this.repo = repo;
        this.alumnoRepo = alumnoRepo;
        this.horarioRepo = horarioRepo;
        this.asignacion = asignacion;
    }

    /** El personal puede filtrar por idAlumno y estado; los demas ven solo las suyas. */
    public List<SolicitudHorarioDto> listar(Usuario usuario, Integer idAlumno, String estado) {
        Integer filtro = usuario.personal() ? idAlumno : usuario.idPersona();
        String est = EstadoSolicitud.filtro(estado);
        List<SolicitudHorario> lista;
        if (filtro != null && est != null) {
            lista = repo.findByAlumnoIdAndEstadoOrderById(filtro, est);
        } else if (filtro != null) {
            lista = repo.findByAlumnoIdOrderById(filtro);
        } else if (est != null) {
            lista = repo.findByEstadoOrderById(est);
        } else {
            lista = repo.findAll(Sort.by("id"));
        }
        return lista.stream().map(this::toDto).toList();
    }

    public SolicitudHorarioDto obtener(Usuario usuario, Integer id) {
        SolicitudHorario s = buscar(id);
        usuario.exigirAcceso(s.getAlumno().getId());
        return toDto(s);
    }

    @Transactional
    public SolicitudHorarioDto crear(Usuario usuario, SolicitudHorarioCrearDto dto) {
        Integer idAlumno = usuario.actuarComo(dto.idAlumno());
        Alumno alumno = alumnoRepo.findById(idAlumno).orElseThrow(() ->
                invalida("La persona " + idAlumno + " no es alumno"));
        if (!Boolean.TRUE.equals(alumno.getPersona().getActivo())) {
            throw invalida("El alumno está inactivo: " + idAlumno);
        }
        Horario horario = horarioRepo.findById(dto.idHorario()).orElseThrow(() ->
                invalida("Horario no existe: " + dto.idHorario()));
        if (!Boolean.TRUE.equals(horario.isActivo())) {
            throw invalida("El horario está inactivo: " + dto.idHorario());
        }
        if (alumno.getHorarios().stream().anyMatch(h -> h.getId().equals(horario.getId()))) {
            throw conflicto("El alumno ya está asignado a ese horario");
        }
        if (repo.existsByAlumnoIdAndHorarioIdAndEstado(idAlumno, horario.getId(), EstadoSolicitud.PENDIENTE)) {
            throw conflicto("Ya hay una solicitud pendiente para ese horario");
        }
        SolicitudHorario s = new SolicitudHorario();
        s.setAlumno(alumno);
        s.setHorario(horario);
        s.setFechaSolicitud(LocalDateTime.now());
        s.setEstado(EstadoSolicitud.PENDIENTE);
        return toDto(repo.save(s));
    }

    @Transactional
    public SolicitudHorarioDto resolver(Usuario usuario, Integer id, ResolucionDto dto) {
        usuario.exigirPersonal();
        SolicitudHorario s = buscar(id);
        String nuevo = EstadoSolicitud.resolucion(dto.estado());
        EstadoSolicitud.exigirPendiente(s.getEstado());
        if (EstadoSolicitud.APROBADA.equals(nuevo)) {
            asignacion.asignarAlumno(s.getAlumno().getId(), s.getHorario().getId());
        }
        s.setEstado(nuevo);
        return toDto(s);
    }

    /** Retirar una solicitud: la borra su dueño (o el personal) mientras siga PENDIENTE. */
    @Transactional
    public void eliminar(Usuario usuario, Integer id) {
        SolicitudHorario s = buscar(id);
        usuario.exigirAcceso(s.getAlumno().getId());
        EstadoSolicitud.exigirPendiente(s.getEstado());
        repo.delete(s);
    }

    private SolicitudHorario buscar(Integer id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada: " + id));
    }

    private static ResponseStatusException invalida(String mensaje) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }

    private static ResponseStatusException conflicto(String mensaje) {
        return new ResponseStatusException(HttpStatus.CONFLICT, mensaje);
    }

    private SolicitudHorarioDto toDto(SolicitudHorario s) {
        return new SolicitudHorarioDto(s.getId(), s.getAlumno().getId(), s.getAlumno().getPersona().getNombre(),
                s.getHorario().getId(), s.getFechaSolicitud(), s.getEstado());
    }
}
