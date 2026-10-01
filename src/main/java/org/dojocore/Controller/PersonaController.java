package org.dojocore.Controller;


import jakarta.validation.Valid;
import org.dojocore.Dto.PersonaDto;
import org.dojocore.Service.PersonaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    private final PersonaService service;

    public PersonaController(PersonaService service){
        this.service = service;
    }

    @GetMapping
    public List<PersonaDto> listar(){
        return service.listar();
    }

    @GetMapping("/{id}")
    public PersonaDto obtener (@PathVariable Integer id){
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonaDto crear(@Valid @RequestBody PersonaDto dto){
        return service.crear(dto);
    }

    @PutMapping("/{id}")
    public PersonaDto actuslizar(@PathVariable Integer id, @Valid @RequestBody PersonaDto dto){
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id){
        service.eliminar(id);
    }
}
