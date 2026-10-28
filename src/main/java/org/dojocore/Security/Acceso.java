package org.dojocore.Security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class Acceso {

    private Acceso() {
    }

    /** SENSEI o SUPERADMIN. */
    public static boolean esPersonal(Authentication a) {
        return a != null && a.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(r -> r.equals("ROLE_SENSEI") || r.equals("ROLE_SUPERADMIN"));
    }

    /** id de la persona dueña de la cuenta (claim idPersona), o null si no hay token. */
    public static Integer idPersona(Authentication a) {
        if (a instanceof JwtAuthenticationToken token) {
            Object claim = token.getToken().getClaim("idPersona");
            if (claim instanceof Number n) {
                return n.intValue();
            }
        }
        return null;
    }
}
