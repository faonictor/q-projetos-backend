package br.edu.ifpe.q_projetos.controller;

import br.edu.ifpe.q_projetos.DTO.LoginRequest;
import br.edu.ifpe.q_projetos.model.Usuario;
import br.edu.ifpe.q_projetos.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
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
            "role", usuario.getRole().name()
        ));
    }
}