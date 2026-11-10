package org.dojocore.Dto;

public record ArchivoAccesoDto(
        String urlVista,
        String urlDescarga,
        long expiraEnSegundos,
        String contentType,
        boolean vistaPrevia) {
}
