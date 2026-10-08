package org.dojocore.Service;

import org.dojocore.Dto.EventoDto;
import org.dojocore.Model.*;
import org.dojocore.Repository.EventoRepository;
import org.dojocore.Repository.SenseiRepository;
import org.dojocore.Repository.TipoEventoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@Transactional(readOnly = true)

    public class EventoService {

    private final EventoRepository repo;
    private final TipoEventoRepository tipoRepo;
    private final SenseiRepository senseiRepo;

    public EventoService(EventoRepository repo, TipoEventoRepository tipoRepo, SenseiRepository senseiRepo) {
        this.repo = repo;
        this.tipoRepo = tipoRepo;
        this.senseiRepo = senseiRepo;
    }

    public List<EventoDto> listar(String tipo) {
        List<Evento> eventos = (tipo == null || tipo.isBlank())
                ? repo.findAll()
                : repo.findByTipoNombre(tipo.trim().toUpperCase(Locale.ROOT));
        return eventos.stream().map(this::toDto).toList();
    }

    public EventoDto obtener(Integer id) {
        return toDto(buscar(id));
    }

    @Transactional
    public EventoDto crear(EventoDto dto) {
        validarFechas(dto);
        TipoEvento tipo = tipoRepo.findById(dto.idTipoEvento()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo De Evento Invalido: " + dto.idTipoEvento()));
        Evento e = instanciar(tipo);
        e.setTipo(tipo);
        aplicar(e, dto);
        e.setSenseiResponsable(resolverSensei(dto.idSenseiResponsable()));
        return toDto(repo.save(e));
    }

    @Transactional
    public EventoDto actualizar(Integer id, EventoDto dto) {
        Evento e = buscar(id);
        if (!e.getTipo().getId().equals(dto.idTipoEvento())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Se Puede Cambiar El Tipo De Un Evento");
        }
        validarFechas(dto);
        aplicar(e, dto);
        if (!Objects.equals(e.getSenseiResponsable().getId(), dto.idSenseiResponsable())) {
            e.setSenseiResponsable(resolverSensei(dto.idSenseiResponsable()));
        }
        return toDto(e);
    }

    @Transactional
    public void eliminar(Integer id) {
        Evento e = buscar(id);
        try {
            repo.delete(e);
            repo.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El Evento Tiene Registros Relacionados y No Se Puede Eliminar");
        }
    }

    private Evento buscar(Integer id){
        return repo.findById(id).orElseThrow(()->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento No Encontrado: "+ id));
    }

    private Evento instanciar(TipoEvento tipo) {
        return switch (tipo.getNombre()) {
            case "GENERAL" -> new Evento();
            case "TORNEO" -> new Torneo();
            case "EXAMEN" -> new Examen();
            case "CERTIFICACION" -> new Certificacion();
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Tipo de evento no soportado: " + tipo.getNombre());
        };
    }

    private void aplicar(Evento e, EventoDto dto) {
        e.setNombre(dto.nombre());
        e.setFechaInicio(dto.fechaInicio());
        e.setFechaFin(dto.fechaFin());
        e.setLugar(dto.lugar());
        e.setDescripcion(dto.descripcion());
        e.setCosto(dto.costo() != null ? dto.costo() : BigDecimal.ZERO);
        e.setEstado(dto.estado());
        e.setFechaPublicacion(dto.fechaPublicacion());
        e.setVisibilidad(dto.visibilidad());
        if (e instanceof Certificacion c) {
            if (dto.tipoCertificacion() == null || dto.tipoCertificacion().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "tipoCertificacion es obligatorio para las certificaciones");
            }
            c.setTipoCertificacion(dto.tipoCertificacion());
        }
    }

    private void validarFechas(EventoDto dto) {
        if (dto.fechaFin().isBefore(dto.fechaInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "fechaFin no puede ser anterior a fechaInicio");
        }
    }

    private Sensei resolverSensei(Integer idSensei) {
        Sensei sensei = senseiRepo.findById(idSensei).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sensei no existe: " + idSensei));
        if (!Boolean.TRUE.equals(sensei.getPersona().getActivo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El sensei está inactivo: " + idSensei);
        }
        return sensei;
    }

    private EventoDto toDto(Evento e) {
        String tipoCertificacion = e instanceof Certificacion c ? c.getTipoCertificacion() : null;
        return new EventoDto(
                e.getId(),
                e.getTipo().getId(),
                e.getTipo().getNombre(),
                e.getNombre(),
                e.getFechaInicio(),
                e.getFechaFin(),
                e.getLugar(),
                e.getDescripcion(),
                e.getCosto(),
                e.getEstado(),
                e.getFechaPublicacion(),
                e.getVisibilidad(),
                e.getSenseiResponsable().getId(),
                tipoCertificacion);
    }
}