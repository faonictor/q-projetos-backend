package br.edu.ifpe.q_projetos.security;

import br.edu.ifpe.q_projetos.exception.RecursoNaoEncontradoException;
import br.edu.ifpe.q_projetos.model.Usuario;
import br.edu.ifpe.q_projetos.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static Long getLoggedUserId(UsuarioRepository usuarioRepository) {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RecursoNaoEncontradoException("Acesso negado: Usuário não autenticado.");
        }

        Usuario user = usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário logado não encontrado."));

        return user.getId();
    }

    public static Usuario getLoggedUser(UsuarioRepository usuarioRepository) {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RecursoNaoEncontradoException("Acesso negado: Usuário não autenticado.");
        }

        return usuarioRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário logado não encontrado."));
    }

    public static void validarPermissaoAdmin() {
        Authentication auth = getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            throw new RecursoNaoEncontradoException("Acesso negado: Ação exclusiva para administradores.");
        }
    }

    public static void validarPermissaoDonoOuAdmin(String emailDono) {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RecursoNaoEncontradoException("Acesso negado: Usuário não autenticado.");
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isDono = auth.getName().equals(emailDono);

        if (!isAdmin && !isDono) {
            throw new RecursoNaoEncontradoException("Acesso negado: Você não possui permissão para acessar estes dados.");
        }
    }

    public static boolean isAdmin() {
        Authentication auth = getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public static String getLoggedUserEmail() {
        Authentication auth = getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            throw new RecursoNaoEncontradoException("Acesso negado: Usuário não autenticado.");
        }
        return auth.getName();
    }
}
