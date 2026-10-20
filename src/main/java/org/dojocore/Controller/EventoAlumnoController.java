package org.dojocore.Controller;

import org.dojocore.Dto.AlumnoDto;
import org.dojocore.Dto.EventoDto;
import org.dojocore.Service.EventoAlumnoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EventoAlumnoController {

    private final EventoAlumnoService service;

    public EventoAlumnoController(EventoAlumnoService service) {
        this.service = service;
    }

    @GetMapping("/eventos/{idEvento}/alumnos")
    public List<AlumnoDto> alumnosDeEvento(@PathVariable Integer idEvento,
                                           @RequestParam(defaultValue = "false") boolean incluirInactivos) {
        return service.alumnosDeEvento(idEvento, incluirInactivos);
    }

    @PostMapping("/eventos/{idEvento}/alumnos/{idAlumno}")
    @ResponseStatus(HttpStatus.CREATED)
    public AlumnoDto agregarAlumno(@PathVariable Integer idEvento, @PathVariable Integer idAlumno) {
        return service.agregarAlumno(idEvento, idAlumno);
    }

    @DeleteMapping("/eventos/{idEvento}/alumnos/{idAlumno}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void quitarAlumno(@PathVariable Integer idEvento, @PathVariable Integer idAlumno) {
        service.quitarAlumno(idEvento, idAlumno);
    }

    @GetMapping("/alumnos/{idAlumno}/eventos")
    public List<EventoDto> eventosDeAlumno(@PathVariable Integer idAlumno) {
        return service.eventosDeAlumno(idAlumno);
    }

    @GetMapping("/alumnos/{idAlumno}/eventos-disponibles")
    public List<EventoDto> eventosDisponibles(@PathVariable Integer idAlumno) {
        return service.eventosDisponibles(idAlumno);
    }
}
