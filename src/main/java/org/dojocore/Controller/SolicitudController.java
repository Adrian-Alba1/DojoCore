package org.dojocore.Controller;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.dojocore.Dto.*;
import org.dojocore.Security.Usuario;
import org.dojocore.Service.SolicitudCorreccionGradoService;
import org.dojocore.Service.SolicitudHorarioService;
import org.dojocore.Service.SolicitudParticipacionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

    private final SolicitudHorarioService horarios;
    private final SolicitudParticipacionService participaciones;
    private final SolicitudCorreccionGradoService correcciones;

    public SolicitudController(SolicitudHorarioService horarios, SolicitudParticipacionService participaciones,
                               SolicitudCorreccionGradoService correcciones){
        this.horarios = horarios;
        this.participaciones = participaciones;
        this.correcciones = correcciones;
    }

    @GetMapping("/horarios")
    public List<SolicitudHorarioDto> listarHorarios(@RequestParam(required = false) Integer idAlumno,
                                                    @RequestParam(required = false) String estado,
                                                    Authentication auth){
        return horarios.listar(Usuario.de(auth), idAlumno, estado);
    }

    @GetMapping("/horarios/{id}")
    public SolicitudHorarioDto obtenerHorario(@PathVariable Integer id, Authentication auth){
        return horarios.obtener(Usuario.de(auth), id);
    }

    @PostMapping("horarios")
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudHorarioDto crearHorario(@Valid @RequestBody SolicitudHorarioCrearDto dto, Authentication auth){
        return horarios.crear(Usuario.de(auth), dto);
    }

    @PatchMapping("/horarios/{id}/resolucion")
    public SolicitudHorarioDto resolverHorario(@PathVariable Integer id, @Valid @RequestBody ResolucionDto dto,
                                               Authentication auth) {
        return horarios.resolver(Usuario.de(auth), id, dto);
    }

    @DeleteMapping("/horarios/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarHorario(@PathVariable Integer id, Authentication auth) {
        horarios.eliminar(Usuario.de(auth), id);
    }

    @GetMapping("/participaciones")
    public List<SolicitudParticipacionDto> listarParticipaciones(@RequestParam(required = false) Integer idPersona,
                                                                 @RequestParam(required = false) String estado,
                                                                 Authentication auth) {
        return participaciones.listar(Usuario.de(auth), idPersona, estado);
    }

    @GetMapping("/participaciones/{id}")
    public SolicitudParticipacionDto obtenerParticipacion(@PathVariable Integer id, Authentication auth) {
        return participaciones.obtener(Usuario.de(auth), id);
    }

    @PostMapping("/participaciones")
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudParticipacionDto crearParticipacion(@Valid @RequestBody SolicitudParticipacionCrearDto dto,
                                                        Authentication auth) {
        return participaciones.crear(Usuario.de(auth), dto);
    }

    @PatchMapping("/participaciones/{id}/resolucion")
    public SolicitudParticipacionDto resolverParticipacion(@PathVariable Integer id,
                                                           @Valid @RequestBody ResolucionDto dto,
                                                           Authentication auth) {
        return participaciones.resolver(Usuario.de(auth), id, dto);
    }

    @DeleteMapping("/participaciones/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarParticipacion(@PathVariable Integer id, Authentication auth) {
        participaciones.eliminar(Usuario.de(auth), id);
    }

    @GetMapping("/correcciones-grado")
    public List<SolicitudCorreccionGradoDto> listarCorrecciones(@RequestParam(required = false) Integer idAlumno,
                                                                @RequestParam(required = false) String estado,
                                                                Authentication auth) {
        return correcciones.listar(Usuario.de(auth), idAlumno, estado);
    }

    @GetMapping("/correcciones-grado/{id}")
    public SolicitudCorreccionGradoDto obtenerCorreccion(@PathVariable Integer id, Authentication auth) {
        return correcciones.obtener(Usuario.de(auth), id);
    }

    @PostMapping("/correcciones-grado")
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudCorreccionGradoDto crearCorreccion(@Valid @RequestBody SolicitudCorreccionGradoCrearDto dto,
                                                       Authentication auth) {
        return correcciones.crear(Usuario.de(auth), dto);
    }

    @PatchMapping("/correcciones-grado/{id}/resolucion")
    public SolicitudCorreccionGradoDto resolverCorreccion(@PathVariable Integer id,
                                                          @Valid @RequestBody ResolucionDto dto,
                                                          Authentication auth) {
        return correcciones.resolver(Usuario.de(auth), id, dto);
    }

    @DeleteMapping("/correcciones-grado/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarCorreccion(@PathVariable Integer id, Authentication auth) {
        correcciones.eliminar(Usuario.de(auth), id);
    }
}
