package org.dojocore.Controller;

import jakarta.validation.Valid;
import org.dojocore.Dto.EventoDto;
import org.dojocore.Service.EventoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    private final EventoService service;

    public EventoController(EventoService service){
        this.service = service;
    }

    //GET /api/eventos o GET /api/eventos?tipo=TORNEO
    @GetMapping
    public List<EventoDto> listar(@RequestParam(required = false) String tipo) {
        return service.listar(tipo);
    }

    @GetMapping("/{id")
    public EventoDto obtener(@PathVariable Integer id){
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventoDto crear(@Valid @RequestBody EventoDto dto){
        return service.crear(dto);
    }

    @PutMapping("/{id}")
    public EventoDto actualizar(@PathVariable Integer id, @Valid @RequestBody EventoDto dto){
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id){
        service.eliminar(id);
    }

}
