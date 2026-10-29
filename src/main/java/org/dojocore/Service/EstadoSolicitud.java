package org.dojocore.Service;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

public class EstadoSolicitud {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String APROBADA = "APROBADA";
    public static final String RECHAZADA = "RECHAZADA";

    private EstadoSolicitud(){
    }

    public static String resolucion(String estado){
        String e = estado.trim().toUpperCase(Locale.ROOT);
        if (!APROBADA.equals(e) && !RECHAZADA.equals(e)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Estado No Permitido " + estado + " (Use APROBADA o RECHAZADA)");
        }
        return e;
    }

    public static String filtro(String estado){
        return (estado == null || estado.isBlank()) ? null : estado.trim().toUpperCase(Locale.ROOT);
    }

    public static void exigirPendiente(String actual){
        if (!PENDIENTE.equals(actual)){
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La Solicitud Ya Fue Resuelta (" + actual + ")");
        }
    }
}
