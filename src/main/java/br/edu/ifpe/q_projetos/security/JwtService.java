package br.edu.ifpe.q_projetos.security;

import br.edu.ifpe.q_projetos.model.Usuario;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import io.jsonwebtoken.JwtException;

@Service
public class JwtService {

  // Essa variável vai puxar o valor lá do seu application.properties
  @Value("${api.security.token.secret}")
  private String secret;

  public String generateToken(Usuario usuario) {
    // O JJWT exige que a chave secreta seja transformada em um objeto Key
    Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

    return Jwts.builder()
        .setIssuer("q-projetos-api") // Quem está emitindo
        .setSubject(usuario.getEmail()) // O "dono" do token
        .claim("role", usuario.getRole().name()) // Pendura o perfil (Admin, Coord, Estudante)
        .setExpiration(getExpirationDate()) // Quando expira
        .signWith(key, SignatureAlgorithm.HS256) // Assina usando o algoritmo HS256 e a sua chave
        .compact(); // Constrói a String final do JWT
  }

  private Date getExpirationDate() {
    // Expira em 2 horas (ajuste conforme a regra de negócio do IFPE)
    return Date.from(LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00")));
  }

  public String validateToken(String token) {
    try {
      Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
      return Jwts.parserBuilder()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token)
          .getBody()
          .getSubject(); // Retorna o e-mail do usuário
    } catch (JwtException e) {
      // Token inválido, expirado ou malformado
      return null;
    }
  }
}