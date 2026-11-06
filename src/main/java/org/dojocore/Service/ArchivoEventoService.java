package org.dojocore.Service;

import org.dojocore.Dto.ArchivoEventoDto;
import org.dojocore.Model.Alumno;
import org.dojocore.Model.ArchivoEvento;
import org.dojocore.Model.Evento;
import org.dojocore.Repository.AlumnoRepository;
import org.dojocore.Repository.ArchivoEventoRepository;
import org.dojocore.Repository.EventoRepository;
import org.dojocore.Security.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ArchivoEventoService {

    private static final String RESTRINGIDO = "RESTRINGIDO";

    private final ArchivoEventoRepository repo;
    private final EventoRepository eventoRepo;
    private final AlumnoRepository alumnoRepo;

    public ArchivoEventoService(ArchivoEventoRepository repo, EventoRepository eventoRepo,
                                AlumnoRepository alumnoRepo){
        this.repo = repo;
        this.eventoRepo = eventoRepo;
        this.alumnoRepo = alumnoRepo;
    }

    public List<ArchivoEventoDto> listar(Usuario usuario, Integer idEvento){
        exigirVisible(usuario, buscarEvento(idEvento));
        return repo.findByEventoIdOrderById(idEvento).stream().map(this::toDto).toList();
    }

    public ArchivoEventoDto obtener(Usuario usuario, Integer id){
        ArchivoEvento a = buscar(id);
        exigirVisible(usuario, a.getEvento());
        return toDto(a);
    }

    @Transactional
    public ArchivoEventoDto crear(Integer idEvento, ArchivoEventoDto dto){
        ArchivoEvento a = new ArchivoEvento();
        a.setEvento(buscarEvento(idEvento));
        a.setFechaSubida(LocalDateTime.now());
        aplicar(a, dto);
        return toDto(repo.save(a));
    }

    @Transactional
    public ArchivoEventoDto actualizar(Integer id, ArchivoEventoDto dto){
        ArchivoEvento a = buscar(id);
        aplicar(a, dto);
        return toDto(a);
    }

    @Transactional
    public void eliminar(Integer id){
        repo.delete(buscar(id));
    }

    private void aplicar(ArchivoEvento a, ArchivoEventoDto dto){
        a.setNombre(dto.nombre().trim());
        a.setRuta(dto.ruta().trim());
        a.setTipo(dto.tipo() == null || dto.tipo().isBlank() ? null : dto.tipo().trim());
    }

    private void exigirVisible(Usuario usuario, Evento evento){
        if (usuario.personal() || !RESTRINGIDO.equalsIgnoreCase(evento.getVisibilidad())){
            return;
        }
        Alumno alumno = usuario.idPersona() == null ? null : alumnoRepo.findById(usuario.idPersona()).orElse(null);
        boolean vinculado = alumno != null
                && alumno.getEventos().stream().anyMatch(e -> e.getId().equals(evento.getId()));
        boolean enRango = alumno != null && evento.tieneRangoDeGrados()
                && evento.admiteGrado(alumno.getPersona().getGradoActual());
        if (!vinculado && !enRango){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No Tienes Los Permisos Para Ver Los Archivos De Este Evento");
        }
    }

    private Evento buscarEvento(Integer id){
        return eventoRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento No Encontrado: " + id));
    }

    private ArchivoEvento buscar(Integer id){
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo No Encontrado: " + id));
    }

    private ArchivoEventoDto toDto(ArchivoEvento a){
        return new ArchivoEventoDto(a.getId(), a.getEvento().getId(), a.getNombre(), a.getRuta(),
                a.getTipo(), a.getFechaSubida());
    }
}
