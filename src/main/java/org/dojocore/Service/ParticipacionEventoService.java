package org.dojocore.Service;

import org.dojocore.Dto.ParticipacionEventoDto;
import org.dojocore.Model.*;
import org.dojocore.Repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
public class ParticipacionEventoService {
    private static final String TORNEO = "TORNEO";
    private static final String EXAMEN = "EXAMEN";
    private static final String APROBADO = "Aprobado";

    private final ParticipacionEventoRepository repo;
    private final EventoRepository eventoRepo;
    private final PersonaRepository personaRepo;
    private final CategoriaTorneoRepository categoriaRepo;
    private final ResultadoEventoRepository resultadoRepo;
    private final GradoRepository gradoRepo;

    public ParticipacionEventoService(ParticipacionEventoRepository repo,
                                      EventoRepository eventoRepo,
                                      PersonaRepository personaRepo,
                                      CategoriaTorneoRepository categoriaRepo,
                                      ResultadoEventoRepository resultadoRepo,
                                      GradoRepository gradoRepo) {
        this.repo = repo;
        this.eventoRepo = eventoRepo;
        this.personaRepo = personaRepo;
        this.categoriaRepo = categoriaRepo;
        this.resultadoRepo = resultadoRepo;
        this.gradoRepo = gradoRepo;
    }

    //Filtros opcionales: idEvento y/o idPersona. Sin filtros devuelve todas.
    public List<ParticipacionEventoDto> listar(Integer idEvento, Integer idPersona) {
        List<ParticipacionEvento> lista;
        if (idEvento != null && idPersona != null) {
            lista = repo.findByEventoIdAndPersonaId(idEvento, idPersona);
        } else if (idEvento != null) {
            lista = repo.findByEventoId(idEvento);
        } else if (idPersona != null) {
            lista = repo.findByPersonaId(idPersona);
        } else {
            lista = repo.findAll();
        }
        return lista.stream().map(this::toDto).toList();
    }

    public ParticipacionEventoDto obtener(Integer id) {
        return toDto(buscar(id));
    }

    @Transactional
    public ParticipacionEventoDto crear(ParticipacionEventoDto dto) {
        Evento evento = eventoRepo.findById(dto.idEvento()).orElseThrow(() ->
                invalida("Evento no existe: " + dto.idEvento()));
        Persona persona = personaRepo.findById(dto.idPersona()).orElseThrow(() ->
                invalida("Persona no existe: " + dto.idPersona()));
        if (!Boolean.TRUE.equals(persona.getActivo())) {
            throw invalida("La persona está inactiva: " + persona.getId());
        }

        CategoriaTorneo categoria = resolverCategoria(evento, dto.idCategoria());

        boolean duplicada = categoria == null
                ? repo.existsByEventoIdAndPersonaIdAndCategoriaIsNull(evento.getId(), persona.getId())
                : repo.existsByEventoIdAndPersonaIdAndCategoriaId(evento.getId(), persona.getId(), categoria.getId());
        if (duplicada) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La persona ya está registrada en este evento" + (categoria != null ? " y categoría" : ""));
        }

        ParticipacionEvento p = new ParticipacionEvento();
        p.setEvento(evento);
        p.setPersona(persona);
        p.setCategoria(categoria);
        aplicarResultado(p, evento, dto);
        return toDto(repo.save(p));
    }

    @Transactional
    public ParticipacionEventoDto actualizar(Integer id, ParticipacionEventoDto dto) {
        ParticipacionEvento p = buscar(id);
        Integer idCategoriaActual = p.getCategoria() != null ? p.getCategoria().getId() : null;
        if (!p.getEvento().getId().equals(dto.idEvento())
                || !p.getPersona().getId().equals(dto.idPersona())
                || !Objects.equals(idCategoriaActual, dto.idCategoria())) {
            throw invalida("No se pueden cambiar el evento, la persona ni la categoría; "
                    + "elimina la participación y crea otra");
        }
        aplicarResultado(p, p.getEvento(), dto);
        return toDto(p);
    }

    @Transactional
    public void eliminar(Integer id) {
        repo.delete(buscar(id));
    }

    private ParticipacionEvento buscar(Integer id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Participación no encontrada: " + id));
    }

    /** Torneo: categoría obligatoria y del mismo torneo. Otros eventos: sin categoría. */
    private CategoriaTorneo resolverCategoria(Evento evento, Integer idCategoria) {
        boolean esTorneo = TORNEO.equals(evento.getTipo().getNombre());
        if (!esTorneo) {
            if (idCategoria != null) {
                throw invalida("Solo los torneos tienen categorías");
            }
            return null;
        }
        if (idCategoria == null) {
            throw invalida("La categoría es obligatoria en los torneos");
        }
        CategoriaTorneo c = categoriaRepo.findById(idCategoria).orElseThrow(() ->
                invalida("Categoría no existe: " + idCategoria));
        if (!c.getTorneo().getId().equals(evento.getId())) {
            throw invalida("La categoría " + idCategoria + " no pertenece al evento " + evento.getId());
        }
        return c;
    }

    /** Valida y asigna resultado, grados y mención. */
    private void aplicarResultado(ParticipacionEvento p, Evento evento, ParticipacionEventoDto dto) {
        String tipoEvento = evento.getTipo().getNombre();

        ResultadoEvento resultado = null;
        if (dto.idResultado() != null) {
            resultado = resultadoRepo.findById(dto.idResultado()).orElseThrow(() ->
                    invalida("Resultado no existe: " + dto.idResultado()));
            boolean aplica = resultado.getTiposEvento().stream()
                    .anyMatch(t -> t.getId().equals(evento.getTipo().getId()));
            if (!aplica) {
                throw invalida("El resultado '" + resultado.getNombre()
                        + "' no aplica a eventos de tipo " + tipoEvento);
            }
        }

        boolean examenAprobado = EXAMEN.equals(tipoEvento)
                && resultado != null && APROBADO.equals(resultado.getNombre());
        Grado anterior = null;
        Grado nuevo = null;
        if (examenAprobado) {
            if (dto.idGradoAnterior() == null || dto.idGradoNuevo() == null) {
                throw invalida("Un examen aprobado requiere idGradoAnterior e idGradoNuevo");
            }
            if (dto.idGradoAnterior().equals(dto.idGradoNuevo())) {
                throw invalida("El grado nuevo debe ser distinto al anterior");
            }
            anterior = gradoRepo.findById(dto.idGradoAnterior()).orElseThrow(() ->
                    invalida("Grado no existe: " + dto.idGradoAnterior()));
            nuevo = gradoRepo.findById(dto.idGradoNuevo()).orElseThrow(() ->
                    invalida("Grado no existe: " + dto.idGradoNuevo()));
        } else if (dto.idGradoAnterior() != null || dto.idGradoNuevo() != null) {
            throw invalida("Los grados solo se registran en exámenes aprobados");
        }

        p.setResultado(resultado);
        p.setGradoAnterior(anterior);
        p.setGradoNuevo(nuevo);
        p.setMencion(dto.mencion() == null || dto.mencion().isBlank() ? null : dto.mencion().trim());
    }

    private static ResponseStatusException invalida(String mensaje) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }

    private ParticipacionEventoDto toDto(ParticipacionEvento p) {
        CategoriaTorneo c = p.getCategoria();
        ResultadoEvento r = p.getResultado();
        return new ParticipacionEventoDto(
                p.getId(),
                p.getEvento().getId(),
                p.getPersona().getId(),
                p.getPersona().getNombre(),
                c != null ? c.getId() : null,
                c != null ? c.getTipo() : null,
                r != null ? r.getId() : null,
                r != null ? r.getNombre() : null,
                p.getGradoAnterior() != null ? p.getGradoAnterior().getId() : null,
                p.getGradoNuevo() != null ? p.getGradoNuevo().getId() : null,
                p.getMencion());
    }
}
