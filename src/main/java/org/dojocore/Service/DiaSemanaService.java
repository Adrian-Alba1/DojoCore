package org.dojocore.Service;

import org.dojocore.Dto.DiaSemanaDto;
import org.dojocore.Repository.DiaSemanaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
public class DiaSemanaService {

    private final DiaSemanaRepository repo;

    public DiaSemanaService(DiaSemanaRepository repo){
        this.repo = repo;
    }

    public List<DiaSemanaDto> listar(){
        return repo.findAll(Sort.by("id")).stream()
                .map(d -> new DiaSemanaDto(d.getId(), d.getNombre()))
                .toList();
    }
}
