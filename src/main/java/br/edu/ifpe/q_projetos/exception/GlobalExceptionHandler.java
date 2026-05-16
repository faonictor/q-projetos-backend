package br.edu.ifpe.q_projetos.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Captura erros do @Valid (Resolve o 3º Teste)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Pega todos os erros do DTO e junta em uma string amigável
        String errosAgrupados = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(" | "));

        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Requisição Inválida");
        body.put("erro", errosAgrupados); // Mantém a chave "erro" que o seu front já espera

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 2. Captura os erros de lógica de negócio (Resolve o 4º Teste)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        String mensagem = ex.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // Ajuste na prioridade das checagens de texto
        if (mensagem.contains("Acesso negado") || mensagem.contains("não encontrado no sistema")) {
            status = HttpStatus.FORBIDDEN; // 403 - Perfeito para quando não houver usuário logado
        } else if (mensagem.contains("não encontrado com o ID")) {
            status = HttpStatus.NOT_FOUND; // 404 - Registro realmente não existe no banco
        } else if (mensagem.contains("Regra de Negócio")) {
            status = HttpStatus.BAD_REQUEST; // 400 - Validações de e-mail duplicado, etc.
        }

        return ResponseEntity.status(status).body(Map.of("erro", mensagem));
    }
}