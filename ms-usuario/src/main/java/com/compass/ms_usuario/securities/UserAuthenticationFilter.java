package com.compass.ms_usuario.securities;

import com.compass.ms_usuario.models.User;
import com.compass.ms_usuario.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenService jwtTokenService;

    private final UserRepository userRepository;

    /**
     * Filtra as requisições HTTP, recuperando e validando o token JWT do usuário.
     *
     * Este método é responsável por processar a requisição HTTP e verificar se um token JWT
     * está presente. Se o token for encontrado, ele tenta recuperar as informações do usuário
     * associado a esse token e define a autenticação no contexto de segurança do Spring.
     *
     * O processo envolve as seguintes etapas:
     * 1. Recupera o token JWT da requisição.
     * 2. Se o token não for nulo, extrai o assunto (subject) do token.
     * 3. Busca o usuário no repositório usando o email extraído do token.
     * 4. Se o usuário for encontrado, cria uma instância de UserDetailsImpl e define a autenticação
     *    no contexto de segurança.
     * 5. Se o usuário não for encontrado, lança uma exceção RuntimeException.
     *
     * @param request a requisição HTTP que está sendo filtrada
     * @param response a resposta HTTP a ser enviada
     * @param filterChain a cadeia de filtros que deve ser executada após este filtro
     * @throws ServletException se ocorrer um erro ao processar a requisição
     * @throws IOException se ocorrer um erro de entrada/saída durante o processamento
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = recoveryToken(request);
        if (token != null) {
            String subject = jwtTokenService.getSubjectFromToken(token);
            Optional<User> userOptional = userRepository.findByEmail(subject);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                UserDetailsImpl userDetails = new UserDetailsImpl(user);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails.getUsername(), null, userDetails.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                throw new RuntimeException("User not found.");
            }
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Recupera o token da requisição.
     *
     * @param request a requisição HTTP
     * @return o token JWT se encontrado, ou null se não
     */
    private String recoveryToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }
}
