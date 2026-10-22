package org.dojocore.Controller;

import jakarta.validation.Valid;
import org.dojocore.Dto.ProductoDto;
import org.dojocore.Service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;

    public ProductoController(ProductoService service){
        this.service = service;
    }

    @GetMapping
    public List<ProductoDto> listar(@RequestParam(defaultValue = "false") boolean incluirInactivos) {
        return service.listar(incluirInactivos);
    }

    @GetMapping("/{id}")
    public ProductoDto obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoDto crear(@Valid @RequestBody ProductoDto dto) {
        return service.crear(dto);
    }

    @PutMapping("/{id}")
    public ProductoDto actualizar(@PathVariable Integer id, @Valid @RequestBody ProductoDto dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        service.eliminar(id);
    }
}
