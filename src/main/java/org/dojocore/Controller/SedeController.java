package org.dojocore.Controller;

import jakarta.validation.Valid;
import org.dojocore.Dto.SedeDto;
import org.dojocore.Service.SedeService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sedes")
public class SedeController {

    private final SedeService service;

    public SedeController(SedeService service){
        this.service = service;
    }

    @GetMapping
    public List<SedeDto> listar(@RequestParam(defaultValue = "false") boolean incluirInactivos){
        return service.listar(incluirInactivos);
    }

    @GetMapping("/{id}")
    public SedeDto obtener(@PathVariable Integer id){
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SedeDto crear(@Valid @RequestBody SedeDto dto){
        return service.crear(dto);
    }

    @PutMapping("/{id}")
    public SedeDto actualizar(@PathVariable Integer id, @Valid @RequestBody SedeDto dto){
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id){
        service.eliminar(id);
    }
}
