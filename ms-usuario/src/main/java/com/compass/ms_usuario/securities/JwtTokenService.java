package com.compass.ms_usuario.securities;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class JwtTokenService {

    private static final String SECRET_KEY = "4Z^XrroxR@dWxqf$mTTKwW$!@#qGr4P";
    private static final String ISSUER = "ms-user";

    /**
     * Gera um token JWT para um usuário.
     *
     * @param user o objeto UserDetailsImpl que contém os detalhes do usuário
     * @return o token JWT gerado
     */
    public String generateToken(UserDetailsImpl user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withIssuedAt(creationDate())
                    .withExpiresAt(expirationDate())
                    .withSubject(user.getUsername())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new JWTCreationException("Error generating token.", exception);
        }
    }

    /**
     * Obtém o assunto (username) do token.
     *
     * @param token o token JWT a ser verificado
     * @return o assunto do token
     */
    public String getSubjectFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
            return JWT.require(algorithm)
                    .withIssuer(ISSUER)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Invalid or expired token.");
        }
    }

    /**
     * Retorna a data de criação do token.
     *
     * @return um Instant representando a data de criação
     */
    public Instant creationDate() {
        return ZonedDateTime.now(ZoneId.of("America/Recife")).toInstant();
    }

    /**
     * Retorna a data de expiração do token.
     *
     * @return um Instant representando a data de expiração
     */
    public Instant expirationDate() {
        return ZonedDateTime.now(ZoneId.of("America/Recife")).plusHours(1).toInstant();
    }
}
