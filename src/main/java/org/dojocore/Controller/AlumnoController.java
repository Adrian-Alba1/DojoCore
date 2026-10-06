package org.dojocore.Controller;

import jakarta.validation.Valid;
import org.dojocore.Dto.AlumnoDto;
import org.dojocore.Service.AlumnoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumnos")
public class AlumnoController {
    private final AlumnoService service;

    public AlumnoController(AlumnoService service) {
        this.service = service;
    }

    @GetMapping
    public List<AlumnoDto> listar(@RequestParam(defaultValue = "false") boolean incluirInactivos) {
        return service.listar(incluirInactivos);
    }

    @GetMapping("/{id}")
    public AlumnoDto obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlumnoDto crear(@Valid @RequestBody AlumnoDto dto) {
        return service.crear(dto);
    }

    @PutMapping("/{id}")
    public AlumnoDto actualizar(@PathVariable Integer id, @Valid @RequestBody AlumnoDto dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        service.eliminar(id);
    }
}
