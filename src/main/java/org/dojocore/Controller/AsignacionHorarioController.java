package org.dojocore.Controller;


import org.dojocore.Dto.AlumnoDto;
import org.dojocore.Dto.HorarioDto;
import org.dojocore.Dto.SenseiDto;
import org.dojocore.Model.Alumno;
import org.dojocore.Service.AsignacionHorarioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AsignacionHorarioController {

    private final AsignacionHorarioService service;

    public AsignacionHorarioController(AsignacionHorarioService service){
        this.service = service;
    }

    @GetMapping("/alumnos/{idAlumno}/horarios")
    public List<HorarioDto> horariosDeAlumno(@PathVariable Integer idAlumno,
                                             @RequestParam(defaultValue = "false") boolean incluirInactivos){
        return service.horariosDeAlumno(idAlumno, incluirInactivos);
    }

    @PostMapping("/alumnos/{idAlumno}/horarios/{idHorario}")
    @ResponseStatus(HttpStatus.CREATED)
    public HorarioDto asignarAlumno(@PathVariable Integer idAlumno, @PathVariable Integer idHorario){
        return service.asignarAlumno(idAlumno, idHorario);
    }

    @DeleteMapping("/alumnos/{idAlumno}/horarios/{idHorario}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void quitarAlumno(@PathVariable Integer idAlumno, @PathVariable Integer idHorario){
        service.quitarAlumno(idAlumno, idHorario);
    }

    @GetMapping("/horarios/{idHorario}/alumnos")
    public List<AlumnoDto> alumnosDeHorario(@PathVariable Integer idHorario,
                                            @RequestParam(defaultValue = "false") boolean incluirInactivos){
        return service.alumnosDeHorario(idHorario, incluirInactivos);
    }

    @GetMapping("/senseis/{idSensei}/horarios")
    public List<HorarioDto> horariosDeSensei(@PathVariable Integer idSinsei,
                                             @RequestParam(defaultValue = "false") boolean incluirInactivos){
        return service.horariosDeSensei(idSinsei, incluirInactivos);
    }

    @PostMapping("/senseis/{idSensei}/horarios/{idHorario}")
    @ResponseStatus(HttpStatus.CREATED)
    public HorarioDto asignarSensei(@PathVariable Integer idSensei, @PathVariable Integer idHorario){
        return service.asignarSensei(idSensei, idHorario);
    }

    @DeleteMapping("/senseis/{idSensei}/horarios/{idHorario}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void quitarSensei(@PathVariable Integer idSensei, @PathVariable Integer idHorario){
        service.quitarSensei(idSensei, idHorario);
    }

    @GetMapping("/horarios/{idHorario}/senseis")
    public List<SenseiDto> senseisDeHorario(@PathVariable Integer idHorario,
                                            @RequestParam(defaultValue = "false") boolean incluirInactivos){
        return service.senseisDeHorario(idHorario, incluirInactivos);
    }
}
