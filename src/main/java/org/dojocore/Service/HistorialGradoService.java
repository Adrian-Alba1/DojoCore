package org.dojocore.Service;

import org.dojocore.Dto.HistorialGradoDto;
import org.dojocore.Model.HistorialGrado;
import org.dojocore.Repository.EventoRepository;
import org.dojocore.Repository.HistorialGradoRepository;
import org.dojocore.Repository.PersonaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class HistorialGradoService {

    private final HistorialGradoRepository repo;
    private final PersonaRepository personaRepo;
    private final EventoRepository eventoRepo;

    public HistorialGradoService(HistorialGradoRepository repo, PersonaRepository personaRepo,
                                 EventoRepository eventoRepo){
        this.repo = repo;
        this.personaRepo = personaRepo;
        this.eventoRepo = eventoRepo;
    }

    public List<HistorialGradoDto> listar(Integer idPersona){
        if(!personaRepo.existsById(idPersona)){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Persona No Encontrada: "+ idPersona);
        }
        return repo.findByPersonaIdOrderByFechaDescIdDesc(idPersona).stream().map(this::toDto).toList();
    }

    private HistorialGradoDto toDto(HistorialGrado h){
        String examen = eventoRepo.findById(h.getIdExamen()).map(e -> e.getNombre()).orElse(null);
        return new HistorialGradoDto(h.getId(), h.getPersona().getId(),
                h.getGradoAnterior().getId(), h.getGradoAnterior().getNombre(),
                h.getGradoNuevo().getId(), h.getGradoNuevo().getNombre(),
                h.getIdExamen(), examen, h.getFecha());
    }
}
