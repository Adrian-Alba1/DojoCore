package org.dojocore.Storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;

@Component
public class EnlaceArchivoFirmador {

    private final byte[] secreto;
    private final long minutos;

    public EnlaceArchivoFirmador(@Value("${app.jwt.secret}") String secreto,
                                 @Value("${app.archivos.enlace-minutos:10}") long minutos) {
        this.secreto = secreto.getBytes(StandardCharsets.UTF_8);
        this.minutos = minutos;
    }

    public long segundosDeValidez(){
        return minutos * 60;
    }

    public String firmar(Integer idArchivo){
        long expira = Instant.now().getEpochSecond() + segundosDeValidez();
        return expira + "." + firma(idArchivo, expira);
    }

    public boolean valido(Integer idArchivo, String token){
        if (token == null){
            return false;
        }
        int punto = token.indexOf('.');
        if (punto <= 0){
            return false;
        }
        long expira;
        try {
            expira = Long.parseLong(token.substring(0, punto));
        } catch (NumberFormatException e){
            return false;
        }
        if (expira < Instant.now().getEpochSecond()){
            return false;
        }
        byte [] esperada = firma(idArchivo, expira).getBytes(StandardCharsets.UTF_8);
        byte [] recibida = token.substring(punto + 1).getBytes(StandardCharsets.UTF_8);
        return MessageDigest.isEqual(esperada, recibida);
    }

    private String firma(Integer idArchivo, long expira){
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secreto, "HmacSHA256"));
            byte[] hash = mac.doFinal(("archivo:" + idArchivo + ":" + expira).getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (GeneralSecurityException e){
            throw new IllegalStateException("No Se Pudo Firmar El Enlace", e);
        }
    }
}
