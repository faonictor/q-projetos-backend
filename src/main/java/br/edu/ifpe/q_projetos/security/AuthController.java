package br.edu.ifpe.q_projetos.security;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.senha());
        Authentication auth = this.authenticationManager.authenticate(usernamePassword);
        Usuario usuario = (Usuario) auth.getPrincipal();
        String token = jwtService.generateToken(usuario);

        return ResponseEntity.ok(Map.of(
                "token", token,
                "nome", usuario.getNome(),
                "role", usuario.getRole().name()));
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> register(@Valid @RequestBody UsuarioCreateDTO dto) {
        UsuarioResponseDTO usuario = authService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }
}
