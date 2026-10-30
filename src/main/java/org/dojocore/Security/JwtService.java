package org.dojocore.Security;

import org.dojocore.Dto.TokenDto;
import org.dojocore.Model.Cuenta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class JwtService {

    private final JwtEncoder encoder;
    private final long minutos;

    public JwtService(JwtEncoder encoder, @Value("${app.jwt.expiration-minutes:60}") long minutos){
        this.encoder = encoder;
        this.minutos = minutos;
    }

    public TokenDto generar(Cuenta cuenta, List<String> roles){
        Instant ahora = Instant.now();
        Instant expira = ahora.plus(minutos, ChronoUnit.MINUTES);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("dojocore")
                .issuedAt(ahora)
                .expiresAt(expira)
                .subject(cuenta.getUsername())
                .claim("idCuenta", cuenta.getId())
                .claim("idPersona", cuenta.getPersona().getId())
                .claim("roles", roles)
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        String token = encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
        return new TokenDto(token, "Bearer", minutos * 60, cuenta.getUsername(),
                cuenta.getPersona().getId(), roles);
    }
}
