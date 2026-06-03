package br.edu.ifpe.q_projetos.dto;

// Importações do Jakarta Bean Validation necessárias
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public class UsuarioPerfilUpdateDTO {

    // O nome é livre, mas se o projeto exigir tamanho mínimo, pode adicionar @Size
    private String nome;

    @Email(message = "O formato do e-mail informado é inválido.")
    private String email;

    @Size(min = 6, message = "A senha deve conter no mínimo 6 caracteres.")
    private String senha;

    // Construtor padrão
    public UsuarioPerfilUpdateDTO() {
    }

    // Construtor completo
    public UsuarioPerfilUpdateDTO(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}