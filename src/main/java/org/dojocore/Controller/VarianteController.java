package org.dojocore.Controller;

import jakarta.validation.Valid;
import org.dojocore.Dto.VarianteDto;
import org.dojocore.Service.VarianteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class VarianteController {

    private final VarianteService service;

    public VarianteController(VarianteService service) {
        this.service = service;
    }

    @GetMapping("/variantes")
    public List<VarianteDto> listar(@RequestParam(required = false) Integer idProducto,
                                    @RequestParam(defaultValue = "false") boolean incluirInactivos) {
        return service.listar(idProducto, incluirInactivos);
    }

    @GetMapping("/productos/{idProducto}/variantes")
    public List<VarianteDto> listarDeProducto(@PathVariable Integer idProducto,
                                              @RequestParam(defaultValue = "false") boolean incluirInactivos) {
        return service.listar(idProducto, incluirInactivos);
    }

    @PostMapping("/productos/{idProducto}/variantes")
    @ResponseStatus(HttpStatus.CREATED)
    public VarianteDto crear(@PathVariable Integer idProducto, @Valid @RequestBody VarianteDto dto) {
        return service.crear(idProducto, dto);
    }

    @GetMapping("/variantes/{id}")
    public VarianteDto obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PutMapping("/variantes/{id}")
    public VarianteDto actualizar(@PathVariable Integer id, @Valid @RequestBody VarianteDto dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/variantes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        service.eliminar(id);
    }
}
