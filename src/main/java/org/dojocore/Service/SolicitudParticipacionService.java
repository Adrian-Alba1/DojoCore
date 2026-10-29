package org.dojocore.Service;

import org.dojocore.Dto.ParticipacionEventoDto;
import org.dojocore.Dto.ResolucionDto;
import org.dojocore.Dto.SolicitudParticipacionCrearDto;
import org.dojocore.Dto.SolicitudParticipacionDto;
import org.dojocore.Model.*;
import org.dojocore.Repository.*;
import org.dojocore.Security.Usuario;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SolicitudParticipacionService {

    private static final String PUBLICADO = "PUBLICADO";
    private static final String TORNEO = "TORNEO";

    private final SolicitudParticipacionRepository repo;
    private final EventoRepository eventoRepo;
    private final PersonaRepository personaRepo;
    private final AlumnoRepository alumnoRepo;
    private final CategoriaTorneoRepository categoriaRepo;
    private final ParticipacionEventoRepository participacionRepo;
    private final ParticipacionEventoService participacionService;
    private final EventoAlumnoService eventoAlumnoService;

    public SolicitudParticipacionService(SolicitudParticipacionRepository repo, EventoRepository eventoRepo,
                                         PersonaRepository personaRepo, AlumnoRepository alumnoRepo,
                                         CategoriaTorneoRepository categoriaRepo,
                                         ParticipacionEventoRepository participacionRepo,
                                         ParticipacionEventoService participacionService,
                                         EventoAlumnoService eventoAlumnoService){
        this.repo = repo;
        this.eventoRepo = eventoRepo;
        this.personaRepo = personaRepo;
        this.alumnoRepo = alumnoRepo;
        this.categoriaRepo = categoriaRepo;
        this.participacionRepo = participacionRepo;
        this.participacionService = participacionService;
        this.eventoAlumnoService = eventoAlumnoService;
    }

    public List<SolicitudParticipacionDto> listar(Usuario usuario, Integer idPersona, String estado) {
        Integer filtro = usuario.personal() ? idPersona : usuario.idPersona();
        String est = EstadoSolicitud.filtro(estado);
        List<SolicitudParticipacion> lista;
        if (filtro != null && est != null) {
            lista = repo.findByPersonaIdAndEstadoOrderById(filtro, est);
        } else if (filtro != null) {
            lista = repo.findByPersonaIdOrderById(filtro);
        } else if (est != null) {
            lista = repo.findByEstadoOrderById(est);
        } else {
            lista = repo.findAll(Sort.by("id"));
        }
        return lista.stream().map(this::toDto).toList();
    }

    public SolicitudParticipacionDto obtener(Usuario usuario, Integer id){
        SolicitudParticipacion s = buscar(id);
        usuario.exigirAcceso(s.getPersona().getId());
        return toDto(s);
    }

    @Transactional
    public SolicitudParticipacionDto crear(Usuario usuario, SolicitudParticipacionCrearDto dto) {
        Integer idPersona = usuario.actuarComo(dto.idPersona());
        Persona persona = personaRepo.findById(idPersona).orElseThrow(() ->
                invalida("La Persona No Existe: " + idPersona));
        if (!Boolean.TRUE.equals(persona.getActivo())) {
            throw invalida("La Persona Está Inactiva: " + idPersona);
        }
        Evento evento = eventoRepo.findById(dto.idEvento()).orElseThrow(() ->
                invalida("El Evento No Existe: " + dto.idEvento()));
        if (!PUBLICADO.equals(evento.getEstado())) {
            throw invalida("Solo Se Puede Solicitar Participación En Eventos Publicados (Estado Actual: "
                    + evento.getEstado() + ")");
        }
        Alumno alumno = alumnoRepo.findById(idPersona).orElse(null);
        if (alumno != null && evento.tieneRangoDeGrados() && !evento.admiteGrado(persona.getGradoActual())) {
            throw invalida("El Grado Del Alumno Está Fuera Del Rango Permitido Para Este Evento");
        }
        CategoriaTorneo categoria = resolverCategoria(evento, dto.idCategoria());

        boolean yaParticipa = categoria == null
                ? participacionRepo.existsByEventoIdAndPersonaIdAndCategoriaIsNull(evento.getId(), idPersona)
                : participacionRepo.existsByEventoIdAndPersonaIdAndCategoriaId(evento.getId(), idPersona, categoria.getId());
        if (yaParticipa) {
            throw conflicto("La Persona Ya Esta Participando En Este Evento" + (categoria != null ? " Y Categoría" : ""));
        }
        boolean pendiente = categoria == null
                ? repo.existsByEventoIdAndPersonaIdAndCategoriaIsNullAndEstado(evento.getId(), idPersona,
                EstadoSolicitud.PENDIENTE)
                : repo.existsByEventoIdAndPersonaIdAndCategoriaIdAndEstado(evento.getId(), idPersona,
                categoria.getId(), EstadoSolicitud.PENDIENTE);
        if (pendiente) {
            throw conflicto("Ya Hay Una Solicitud Pendiente Para Este Evento");
        }
        SolicitudParticipacion s = new SolicitudParticipacion();
        s.setEvento(evento);
        s.setPersona(persona);
        s.setCategoria(categoria);
        s.setFechaSolicitud(LocalDateTime.now());
        s.setEstado(EstadoSolicitud.PENDIENTE);
        return toDto(repo.save(s));
    }

    @Transactional
    public SolicitudParticipacionDto resolver(Usuario usuario, Integer id, ResolucionDto dto) {
        usuario.exigirPersonal();
        SolicitudParticipacion s = buscar(id);
        String nuevo = EstadoSolicitud.resolucion(dto.estado());
        EstadoSolicitud.exigirPendiente(s.getEstado());
        if (EstadoSolicitud.APROBADA.equals(nuevo)) {
            Integer idEvento = s.getEvento().getId();
            Integer idPersona = s.getPersona().getId();
            Alumno alumno = alumnoRepo.findById(idPersona).orElse(null);
            if (alumno != null && alumno.getEventos().stream().noneMatch(e -> e.getId().equals(idEvento))) {
                eventoAlumnoService.agregarAlumno(idEvento, idPersona);
            }
            Integer idCategoria = s.getCategoria() != null ? s.getCategoria().getId() : null;
            participacionService.crear(new ParticipacionEventoDto(null, idEvento, idPersona, null,
                    idCategoria, null, null, null, null, null, null));
        }
        s.setEstado(nuevo);
        return toDto(s);
    }

    @Transactional
    public void eliminar(Usuario usuario, Integer id) {
        SolicitudParticipacion s = buscar(id);
        usuario.exigirAcceso(s.getPersona().getId());
        EstadoSolicitud.exigirPendiente(s.getEstado());
        repo.delete(s);
    }

    private CategoriaTorneo resolverCategoria(Evento evento, Integer idCategoria) {
        if (!TORNEO.equals(evento.getTipo().getNombre())) {
            if (idCategoria != null) {
                throw invalida("Solo Los Torneos Tienen Categorías");
            }
            return null;
        }
        if (idCategoria == null) {
            throw invalida("La Categoría Es Obligatoria En Los Torneos");
        }
        CategoriaTorneo c = categoriaRepo.findById(idCategoria).orElseThrow(() ->
                invalida("La Categoría No Existe: " + idCategoria));
        if (!c.getTorneo().getId().equals(evento.getId())) {
            throw invalida("La Categoría " + idCategoria + " No Pertenece Al Evento " + evento.getId());
        }
        return c;
    }

    private SolicitudParticipacion buscar(Integer id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud No Encontrada: " + id));
    }

    private static ResponseStatusException invalida(String mensaje) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }

    private static ResponseStatusException conflicto(String mensaje) {
        return new ResponseStatusException(HttpStatus.CONFLICT, mensaje);
    }

    private SolicitudParticipacionDto toDto(SolicitudParticipacion s) {
        return new SolicitudParticipacionDto(s.getId(), s.getEvento().getId(), s.getEvento().getNombre(),
                s.getPersona().getId(), s.getPersona().getNombre(),
                s.getCategoria() != null ? s.getCategoria().getId() : null,
                s.getFechaSolicitud(), s.getEstado());
    }
}
