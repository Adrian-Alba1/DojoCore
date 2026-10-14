package org.dojocore.Controller;

import org.dojocore.Dto.DiaSemanaDto;
import org.dojocore.Service.DiaSemanaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dias")
public class DiaSemanaController {

    private final DiaSemanaService service;

    public DiaSemanaController(DiaSemanaService service){
        this.service = service;
    }

    @GetMapping
    public List<DiaSemanaDto> listar(){
        return service.listar();
    }
}
