package org.dojocore.Storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface AlmacenArchivos {

    //guarda y da la clave interna
    String guardar(MultipartFile archivo, String extension);

    //contenido de una clave
    Resource cargar(String clave);

    void eliminar(String clave);
}
