package org.dojocore.Controller;

import org.dojocore.Dto.HistorialGradoDto;
import org.dojocore.Service.HistorialGradoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/personas")
public class HistorialGradoController {

    private final HistorialGradoService service;

    public HistorialGradoController (HistorialGradoService service){
        this.service = service;
    }

    @GetMapping("/{idPersona}/historial-grados")
    public List<HistorialGradoDto> listar(@PathVariable Integer idPersona){
        return service.listar(idPersona);
    }
}

