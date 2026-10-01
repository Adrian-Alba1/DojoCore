package org.dojocore.Service;

import org.dojocore.Dto.GradoDto;
import org.dojocore.Model.Grado;
import org.dojocore.Repository.GradoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GradoService {

    private final GradoRepository repo;

    public GradoService(GradoRepository repo){
        this.repo = repo;
    }

    public List<GradoDto> listar(){
        return repo.findAll().stream().map(this::toDto).toList();
    }

    public GradoDto obtener(Integer id){
        return toDto(buscar(id));
    }

    @Transactional
    public GradoDto crear(GradoDto dto){
        Grado g = new Grado();
        aplicar(g, dto);
        return toDto(repo.save(g));
    }

    @Transactional
    public GradoDto actualizar(Integer id, GradoDto dto){
        Grado g = buscar(id);
        aplicar(g, dto);
        return toDto(g);
    }

    @Transactional
    public void eliminar (Integer id){
        repo.delete(buscar(id));
    }

    private Grado buscar(Integer id){
        return repo.findById(id).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "El grado no fue encontrado: "+ id));
    }

    private void aplicar(Grado g, GradoDto dto){
        g.setNivel(dto.nivel());
        g.setNombre(dto.nombre());
        g.setColor(dto.color());
        g.setEtapa(dto.etapa());
        g.setOrden(dto.orden());
    }

    private GradoDto toDto(Grado g){
        return new GradoDto(g.getId(), g.getNivel(), g.getNombre(), g.getColor(), g.getEtapa(), g.getOrden());
    }

}
