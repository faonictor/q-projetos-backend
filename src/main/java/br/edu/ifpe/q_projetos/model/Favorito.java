package br.edu.ifpe.q_projetos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "favorito")
@Data                 // Gera Getters, Setters, Equals, HashCode e ToString
@NoArgsConstructor    // Gera o construtor padrão (JPA requirement)
@AllArgsConstructor   // Gera o construtor completo
public class Favorito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "id_projeto", nullable = false)
    private Long idProjeto;

    @CreationTimestamp // Automatiza o registro da data no momento do INSERT
    @Column(name = "data_registro", nullable = false, updatable = false)
    private LocalDateTime dataRegistro;

    /**
     * Construtor para uso prático no código.
     * O ID e a Data serão gerados automaticamente.
     */
    public Favorito(Long idUsuario, Long idProjeto) {
        this.idUsuario = idUsuario;
        this.idProjeto = idProjeto;
    }
}