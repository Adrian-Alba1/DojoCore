package org.dojocore.Service;

import org.dojocore.Dto.AlumnoDto;
import org.dojocore.Dto.HorarioDto;
import org.dojocore.Dto.SenseiDto;
import org.dojocore.Model.Alumno;
import org.dojocore.Model.Horario;
import org.dojocore.Model.Sensei;
import org.dojocore.Repository.AlumnoRepository;
import org.dojocore.Repository.HorarioRepository;
import org.dojocore.Repository.SenseiRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class AsignacionHorarioService {

    private final AlumnoRepository alumnoRepo;
    private final SenseiRepository senseiRepo;
    private final HorarioRepository horarioRepo;
    private final HorarioService horarioService;
    private final AlumnoService alumnoService;
    private final SenseiService senseiService;

    public AsignacionHorarioService(AlumnoRepository alumnoRepo, SenseiRepository senseiRepo,
                                    HorarioRepository horarioRepo, HorarioService horarioService,
                                    AlumnoService alumnoService, SenseiService senseiService){

        this.alumnoRepo = alumnoRepo;
        this.alumnoService = alumnoService;
        this.senseiRepo = senseiRepo;
        this.senseiService = senseiService;
        this.horarioRepo = horarioRepo;
        this.horarioService = horarioService;
    }

    public List<HorarioDto> horariosDeAlumno(Integer idAlumno, boolean incluirInactivos){
        return mapear(buscarAlumno(idAlumno).getHorarios(), incluirInactivos);
    }

    public List<AlumnoDto> alumnosDeHorario(Integer idHorario, boolean incluirInactivos){
        buscarHorario(idHorario);
        List<Alumno> alumnos = incluirInactivos
                ? alumnoRepo.findByHorariosIdOrderById(idHorario)
                : alumnoRepo.findByHorariosIdAndPersonaActivoOrderById(idHorario, true);
        return alumnos.stream().map(alumnoService::toDto).toList();
    }

    @Transactional
    public HorarioDto asignarAlumno(Integer idAlumno, Integer idHorario) {
        Alumno alumno = buscarAlumno(idAlumno);
        if (!Boolean.TRUE.equals(alumno.getPersona().getActivo())) {
            throw invalida("El alumno está inactivo: " + idAlumno);
        }
        Horario horario = buscarHorarioActivo(idHorario);
        if (!alumno.getHorarios().add(horario)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El alumno ya está asignado a ese horario");
        }
        return horarioService.toDto(horario);
    }

    @Transactional
    public void quitarAlumno(Integer idAlumno, Integer idHorario) {
        Alumno alumno = buscarAlumno(idAlumno);
        if (!alumno.getHorarios().removeIf(h -> h.getId().equals(idHorario))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "El alumno no está asignado al horario " + idHorario);
        }
    }

    // Horarios de un sensei
    public List<HorarioDto> horariosDeSensei(Integer idSensei, boolean incluirInactivos) {
        return mapear(buscarSensei(idSensei).getHorarios(), incluirInactivos);
    }

    //Senseis de un horario
    public List<SenseiDto> senseisDeHorario(Integer idHorario, boolean incluirInactivos) {
        buscarHorario(idHorario);
        List<Sensei> senseis = incluirInactivos
                ? senseiRepo.findByHorariosIdOrderById(idHorario)
                : senseiRepo.findByHorariosIdAndPersonaActivoOrderById(idHorario, true);
        return senseis.stream().map(senseiService::toDto).toList();
    }

    @Transactional
    public HorarioDto asignarSensei(Integer idSensei, Integer idHorario) {
        Sensei sensei = buscarSensei(idSensei);
        if (!Boolean.TRUE.equals(sensei.getPersona().getActivo())) {
            throw invalida("El sensei está inactivo: " + idSensei);
        }
        Horario horario = buscarHorarioActivo(idHorario);
        if (!sensei.getHorarios().add(horario)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El sensei ya está asignado a ese horario");
        }
        return horarioService.toDto(horario);
    }

    @Transactional
    public void quitarSensei(Integer idSensei, Integer idHorario) {
        Sensei sensei = buscarSensei(idSensei);
        if (!sensei.getHorarios().removeIf(h -> h.getId().equals(idHorario))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "El sensei no está asignado al horario " + idHorario);
        }
    }

    private List<HorarioDto> mapear(Set<Horario> horarios, boolean incluirInactivos) {
        return horarios.stream()
                .filter(h -> incluirInactivos || Boolean.TRUE.equals(h.isActivo()))
                .sorted(Comparator.comparing(Horario::getId))
                .map(horarioService::toDto)
                .toList();
    }

    private Alumno buscarAlumno(Integer id) {
        return alumnoRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado: " + id));
    }

    private Sensei buscarSensei(Integer id) {
        return senseiRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Sensei no encontrado: " + id));
    }

    private Horario buscarHorario(Integer id) {
        return horarioRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Horario no encontrado: " + id));
    }

    // Solo se puede asignar a horarios activos
    private Horario buscarHorarioActivo(Integer id) {
        Horario h = buscarHorario(id);
        if (!Boolean.TRUE.equals(h.isActivo())) {
            throw invalida("El horario está inactivo: " + id);
        }
        return h;
    }

    private static ResponseStatusException invalida(String mensaje) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }
}
