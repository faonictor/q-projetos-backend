package br.edu.ifpe.q_projetos.config;

import br.edu.ifpe.q_projetos.model.Usuario;
import br.edu.ifpe.q_projetos.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

// Classe para popular o banco de dados com um usuário administrador inicial

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL:admin@ifpe.edu.br}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:admin123}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        if (!usuarioRepository.existsByEmail(adminEmail)) {
            Usuario admin = Usuario.builder()
                    .nome("Administrador do Sistema")
                    .email(adminEmail)
                    .senha(passwordEncoder.encode(adminPassword))
                    .role(Usuario.Role.ROLE_ADMIN)
                    .vinculo(Usuario.Vinculo.SERVIDOR)
                    .build();

            usuarioRepository.save(admin);
            System.out.println(">>> SEED: Usuário Administrador inicial criado (" + adminEmail + ")");
        }
    }
}
