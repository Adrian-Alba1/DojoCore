package org.dojocore.Controller;

import jakarta.validation.Valid;
import org.dojocore.Dto.SenseiDto;
import org.dojocore.Service.SenseiService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/senseis")
public class SenseiController {

    private final SenseiService service;

    public SenseiController(SenseiService service) {
        this.service = service;
    }

    @GetMapping
    public List<SenseiDto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public SenseiDto obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SenseiDto crear(@Valid @RequestBody SenseiDto dto) {
        return service.crear(dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        service.eliminar(id);
    }
}
