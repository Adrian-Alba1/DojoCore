package org.dojocore.Controller;

import jakarta.validation.Valid;
import org.dojocore.Dto.CategoriaTorneoDto;
import org.dojocore.Service.CategoriaTorneoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoriaTorneoController {

    private final CategoriaTorneoService service;

    public CategoriaTorneoController(CategoriaTorneoService service){
        this.service = service;
    }

    @GetMapping("/eventos/{idEvento}/categorias")
    public List<CategoriaTorneoDto> listar(@PathVariable Integer idEvento){
        return  service.listar(idEvento);
    }

    @PostMapping("/eventos/{idEvento}/categorias")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaTorneoDto crear(@PathVariable Integer idEvento, @Valid @RequestBody CategoriaTorneoDto dto){
        return service.crear(idEvento, dto);
    }

    @DeleteMapping("/categorias/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id){
        service.eliminar(id);
    }
}
