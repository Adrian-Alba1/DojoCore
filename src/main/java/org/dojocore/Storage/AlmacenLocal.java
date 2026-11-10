package org.dojocore.Storage;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Component
public class AlmacenLocal implements AlmacenArchivos {

    private static final Logger log = LoggerFactory.getLogger(AlmacenLocal.class);

    private final Path base;

    public AlmacenLocal(@Value("${app.archivos.directorio:./uploads}") String directorio){
        this.base = Paths.get(directorio).toAbsolutePath().normalize();
        try{
            Files.createDirectories(base);
        } catch (IOException e){
            throw new IllegalStateException("No Se Pudo Crear La Carpeta De Archivos: " + base, e);
        }
    }

    @Override
    public String guardar(MultipartFile archivo, String extension) {
       String clave = UUID.randomUUID() + "." + extension;
       try {
           archivo.transferTo(resolver(clave));
       } catch (IOException e) {
            log.error("No se pudo guardar el archivo {}", clave, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo guardar el archivo");
        }
        return clave;
    }

    @Override
    public Resource cargar(String clave) {
        Path ruta = resolver(clave);
        if (!Files.isReadable(ruta)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El archivo ya no está disponible");
        }
        return new FileSystemResource(ruta);
    }

    @Override
    public void eliminar(String clave) {
        try {
            Files.deleteIfExists(resolver(clave));
        } catch (IOException | RuntimeException e) {
            log.warn("No se pudo borrar el archivo {}", clave, e);
        }
    }

    private Path resolver(String clave) {
        Path ruta = base.resolve(clave).normalize();
        if (!ruta.startsWith(base)) {
            throw new IllegalArgumentException("Clave de archivo inválida");
        }
        return ruta;
    }
}
