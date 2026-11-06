package org.dojocore.Controller;

import jakarta.validation.Valid;
import org.dojocore.Dto.ArchivoEventoDto;
import org.dojocore.Security.Usuario;
import org.dojocore.Service.ArchivoEventoService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ArchivoEventoControler {

    private final ArchivoEventoService service;

    public ArchivoEventoControler(ArchivoEventoService service){
        this.service = service;
    }

    @GetMapping("/eventos/{idEvento}/archivos")
    public List<ArchivoEventoDto> listar(@PathVariable Integer idEvento, Authentication auth){
        return service.listar(Usuario.de(auth), idEvento);
    }

    @PostMapping("/eventos/{idEvento}/archivos")
    @ResponseStatus(HttpStatus.CREATED)
    public ArchivoEventoDto crear(@PathVariable Integer idEvento, @Valid @RequestBody ArchivoEventoDto dto){
        return service.crear(idEvento, dto);
    }

    @GetMapping("/archivos/{id}")
    public ArchivoEventoDto obtener(@PathVariable Integer id, Authentication auth){
        return service.obtener(Usuario.de(auth), id);
    }

    @PutMapping("/archivos/{id}")
    public ArchivoEventoDto actualizar(@PathVariable Integer id, @Valid @RequestBody ArchivoEventoDto dto){
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/archivos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id){
        service.eliminar(id);
    }
}
