package org.dojocore.Service;

import org.dojocore.Dto.ArchivoEventoEntradaDto;
import org.springframework.core.io.Resource;
import org.dojocore.Dto.ArchivoAccesoDto;
import org.dojocore.Dto.ArchivoEventoDto;
import org.dojocore.Model.Alumno;
import org.dojocore.Model.ArchivoEvento;
import org.dojocore.Model.Evento;
import org.dojocore.Repository.AlumnoRepository;
import org.dojocore.Repository.ArchivoEventoRepository;
import org.dojocore.Repository.EventoRepository;
import org.dojocore.Security.Usuario;
import org.dojocore.Storage.AlmacenArchivos;
import org.dojocore.Storage.EnlaceArchivoFirmador;
import org.dojocore.Storage.TipoArchivo;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ArchivoEventoService {

    private static final String RESTRINGIDO = "RESTRINGIDO";
    private static final Set<String> TIPOS_CON_VISTA = Set.of("PDF", "IMAGEN", "VIDEO", "AUDIO");

    private final ArchivoEventoRepository repo;
    private final EventoRepository eventoRepo;
    private final AlumnoRepository alumnoRepo;
    private final AlmacenArchivos almacen;
    private final EnlaceArchivoFirmador firmador;

    public ArchivoEventoService(ArchivoEventoRepository repo, EventoRepository eventoRepo,
                                AlumnoRepository alumnoRepo, AlmacenArchivos almacen,
                                EnlaceArchivoFirmador firmador){
        this.repo = repo;
        this.eventoRepo = eventoRepo;
        this.alumnoRepo = alumnoRepo;
        this.almacen = almacen;
        this.firmador = firmador;
    }

    public record ContenidoArchivo(Resource recurso, String contentType, ContentDisposition disposicion){
    }

    public List<ArchivoEventoDto> listar(Usuario usuario, Integer idEvento){
        exigirVisible(usuario, buscarEvento(idEvento));
        return repo.findByEventoIdOrderById(idEvento).stream().map(this::toDto).toList();
    }

    public ArchivoEventoDto obtener(Usuario usuario, Integer id){
        ArchivoEvento a = buscar(id);
        exigirVisible(usuario, a.getEvento());
        return toDto(a);
    }

    public ArchivoAccesoDto acceso(Usuario usuario, Integer id){
        ArchivoEvento a = buscar(id);
        exigirVisible(usuario, a.getEvento());
        if (a.esEnlaceExterno()){
            return new ArchivoAccesoDto(a.getRuta(), a.getRuta(), 0, null, vistaPreviaDeEnlace(a));
        }
        TipoArchivo tipo = tipoDe(a);
        String url = "/api/archivos/" + a.getId() + "/contenido?token=" + firmador.firmar(a.getId());
        return new ArchivoAccesoDto(url, url + "&descargar=true", firmador.segundosDeValidez(),
                tipo.contentType(), tipo.vistaPrevia());
    }

    public ContenidoArchivo contenido(Integer id, String token, boolean descargar){
        if (!firmador.valido(id, token)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Enlace inválido o vencido");
        }
        ArchivoEvento a = buscar(id);
        if (a.esEnlaceExterno()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este archivo es un enlace externo");
        }
        TipoArchivo tipo = tipoDe(a);
        Resource recurso = almacen.cargar(a.getRuta());
        boolean enLinea = !descargar && tipo.vistaPrevia();
        ContentDisposition disposicion = (enLinea ? ContentDisposition.inline() : ContentDisposition.attachment())
                .filename(nombreDeDescarga(a, tipo), StandardCharsets.UTF_8)
                .build();
        return new ContenidoArchivo(recurso, tipo.contentType(), disposicion);
    }

    @Transactional
    public ArchivoEventoDto crearEnlace(Integer idEvento, ArchivoEventoEntradaDto dto){
        if (dto.ruta() == null || dto.ruta().isBlank()){
            throw invalida("La Ruta Es Obligatoria Al Registrar Un Enlace");
        }
        ArchivoEvento a = new ArchivoEvento();
        a.setEvento(buscarEvento(idEvento));
        a.setNombre(dto.nombre().trim());
        a.setRuta(dto.ruta().trim());
        a.setTipo(textoOpcional(dto.tipo()));
        a.setFechaSubida(LocalDateTime.now());
        return toDto(repo.save(a));
    }

    @Transactional
    public ArchivoEventoDto subir(Integer idEvento, MultipartFile archivo, String nombre) {
        Evento evento = buscarEvento(idEvento);
        if (archivo == null || archivo.isEmpty()) {
            throw invalida("Falta el archivo");
        }
        String original = nombreOriginal(archivo);
        String extension = extension(original);
        TipoArchivo tipo = TipoArchivo.deExtension(extension).orElseThrow(() ->
                invalida("Tipo de archivo no permitido. Permitidos: " + TipoArchivo.permitidos()));
        byte[] cabecera;
        try (InputStream in = archivo.getInputStream()) {
            cabecera = in.readNBytes(16);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo leer el archivo");
        }
        if (!tipo.firmaValida(cabecera)) {
            throw invalida("El contenido del archivo no corresponde a su extensión (." + tipo.extension() + ")");
        }
        String clave = almacen.guardar(archivo, tipo.extension());
        try {
            ArchivoEvento a = new ArchivoEvento();
            a.setEvento(evento);
            a.setNombre(nombreFinal(nombre, original));
            a.setRuta(clave);
            a.setTipo(tipo.categoria());
            a.setFechaSubida(LocalDateTime.now());
            return toDto(repo.save(a));
        } catch (RuntimeException e) {
            almacen.eliminar(clave);
            throw e;
        }
    }

    @Transactional
    public ArchivoEventoDto actualizar(Integer id, ArchivoEventoEntradaDto dto){
        ArchivoEvento a = buscar(id);
        a.setNombre(dto.nombre().trim());
        if (a.esEnlaceExterno()){
            if (dto.ruta() != null && !dto.ruta().isBlank()){
                a.setRuta(dto.ruta().trim());
            }
            a.setTipo(textoOpcional(dto.tipo()));
        } else if (dto.ruta() != null) {
            throw invalida("Un Archivo Subido No Cambia De Enlace; Sube Otro Archivo y Borra Este");
        }
        return toDto(a);
    }

    @Transactional
    public void eliminar(Integer id){
        ArchivoEvento a = buscar(id);
        boolean almacenado = !a.esEnlaceExterno();
        String clave = a.getRuta();
        repo.delete(a);
        repo.flush();
        if (almacenado){
            almacen.eliminar(clave);
        }
    }


    private void exigirVisible(Usuario usuario, Evento evento){
        if (usuario.personal() || !RESTRINGIDO.equalsIgnoreCase(evento.getVisibilidad())){
            return;
        }
        Alumno alumno = usuario.idPersona() == null ? null : alumnoRepo.findById(usuario.idPersona()).orElse(null);
        boolean vinculado = alumno != null
                && alumno.getEventos().stream().anyMatch(e -> e.getId().equals(evento.getId()));
        boolean enRango = alumno != null && evento.tieneRangoDeGrados()
                && evento.admiteGrado(alumno.getPersona().getGradoActual());
        if (!vinculado && !enRango){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No Tienes Los Permisos Para Ver Los Archivos De Este Evento");
        }
    }

    private TipoArchivo tipoDe(ArchivoEvento a){
        return TipoArchivo.deExtension(extension(a.getRuta())).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Tipo de archivo no reconocido"));
    }

    private static boolean vistaPreviaDeEnlace(ArchivoEvento a){
        return a.getTipo() != null && TIPOS_CON_VISTA.contains(a.getTipo().trim().toUpperCase());
    }

    private static String nombreDeDescarga(ArchivoEvento a, TipoArchivo tipo){
        String base = a.getNombre().replaceAll("[\\r\\n\"\\\\/]", "_").trim();
        if (base.isEmpty()){
            base = "archivo";
        }
        return base.toLowerCase().endsWith("." + tipo.extension()) ? base : base + "." + tipo.extension();
    }

    private static String nombreOriginal(MultipartFile archivo){
        String nombre = archivo.getOriginalFilename();
        if (nombre == null || nombre.isBlank()){
            throw invalida("El Archivo No Tiene Nombre");
        }
        int corte = Math.max(nombre.lastIndexOf('/'), nombre.lastIndexOf('\\'));
        return corte >= 0 ? nombre.substring(corte+1) : nombre;
    }

    private static String extension(String nombre){
        int punto = nombre.lastIndexOf('.');
        return punto < 0 ? "" : nombre.substring(punto +1).toLowerCase();
    }

    private static String nombreFinal(String nombre, String original){
        String base = (nombre != null && !nombre.isBlank()) ? nombre.trim() : original;
        if (nombre == null || nombre.isBlank()){
            int punto = base.lastIndexOf('.');
            base = punto > 0 ? base.substring(0, punto) : base;
        }
        return base.length() > 150 ? base.substring(0, 150) : base;
    }

    private static String textoOpcional(String s){
        return s == null || s.isBlank() ? null : s.trim();
    }

    private Evento buscarEvento(Integer id){
        return eventoRepo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento No Encontrado: " + id));
    }

    private ArchivoEvento buscar(Integer id){
        return repo.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Archivo No Encontrado: " + id));
    }

    private static ResponseStatusException invalida(String mensaje){
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, mensaje);
    }

    private ArchivoEventoDto toDto(ArchivoEvento a){
        if (a.esEnlaceExterno()){
            return new ArchivoEventoDto(a.getId(), a.getEvento().getId(), a.getNombre(), a.getTipo(), null,
                    a.getFechaSubida(), false, vistaPreviaDeEnlace(a), a.getRuta());
        }
        TipoArchivo tipo = TipoArchivo.deExtension(extension(a.getRuta())).orElse(null);
        return new ArchivoEventoDto(a.getId(), a.getEvento().getId(), a.getNombre(), a.getTipo(),
                tipo != null ? tipo.contentType() : null, a.getFechaSubida(), true,
                tipo != null && tipo.vistaPrevia(), null);
    }
}
