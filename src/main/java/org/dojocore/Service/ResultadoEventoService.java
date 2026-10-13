package org.dojocore.Service;

import org.dojocore.Dto.ResultadoEventoDto;
import org.dojocore.Model.ResultadoEvento;
import org.dojocore.Repository.ResultadoEventoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class ResultadoEventoService {

    private final ResultadoEventoRepository repo;

    public ResultadoEventoService(ResultadoEventoRepository repo){
        this.repo =repo;
    }

    public List<ResultadoEventoDto> listar(String tipo){
        List<ResultadoEvento> resultados = (tipo == null || tipo.isBlank())
            ? repo.findAll(Sort.by("id"))
            : repo.findByTiposEventoNombreOrderById(tipo.trim().toUpperCase(Locale.ROOT));
        return resultados.stream()
            .map(r -> new ResultadoEventoDto(r.getId(), r.getNombre()))
            .toList();
    }
}
