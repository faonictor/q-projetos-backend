package br.edu.ifpe.q_projetos.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    public enum Role {
        ROLE_ADMIN,
        ROLE_COORD,
        ROLE_USER
    }

    public enum Vinculo {
        SERVIDOR,
        ESTUDANTE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    // é necessário implementar lo hash hihihihi (O Louco Bulovask) no serviço
    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Vinculo vinculo;
}

// ajuste joão