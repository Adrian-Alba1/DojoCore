package org.dojocore.Service;

import org.dojocore.Dto.LoginDto;
import org.dojocore.Dto.MeDto;
import org.dojocore.Dto.TokenDto;
import org.dojocore.Model.Cuenta;
import org.dojocore.Repository.AlumnoRepository;
import org.dojocore.Repository.CuentaRepository;
import org.dojocore.Repository.SenseiRepository;
import org.dojocore.Security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private static final String HASH_RELLENO = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

    private final CuentaRepository cuentaRepo;
    private final AlumnoRepository alumnoRepo;
    private final SenseiRepository senseiRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(CuentaRepository cuentaRepo, AlumnoRepository alumnoRepo, SenseiRepository senseiRepo,
                       PasswordEncoder passwordEncoder, JwtService jwtService){
        this.cuentaRepo = cuentaRepo;
        this.alumnoRepo = alumnoRepo;
        this.senseiRepo = senseiRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public TokenDto login(LoginDto dto) {
        Cuenta cuenta = cuentaRepo.findByUsername(dto.username().trim()).orElse(null);
        String hash = cuenta != null ? cuenta.getPasswordHash() : HASH_RELLENO;
        boolean coincide = passwordEncoder.matches(dto.password(), hash);
        if (cuenta == null || !coincide || !Boolean.TRUE.equals(cuenta.getPersona().getActivo())) {
            throw credencialesInvalidas();
        }
        return jwtService.generar(cuenta, roles(cuenta));
    }

    public MeDto me(String username) {
        Cuenta cuenta = cuentaRepo.findByUsername(username).orElseThrow(AuthService::credencialesInvalidas);
        if (!Boolean.TRUE.equals(cuenta.getPersona().getActivo())) {
            throw credencialesInvalidas();
        }
        return new MeDto(cuenta.getId(), cuenta.getPersona().getId(), cuenta.getUsername(),
                cuenta.getPersona().getNombre(), roles(cuenta));
    }

    private List<String> roles(Cuenta cuenta) {
        List<String> roles = new ArrayList<>();
        if (cuenta.getRol() != null) {
            roles.add(cuenta.getRol().getNombre().trim().toUpperCase(Locale.ROOT));
        }
        Integer idPersona = cuenta.getPersona().getId();
        if (senseiRepo.existsById(idPersona)) {
            roles.add("SENSEI");
        }
        if (alumnoRepo.existsById(idPersona)) {
            roles.add("ALUMNO");
        }
        return roles;
    }

    private static ResponseStatusException credencialesInvalidas() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
    }
}
