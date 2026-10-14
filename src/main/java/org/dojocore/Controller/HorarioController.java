package org.dojocore.Controller;

import jakarta.validation.Valid;
import org.dojocore.Dto.HorarioDto;
import org.dojocore.Service.HorarioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/horarios")
public class HorarioController {

    private final HorarioService service;

    public HorarioController(HorarioService service){
        this.service = service;
    }

    @GetMapping
    public List<HorarioDto> listar(@RequestParam(required = false)Integer idSede,
                                   @RequestParam(defaultValue = "false") boolean incluirInactivos){
        return service.listar(idSede, incluirInactivos);
    }

    @GetMapping("/{id}")
    public HorarioDto obtener(@PathVariable Integer id){
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HorarioDto crear(@Valid @RequestBody HorarioDto dto){
        return service.crear(dto);
    }

    @PutMapping("/{id}")
    public HorarioDto actualizar(@PathVariable Integer id, @Valid @RequestBody HorarioDto dto){
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id){
        service.eliminar(id);
    }
}
