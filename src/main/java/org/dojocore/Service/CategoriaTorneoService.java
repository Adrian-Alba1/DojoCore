package org.dojocore.Service;

import org.dojocore.Dto.CategoriaTorneoDto;
import org.dojocore.Model.CategoriaTorneo;
import org.dojocore.Model.Evento;
import org.dojocore.Model.Torneo;
import org.dojocore.Repository.CategoriaTorneoRepository;
import org.dojocore.Repository.EventoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoriaTorneoService {
    private final CategoriaTorneoRepository repo;
    private final EventoRepository eventoRepo;

    public CategoriaTorneoService(CategoriaTorneoRepository repo, EventoRepository eventoRepo) {
        this.repo = repo;
        this.eventoRepo = eventoRepo;
    }

    public List<CategoriaTorneoDto> listar(Integer idEvento) {
        Torneo torneo = buscarTorneo(idEvento);
        return repo.findByTorneoIdOrderById(torneo.getId()).stream().map(this::toDto).toList();
    }

    @Transactional
    public CategoriaTorneoDto crear(Integer idEvento, CategoriaTorneoDto dto) {
        Torneo torneo = buscarTorneo(idEvento);
        String tipo = dto.tipo().trim();
        if (repo.existsByTorneoIdAndTipoIgnoreCase(idEvento, tipo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El torneo ya tiene una categoría '" + tipo + "'");
        }
        CategoriaTorneo c = new CategoriaTorneo();
        c.setTorneo(torneo);
        c.setTipo(tipo);
        return toDto(repo.save(c));
    }

   // Si la categoría ya tiene participaciones no se puede borrar (409).
    @Transactional
    public void eliminar(Integer id) {
        CategoriaTorneo c = repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada: " + id));
        try {
            repo.delete(c);
            repo.flush();
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La categoría tiene participaciones registradas y no se puede eliminar");
        }
    }

    private Torneo buscarTorneo(Integer idEvento) {
        Evento e = eventoRepo.findById(idEvento).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento no encontrado: " + idEvento));
        if (e instanceof Torneo torneo) {
            return torneo;
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El evento " + idEvento + " no es un torneo");
    }

    private CategoriaTorneoDto toDto(CategoriaTorneo c) {
        return new CategoriaTorneoDto(c.getId(), c.getTorneo().getId(), c.getTipo());
    }
}