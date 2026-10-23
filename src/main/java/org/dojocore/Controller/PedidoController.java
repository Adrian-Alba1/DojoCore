package org.dojocore.Controller;

import jakarta.validation.Valid;
import org.dojocore.Dto.CambioEstadoPedidoDto;
import org.dojocore.Dto.PedidoCrearDto;
import org.dojocore.Dto.PedidoDto;
import org.dojocore.Dto.PedidoItemsDto;
import org.dojocore.Service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service){
        this.service = service;
    }

    @GetMapping
    public List<PedidoDto> listar(@RequestParam(required = false)Integer idPersona,
                                  @RequestParam(required = false) String estado){
        return service.listar(idPersona, estado);
    }

    @GetMapping("/{id}")
    public PedidoDto obtener(@PathVariable Integer id){
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoDto crear(@Valid @RequestBody PedidoCrearDto dto){
        return service.crear(dto);
    }

    @PutMapping("/{id}/items")
    public PedidoDto actualizarItems(@PathVariable Integer id, @Valid @RequestBody PedidoItemsDto dto){
        return service.actualizarItems(id, dto);
    }

    @PatchMapping("/{id}/estado")
    public PedidoDto cambiarEstado(@PathVariable Integer id, @Valid @RequestBody CambioEstadoPedidoDto dto){
        return service.cambiarEstado(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id){
        service.eliminar(id);
    }
}
