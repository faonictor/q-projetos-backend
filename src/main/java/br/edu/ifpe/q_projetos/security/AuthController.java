package br.edu.ifpe.q_projetos.security;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.edu.ifpe.q_projetos.DTO.LoginRequest;
import br.edu.ifpe.q_projetos.DTO.UsuarioCreateDTO;
import br.edu.ifpe.q_projetos.DTO.UsuarioResponseDTO;
import br.edu.ifpe.q_projetos.model.Usuario;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthService authService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, AuthService authService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // 1. O Spring Security tenta autenticar o e-mail e a senha
        var usernamePassword = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.senha());

        Authentication auth = this.authenticationManager.authenticate(usernamePassword);

        // 2. Se chegou aqui, a senha está correta. Pegamos o usuário autenticado.
        Usuario usuario = (Usuario) auth.getPrincipal();

        // 3. Geramos o token para ele
        String token = jwtService.generateToken(usuario);

        // 4. Retornamos o token e os dados básicos para o front-end
        return ResponseEntity.ok(Map.of(
                "token", token,
                "nome", usuario.getNome(),
                "role", usuario.getRole().name()));
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> register(
            @Valid @RequestBody UsuarioCreateDTO dto) {

        UsuarioResponseDTO usuario = authService.registrar(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuario);
    }
}