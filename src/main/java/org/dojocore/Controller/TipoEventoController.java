package org.dojocore.Controller;

import org.dojocore.Dto.TipoEventoDto;
import org.dojocore.Service.TipoEventoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-evento")
public class TipoEventoController {

    private final TipoEventoService service;

    public TipoEventoController(TipoEventoService service){
        this.service = service;
    }

    @GetMapping
    public List<TipoEventoDto> listar(){
        return service.listar();
    }
}
