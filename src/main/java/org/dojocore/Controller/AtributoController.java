package org.dojocore.Controller;

import jakarta.validation.Valid;
import org.dojocore.Dto.AtributoDto;
import org.dojocore.Dto.ValorAtributoDto;
import org.dojocore.Service.AtributoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AtributoController {

    private final AtributoService service;

    public AtributoController(AtributoService service){
        this.service = service;
    }

    @GetMapping("/atributos")
    public List<AtributoDto> listar(){
        return service.listar();
    }

    @GetMapping("/atributos/{id}")
    public AtributoDto obtener(@PathVariable Integer id){
        return service.obtener(id);
    }

    @PostMapping("/atributos")
    @ResponseStatus(HttpStatus.CREATED)
    public AtributoDto crear(@Valid @RequestBody AtributoDto dto){
        return service.crear(dto);
    }

    @PutMapping("/atributos/{id}")
    public AtributoDto actualizar(@PathVariable Integer id, @Valid @RequestBody AtributoDto dto){
        return  service.actualizar(id, dto);
    }

    @DeleteMapping("/atributos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id){
        service.eliminar(id);
    }

    @GetMapping("/atributos/{idAtributo}/valores")
    public List<ValorAtributoDto> listarValores(@PathVariable Integer idAtributo){
        return service.listarValores(idAtributo);
    }

    @PostMapping("/atributos/{idAtributo}/valores")
    @ResponseStatus(HttpStatus.CREATED)
    public ValorAtributoDto crearValor(@PathVariable Integer idAtributo, @Valid @RequestBody ValorAtributoDto dto){
        return service.crearValor(idAtributo, dto);
    }

    @PutMapping("/valores/{idValor}")
    public ValorAtributoDto actualizarValor(@PathVariable Integer idValor, @Valid @RequestBody ValorAtributoDto dto){
        return service.actualizarValor(idValor, dto);
    }

    @DeleteMapping("/valores/{idValor}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarValor(@PathVariable Integer idValor){
        service.eliminarValor(idValor);
    }
}
