package br.edu.ifpe.q_projetos.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioPerfilUpdateDTO {

    private String nome;
    
    @Email(message = "O formato do e-mail é inválido.")
    private String email;
    
    @Size(min = 6, message = "A nova senha deve ter no mínimo 6 caracteres.")
    private String senha; 
}
