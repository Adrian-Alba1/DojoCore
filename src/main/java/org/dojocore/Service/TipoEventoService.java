package org.dojocore.Service;

import jakarta.persistence.Entity;
import org.dojocore.Dto.TipoEventoDto;
import org.dojocore.Model.TipoEvento;
import org.dojocore.Repository.TipoEventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TipoEventoService {

    private final TipoEventoRepository repo;

    public TipoEventoService(TipoEventoRepository repo){
        this.repo =repo;
    }

    public List<TipoEventoDto> listar(){
        return repo.findAll().stream().map(t -> new TipoEventoDto(t.getId(), t.getNombre())).toList();
    }
}
