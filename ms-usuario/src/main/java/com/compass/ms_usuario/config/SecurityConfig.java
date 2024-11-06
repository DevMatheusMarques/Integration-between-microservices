package com.compass.ms_usuario.config;

import com.compass.ms_usuario.securities.UserAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserAuthenticationFilter userAuthenticationFilter;

    /**
     * Configura as regras de segurança para a aplicação.
     *
     * Este método define a configuração de segurança utilizando o Spring Security, incluindo
     * o gerenciamento de sessão, as regras de autorização para diferentes endpoints,
     * o controle de headers HTTP, e o tratamento de exceções relacionadas à autenticação.
     * A configuração também desativa o CSRF e define um filtro de autenticação personalizado.
     *
     * @param http O objeto {@link HttpSecurity} que permite configurar as regras de segurança HTTP.
     * @return O objeto {@link SecurityFilterChain} que contém a configuração final de segurança.
     * @throws Exception Caso haja um erro durante a configuração de segurança.
     */
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.POST, "/api/user/register", "/api/user/auth").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user/get").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/user/update/password").authenticated()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterBefore(userAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configura o CORS (Cross-Origin Resource Sharing) para a aplicação.
     *
     * Este método cria e retorna uma configuração de CORS personalizada que permite o compartilhamento de recursos
     * entre diferentes origens, configurando permissões para métodos HTTP, cabeçalhos e credenciais.
     * A configuração permite acesso irrestrito a todas as origens e métodos e permite o envio de credenciais.
     *
     * @return Um objeto {@link CorsConfigurationSource} que contém as configurações de CORS para a aplicação.
     * @throws RuntimeException Se houver algum erro durante a configuração de CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        try {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowCredentials(true);
            configuration.addAllowedOriginPattern("*");
            configuration.addAllowedMethod("*");
            configuration.addAllowedHeader("*");

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);

            return source;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Configura o AuthenticationManager da aplicação.
     *
     * @param authenticationConfiguration A configuração de autenticação.
     * @return O AuthenticationManager configurado.
     * @throws Exception Se ocorrer um erro durante a configuração.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Cria e retorna um PasswordEncoder para codificação de senhas.
     *
     * @return Uma nova instância de BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        try {
            return new BCryptPasswordEncoder();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
