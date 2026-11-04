package org.dojocore.Controller;

import jakarta.validation.Valid;
import org.dojocore.Dto.ContactoDto;
import org.dojocore.Service.ContactoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ContactoController {

    private final ContactoService service;

    public ContactoController(ContactoService service){
        this.service = service;
    }

    @GetMapping("/personas/{idPersonas}/contactos")
    public List<ContactoDto> listar(@PathVariable Integer idPersona){
        return service.listar(idPersona);
    }

    @PostMapping("/personas/{idPersonas}/contactos")
    @ResponseStatus(HttpStatus.CREATED)
    public ContactoDto crear(@PathVariable Integer idPersona, @Valid @RequestBody ContactoDto dto){
        return service.crear(idPersona, dto);
    }

    @DeleteMapping("/personas/{idPersonas}/contactos/{idContacto}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void quitar(@PathVariable Integer idPersona, @PathVariable Integer idContacto){
        service.quitar(idPersona, idContacto);
    }

    @PutMapping("/contactos/{idContacto}")
    public ContactoDto actualizar(@PathVariable Integer idContacto, @Valid @RequestBody ContactoDto dto){
        return service.actualizar(idContacto, dto);
    }

    @PutMapping("/alumnos/{idAlumno}/contactos/{idContacto}/tutor")
    public ContactoDto marcarTutor(@PathVariable Integer idAlumno, @PathVariable Integer idContacto){
        return service.marcarTutor(idAlumno, idContacto);
    }

    @DeleteMapping("/alumnos/{idAlumno}/contactos/{idContacto}/tutor")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void quitarTutor(@PathVariable Integer idAlumno, @PathVariable Integer idContacto){
        service.quitarTutor(idAlumno, idContacto);
    }
}
