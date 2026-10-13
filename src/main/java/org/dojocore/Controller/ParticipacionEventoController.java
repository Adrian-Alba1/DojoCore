package org.dojocore.Controller;

import jakarta.validation.Valid;
import org.dojocore.Dto.ParticipacionEventoDto;
import org.dojocore.Service.ParticipacionEventoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participaciones")

public class ParticipacionEventoController {

    private final ParticipacionEventoService service;

    public ParticipacionEventoController(ParticipacionEventoService service){
        this.service = service;
    }

    // GET /api/participaciones?idEvento=4&idPersona=8 (ambos filtros son opcionales)
    @GetMapping
    public List<ParticipacionEventoDto> listar(@RequestParam(required = false) Integer idEvento,
                                               @RequestParam(required = false) Integer idPersona) {
        return service.listar(idEvento, idPersona);
    }

    @GetMapping("/{id}")
    public ParticipacionEventoDto obtener(@PathVariable Integer id){
        return service.obtener(id);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipacionEventoDto crear(@Valid @RequestBody ParticipacionEventoDto dto) {
        return service.crear(dto);
    }

    @PutMapping("/{id}")
    public ParticipacionEventoDto actualizar(@PathVariable Integer id,
                                             @Valid @RequestBody ParticipacionEventoDto dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        service.eliminar(id);
    }
}
