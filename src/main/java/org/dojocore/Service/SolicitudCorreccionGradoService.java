package org.dojocore.Service;

import org.dojocore.Dto.ResolucionDto;
import org.dojocore.Dto.SolicitudCorreccionGradoCrearDto;
import org.dojocore.Dto.SolicitudCorreccionGradoDto;
import org.dojocore.Model.Alumno;
import org.dojocore.Model.Grado;
import org.dojocore.Model.SolicitudCorreccionGrado;
import org.dojocore.Repository.AlumnoRepository;
import org.dojocore.Repository.GradoRepository;
import org.dojocore.Repository.SolicitudCorreccionGradoRepository;
import org.dojocore.Security.Usuario;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SolicitudCorreccionGradoService {

    private final SolicitudCorreccionGradoRepository repo;
    private final AlumnoRepository alumnoRepo;
    private final GradoRepository gradoRepo;

    public SolicitudCorreccionGradoService(SolicitudCorreccionGradoRepository repo, AlumnoRepository alumnoRepo,
                                           GradoRepository gradoRepo){
        this.repo = repo;
        this.alumnoRepo = alumnoRepo;
        this.gradoRepo =  gradoRepo;
    }

    public List<SolicitudCorreccionGradoDto> listar(Usuario usuario, Integer idAlumno, String estado){
        Integer filtro = usuario.personal() ? idAlumno : usuario.idPersona();
        String est = EstadoSolicitud.filtro(estado);
        List<SolicitudCorreccionGrado> lista;
        if(filtro != null && est != null){
            lista = repo.findByAlumnoIdAndEstadoOrderById(filtro, est);
        } else if (filtro != null) {
            lista = repo.findByAlumnoIdOrderById(filtro);
        } else if (est != null) {
            lista = repo.findByEstadoOrderById(est);
        } else {
            lista = repo.findAll(Sort.by("id"));
        }
        return lista.stream().map(this::toDto).toList();
    }

    public SolicitudCorreccionGradoDto obtener(Usuario usuario, Integer id){
        SolicitudCorreccionGrado s = buscar(id);
        usuario.exigirAcceso(s.getAlumno().getId());
        return toDto(s);
    }

    @Transactional
    public SolicitudCorreccionGradoDto crear(Usuario usuario, SolicitudCorreccionGradoCrearDto dto){
        Integer idAlumno = usuario.actuarComo(dto.idAlumno());
        Alumno alumno = alumnoRepo.findById(idAlumno).orElseThrow(()->
                invalida("La Persona "+ idAlumno + " No Es Alumno"));
        if (!Boolean.TRUE.equals(alumno.getPersona().getActivo())){
            throw invalida("El Alumno Esta Inactivo: " + idAlumno);
        }
        Grado grado = gradoRepo.findById(dto.idGradoSolicitado()).orElseThrow(()->
                invalida("Grado No Existe: " + dto.idGradoSolicitado()));
        Grado actual = alumno.getPersona().getGradoActual();
        if (actual != null && actual.getId().equals(grado.getId())){
            throw invalida("Ese Ya Es El Grado Actual Del Alumno");
        }
        if (repo.existsByAlumnoIdAndEstado(idAlumno, EstadoSolicitud.PENDIENTE)){
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El Alumno Ya Tiene Una Solicitud De Correccion Pendiente");
        }
        SolicitudCorreccionGrado s = new SolicitudCorreccionGrado();
        s.setAlumno(alumno);
        s.setGradoSolicitado(grado);
        s.setFechaSolicitud(LocalDateTime.now());
        s.setEstado(EstadoSolicitud.PENDIENTE);
        return toDto(repo.save(s));
    }

    @Transactional
    public SolicitudCorreccionGradoDto resolver(Usuario usuario, Integer id, ResolucionDto dto) {
        usuario.exigirPersonal();
        SolicitudCorreccionGrado s = buscar(id);
        String nuevo = EstadoSolicitud.resolucion(dto.estado());
        EstadoSolicitud.exigirPendiente(s.getEstado());
        if (EstadoSolicitud.APROBADA.equals(nuevo)) {
            if (!Boolean.TRUE.equals(s.getAlumno().getPersona().getActivo())) {
                throw invalida("El alumno está inactivo: " + s.getAlumno().getId());
            }
            s.getAlumno().getPersona().setGradoActual(s.getGradoSolicitado());
        }
        s.setEstado(nuevo);
        return toDto(s);
    }

    @Transactional
    public void eliminar(Usuario usuario, Integer id) {
        SolicitudCorreccionGrado s = buscar(id);
        usuario.exigirAcceso(s.getAlumno().getId());
        EstadoSolicitud.exigirPendiente(s.getEstado());
        repo.delete(s);
    }

    private SolicitudCorreccionGrado buscar(Integer id) {
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada: " + id));
    }

    private static ResponseStatusException invalida(String mensaje) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }

    private SolicitudCorreccionGradoDto toDto(SolicitudCorreccionGrado s) {
        return new SolicitudCorreccionGradoDto(s.getId(), s.getAlumno().getId(),
                s.getAlumno().getPersona().getNombre(), s.getGradoSolicitado().getId(),
                s.getGradoSolicitado().getNombre(), s.getFechaSolicitud(), s.getEstado());
    }
}
