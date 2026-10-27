package org.dojocore.Controller;

import jakarta.validation.Valid;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.dojocore.Dto.LoginDto;
import org.dojocore.Dto.MeDto;
import org.dojocore.Dto.TokenDto;
import org.dojocore.Service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service){
        this.service = service;
    }

    @PostMapping("/login")
    public TokenDto login(@Valid @RequestBody LoginDto dto){
        return service.login(dto);
    }

    @GetMapping("/me")
    public MeDto me(Authentication authentication){
        return service.me(authentication.name());
    }
}
