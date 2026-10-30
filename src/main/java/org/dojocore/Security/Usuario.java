package org.dojocore.Security;

import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

public record Usuario(Integer idPersona, boolean personal)  {

    public static Usuario de(Authentication authentication) {
        return new Usuario(Acceso.idPersona(authentication), Acceso.esPersonal(authentication));
    }

    public void exigirPersonal() {
        if (!personal) {
            throw prohibido();
        }
    }

    public void exigirAcceso(Integer idPropietario) {
        if (!personal && !Objects.equals(idPersona, idPropietario)) {
            throw prohibido();
        }
    }

    public Integer actuarComo(Integer idSolicitado) {
        if (personal) {
            Integer id = idSolicitado != null ? idSolicitado : idPersona;
            if (id == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta indicar la persona");
            }
            return id;
        }
        if (idSolicitado != null && !idSolicitado.equals(idPersona)) {
            throw prohibido();
        }
        return idPersona;
    }

    private static ResponseStatusException prohibido() {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para esta operación");
    }
}
