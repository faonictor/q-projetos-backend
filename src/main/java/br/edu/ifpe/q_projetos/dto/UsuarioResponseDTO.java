package br.edu.ifpe.q_projetos.dto;

import br.edu.ifpe.q_projetos.model.Usuario.Role;
import br.edu.ifpe.q_projetos.model.Usuario.Vinculo;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {

    private Long id;
    private String nome;
    private String email;
    private Role role;
    private Vinculo vinculo;
    private String avatar;
}