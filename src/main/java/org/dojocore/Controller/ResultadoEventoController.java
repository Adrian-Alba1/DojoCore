package org.dojocore.Controller;

import org.dojocore.Dto.ResultadoEventoDto;
import org.dojocore.Model.ResultadoEvento;
import org.dojocore.Service.ResultadoEventoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/resultados")
public class ResultadoEventoController {

    private final ResultadoEventoService service;

    public ResultadoEventoController(ResultadoEventoService service){
        this.service = service;
    }

    @GetMapping
    public List<ResultadoEventoDto> listar(@RequestParam(required = false) String tipo){
        return service.listar(tipo);
    }
}
