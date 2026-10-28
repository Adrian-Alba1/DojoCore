package org.dojocore.Security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PERSONAL = {"SENSEI", "SUPERADMIN"};

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/auth/me").authenticated()

                        // Datos propios: el personal o la propia persona
                        .requestMatchers(HttpMethod.GET,
                                "/api/alumnos/{id}",
                                "/api/alumnos/{id}/horarios",
                                "/api/alumnos/{id}/eventos",
                                "/api/alumnos/{id}/eventos-disponibles",
                                "/api/personas/{id}").access(personalOPropio("id"))
                        .requestMatchers(HttpMethod.POST, "/api/eventos/{idEvento}/alumnos/{idAlumno}")
                        .access(personalOPropio("idAlumno"))
                        .requestMatchers(HttpMethod.DELETE, "/api/eventos/{idEvento}/alumnos/{idAlumno}")
                        .access(personalOPropio("idAlumno"))

                        // Listados con datos de otras personas o de gestion: solo personal
                        .requestMatchers(HttpMethod.GET,
                                "/api/alumnos",
                                "/api/personas",
                                "/api/senseis/**",
                                "/api/participaciones/**",
                                "/api/inventario/**",
                                "/api/eventos/{id}/alumnos",
                                "/api/horarios/{id}/alumnos",
                                "/api/horarios/{id}/senseis",
                                "/api/variantes/{id}/inventario",
                                "/api/variantes/{id}/movimientos").hasAnyRole(PERSONAL)

                        // Catalogos: lectura para cualquier cuenta
                        .requestMatchers(HttpMethod.GET,
                                "/api/grados/**", "/api/tipos-evento/**", "/api/eventos/**",
                                "/api/categorias/**", "/api/resultados/**", "/api/sedes/**",
                                "/api/dias/**", "/api/horarios/**", "/api/productos/**",
                                "/api/variantes/**", "/api/atributos/**", "/api/valores/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/pedidos", "/api/pedidos/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/pedidos").authenticated()

                        .requestMatchers(HttpMethod.PATCH, "/api/solicitudes/*/{id}/resolucion").hasAnyRole(PERSONAL)
                        .requestMatchers("/api/solicitudes/**").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/senseis").hasRole("SUPERADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/senseis/{id}").hasRole("SUPERADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/senseis/{id}").hasRole("SUPERADMIN")

                        .anyRequest().hasAnyRole(PERSONAL))
                .oauth2ResourceServer(oauth -> oauth
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    private static AuthorizationManager<RequestAuthorizationContext> personalOPropio(String variable) {
        return (Supplier<Authentication> auth, RequestAuthorizationContext contexto) -> {
            Authentication a = auth.get();
            if (Acceso.esPersonal(a)) {
                return new AuthorizationDecision(true);
            }
            Integer idPersona = Acceso.idPersona(a);
            String valor = contexto.getVariables().get(variable);
            return new AuthorizationDecision(idPersona != null && String.valueOf(idPersona).equals(valor));
        };
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("roles");
        authorities.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }

    @Bean
    SecretKey jwtKey(@Value("${app.jwt.secret}") String secret) {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32){
            throw  new IllegalStateException("app.jwt.secret Debe Tener Al Menos 32 caracteres");
        }
        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    JwtEncoder jwtEncoder(SecretKey jwtKey){
        return new NimbusJwtEncoder(new ImmutableSecret<>(jwtKey));
    }

    @Bean
    JwtDecoder jwtDecoder(SecretKey jwtKey){
        return NimbusJwtDecoder.withSecretKey(jwtKey).macAlgorithm(MacAlgorithm.HS256).build();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
