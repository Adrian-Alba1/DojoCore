package org.dojocore.Service;

import org.dojocore.Dto.HorarioDto;
import org.dojocore.Model.DiaSemana;
import org.dojocore.Model.Horario;
import org.dojocore.Model.Sede;
import org.dojocore.Repository.DiaSemanaRepository;
import org.dojocore.Repository.HorarioRepository;
import org.dojocore.Repository.SedeRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class HorarioService {

    private final HorarioRepository repo;
    private final SedeRepository sedeRepo;
    private final DiaSemanaRepository diaRepo;

    public HorarioService (HorarioRepository repo, SedeRepository sedeRepo, DiaSemanaRepository diaRepo){
        this.repo = repo;
        this.sedeRepo = sedeRepo;
        this.diaRepo = diaRepo;
    }

    public List<HorarioDto> listar(Integer idSede, boolean incluirInactivos) {
        List<Horario> lista;
        if(idSede != null && !incluirInactivos){
            lista = repo.findBySedeIdAndActivoOrderById(idSede, true);
        } else if (idSede != null) {
            lista = repo.findBySedeIdOrderById(idSede);
        } else if (!incluirInactivos) {
            lista = repo.findByActivoOrderById(true);
        } else {
            lista = repo.findAll(Sort.by("id"));
        }
        return lista.stream().map(this::toDto).toList();
    }

    public HorarioDto obtener(Integer id){
        return toDto(buscar(id));
    }

    @Transactional
    public HorarioDto crear(HorarioDto dto){
        validarHoras(dto);
        Sede sede = sedeRepo.findById(dto.idSede()).orElseThrow(()->
                invalida("Sede no existe: "+ dto.idSede()));
        boolean activo = dto.activo() == null || dto.activo();
        validarSedeActiva(sede, activo);

        Horario h = new Horario();
        h.setSede(sede);
        h.setHoraInicio(dto.horarioInicio());
        h.setHoraFin(dto.horarioFin());
        h.setDescripcion(dto.descripcion());
        h.setActivo(activo);
        h.getDias().addAll(resolverDias(dto.idsDias()));
        return toDto(repo.save(h));
    }

    @Transactional
    public HorarioDto actualizar(Integer id, HorarioDto dto){
        Horario h = buscar(id);
        validarHoras(dto);
        Sede sede = h.getSede().getId().equals(dto.idSede())
                ? h.getSede()
                : sedeRepo.findById(dto.idSede()).orElseThrow(() -> invalida("Sede no Existe: " + dto.idSede()));
        boolean activo = dto.activo() != null ? dto.activo() : h.isActivo();
        validarSedeActiva(sede, activo);

        Set<DiaSemana> dias = resolverDias(dto.idsDias());
        h.setSede(sede);
        h.setHoraInicio(dto.horarioInicio());
        h.setHoraFin(dto.horarioFin());
        h.setDescripcion(dto.descripcion());
        h.setActivo(activo);
        h.getDias().clear();
        h.getDias().addAll(dias);
        return toDto(h);
    }

    @Transactional
    public void eliminar(Integer id){
        buscar(id).setActivo(false);
    }

    private Horario buscar (Integer id){
        return repo.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Horario no Encontrado: "+ id));
    }

    private void validarHoras(HorarioDto dto){
        if (!dto.horarioFin().isAfter(dto.horarioInicio())){
            throw invalida("La Hora de Finalización Debe Ser Posterior a la de Inicio");
        }
    }

    private void validarSedeActiva(Sede sede, boolean horarioActivo){
        if (horarioActivo && !Boolean.TRUE.equals(sede.getActivo())) {
            throw invalida("La Sede Esta Inactiva: "+sede.getId());
        }
    }

    private Set<DiaSemana> resolverDias(List<Integer> ids){
        Set<Integer> unicos = new HashSet<>(ids);
        List<DiaSemana> dias = diaRepo.findAllById((unicos));
        if (dias.size() != unicos.size()){
            throw invalida("Algún Día no Existe; los Válidos Van de 1 (Lunes) a 7 (Domingo)");
        }
        return new HashSet<>(dias);
    }

    private static ResponseStatusException invalida(String mensaje){
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }

    private HorarioDto toDto(Horario h){
        List<DiaSemana> dias = h.getDias().stream()
                .sorted(Comparator.comparing(DiaSemana::getId))
                .toList();
        return new HorarioDto(
                h.getId(),
                h.getSede().getId(),
                h.getSede().getNombre(),
                h.getHoraInicio(),
                h.getHoraFin(),
                h.getDescripcion(),
                h.isActivo(),
                dias.stream().map(DiaSemana::getId).toList(),
                dias.stream().map(DiaSemana::getNombre).toList());
    }
}
