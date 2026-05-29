package br.edu.ifpe.q_projetos.dto;

import br.edu.ifpe.q_projetos.model.Usuario.Role;
import br.edu.ifpe.q_projetos.model.Usuario.Vinculo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioUpdateDTO {

    private String nome;
    
    @Email(message = "O formato do e-mail é inválido.")
    private String email;
    
    // Senha opcional: se enviada, o Service aplicará o Hash
    @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres.")
    private String senha; 
    
    // Campos sensíveis moderados pelo Admin
    private Role role;
    private Vinculo vinculo;
}