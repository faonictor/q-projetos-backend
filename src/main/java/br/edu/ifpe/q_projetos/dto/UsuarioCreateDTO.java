package br.edu.ifpe.q_projetos.dto;

import br.edu.ifpe.q_projetos.model.Usuario.Role;
import br.edu.ifpe.q_projetos.model.Usuario.Vinculo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioCreateDTO {

    @NotBlank(message = "O nome é obrigatório.")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O formato do e-mail é inválido.")
    private String email;

    // Senha opcional para suportar Google Auth, mas validada se existir
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String senha;

    // A Role não precisa de validação estrita aqui, pois o Service força ROLE_USER se for nula/inválida
    private Role role;

    @NotNull(message = "O vínculo (SERVIDOR ou ESTUDANTE) é obrigatório.")
    private Vinculo vinculo;
}