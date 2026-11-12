package org.dojocore.Controller;

import jakarta.validation.Valid;
import org.dojocore.Dto.ArchivoAccesoDto;
import org.dojocore.Dto.ArchivoEventoDto;
import org.dojocore.Dto.ArchivoEventoEntradaDto;
import org.dojocore.Security.Usuario;
import org.dojocore.Service.ArchivoEventoService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ArchivoEventoController {

    private final ArchivoEventoService service;

    public ArchivoEventoController(ArchivoEventoService service) {
        this.service = service;
    }

    @GetMapping("/eventos/{idEvento}/archivos")
    public List<ArchivoEventoDto> listar(@PathVariable Integer idEvento, Authentication auth) {
        return service.listar(Usuario.de(auth), idEvento);
    }

    @PostMapping(value = "/eventos/{idEvento}/archivos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ArchivoEventoDto subir(@PathVariable Integer idEvento,
                                  @RequestPart("file") MultipartFile file,
                                  @RequestParam(required = false) String nombre) {
        return service.subir(idEvento, file, nombre);
    }

    @PostMapping(value = "/eventos/{idEvento}/archivos", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ArchivoEventoDto crearEnlace(@PathVariable Integer idEvento,
                                        @Valid @RequestBody ArchivoEventoEntradaDto dto) {
        return service.crearEnlace(idEvento, dto);
    }

    @GetMapping("/archivos/{id}")
    public ArchivoEventoDto obtener(@PathVariable Integer id, Authentication auth) {
        return service.obtener(Usuario.de(auth), id);
    }

    @GetMapping("/archivos/{id}/acceso")
    public ArchivoAccesoDto acceso(@PathVariable Integer id, Authentication auth) {
        return service.acceso(Usuario.de(auth), id);
    }

    @GetMapping("/archivos/{id}/contenido")
    public ResponseEntity<Resource> contenido(@PathVariable Integer id,
                                              @RequestParam(required = false) String token,
                                              @RequestParam(defaultValue = "false") boolean descargar) {
        ArchivoEventoService.ContenidoArchivo c = service.contenido(id, token, descargar);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(c.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, c.disposicion().toString())
                .body(c.recurso());
    }

    @PutMapping("/archivos/{id}")
    public ArchivoEventoDto actualizar(@PathVariable Integer id, @Valid @RequestBody ArchivoEventoEntradaDto dto){
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/archivos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id){
        service.eliminar(id);
    }
}