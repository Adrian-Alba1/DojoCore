package org.dojocore.Storage;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public enum TipoArchivo {
    PDF("pdf", "application/pdf", "PDF", true),
    JPG("jpg", "image/jpeg", "IMAGEN", true),
    JPEG("jpeg", "image/jpeg", "IMAGEN", true),
    PNG("png", "image/png", "IMAGEN", true),
    GIF("gif", "image/gif", "IMAGEN", true),
    WEBP("webp", "image/webp", "IMAGEN", true),
    MP4("mp4", "video/mp4", "VIDEO", true),
    WEBM("webm", "video/webm", "VIDEO", true),
    MOV("mov", "video/quicktime", "VIDEO", false),
    MP3("mp3", "audio/mpeg", "AUDIO", true),
    DOCX("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "DOCUMENTO", false),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "DOCUMENTO", false),
    PPTX("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation", "DOCUMENTO", false);

    private final String extension;
    private final String contentType;
    private final String categoria;
    private final boolean vistaPrevia;

    TipoArchivo(String extension, String contentType, String categoria, boolean vistaPrevia){
        this.extension = extension;
        this.contentType = contentType;
        this.categoria = categoria;
        this.vistaPrevia = vistaPrevia;
    }

    public String extension() {
        return extension;
    }
    public String contentType() {
        return contentType;
    }
    public String categoria(){
        return categoria;
    }
    public boolean vistaPrevia(){
        return vistaPrevia;
    }

    public static Optional<TipoArchivo> deExtension(String extension){
        if (extension == null){
            return Optional.empty();
        }
        String ext = extension.toLowerCase();
        return Arrays.stream(values()).filter(t -> t.extension.equals(ext)).findFirst();
    }

    public static String permitidos(){
        return Arrays.stream(values()).map(TipoArchivo::extension).collect(Collectors.joining(", "));
    }

    public boolean firmaValida(byte[] c){
        return switch (this){
            case PDF -> empieza(c, 0, 0x25, 0x50, 0x44, 0x46);
            case JPG, JPEG -> empieza(c ,0, 0xFF, 0xD8, 0XFF);
            case PNG -> empieza(c, 0, 0x89, 0x50, 0x4E, 0x47);
            case GIF -> empieza(c, 0, 0x47, 0x49, 0x46, 0x38);
            case WEBP -> empieza(c, 0, 0x52, 0x49, 0x46,0x46) && empieza(c, 8, 0x57, 0x45, 0x42, 0x50);
            case MP4, MOV -> empieza(c, 4, 0x66, 0x74, 0x79, 0x70);
            case WEBM -> empieza(c, 0, 0x1A, 0x45, 0xDF, 0xA3);
            case MP3 -> empieza(c, 0, 0x49, 0x44, 0x33)
                    || (c.length >=2 && (c[0] & 0xFF) == 0xFF && (c[1] & 0xE0) == 0xE0);
            case DOCX, XLSX, PPTX -> empieza(c, 0, 0x50, 0x4B, 0x03, 0x04);
        };
    }

    private static boolean empieza(byte[] c, int desde, int... esperando){
        if (c.length < desde + esperando.length){
            return false;
        }
        for (int i = 0; i < esperando.length; i++){
            if ((c[desde + i] & 0xFF) != esperando[i]){
                return false;
            }
        }
        return true;
    }
}
