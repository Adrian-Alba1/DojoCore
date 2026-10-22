package org.dojocore.Controller;

import jakarta.validation.Valid;
import org.dojocore.Dto.InventarioDto;
import org.dojocore.Dto.MovimientoInventarioDto;
import org.dojocore.Dto.MovimientoManualDto;
import org.dojocore.Service.InventarioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class InventarioController {

    private final InventarioService service;

    public InventarioController(InventarioService service){
        this.service = service;
    }

    @GetMapping("/inventario")
    public List<InventarioDto> listar(@RequestParam(defaultValue = "false") boolean incluirInactivos){
        return service.listar(incluirInactivos);
    }

    @GetMapping("/variantes/{idVariante}/inventario")
    public InventarioDto obtener(@PathVariable Integer idVariante){
        return service.obtener(idVariante);
    }

    @GetMapping("/variantes{idVariante}/movimientos")
    public List<MovimientoInventarioDto> movimientos(@PathVariable Integer idVariante){
        return service.movimientos(idVariante);
    }

    @PostMapping("/variantes/{idVariante}/movimientos")
    @ResponseStatus(HttpStatus.CREATED)
    public MovimientoInventarioDto registrar(@PathVariable Integer idVariante,
                                             @Valid @RequestBody MovimientoManualDto dto){
        return service.registrarMovimiento(idVariante, dto);
    }
}
