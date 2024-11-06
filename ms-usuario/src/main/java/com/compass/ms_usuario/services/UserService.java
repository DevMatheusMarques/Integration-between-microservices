package com.compass.ms_usuario.services;

import com.compass.ms_usuario.config.RabbitConfig;
import com.compass.ms_usuario.exceptions.DataAcessException;
import com.compass.ms_usuario.exceptions.UserNotFoundException;
import com.compass.ms_usuario.models.Address;
import com.compass.ms_usuario.models.User;
import com.compass.ms_usuario.models.dto.LoginUserDto;
import com.compass.ms_usuario.models.dto.RecoveryJwtTokenDto;
import com.compass.ms_usuario.models.dto.UpdatePasswordRequestDTO;
import com.compass.ms_usuario.models.dto.UserRequestDTO;
import com.compass.ms_usuario.repositories.UserRepository;
import com.compass.ms_usuario.securities.JwtTokenService;
import com.compass.ms_usuario.securities.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    private final RabbitTemplate rabbitTemplate;

    private final AuthenticationManager authenticationManager;

    private final JwtTokenService jwtTokenService;


    /**
     * Autentica o usuário e gera um token JWT.
     *
     * @param loginUserDto objeto com informações de login do usuário
     * @return um DTO contendo o token JWT gerado
     */
    public RecoveryJwtTokenDto authenticateUser(LoginUserDto loginUserDto) {
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(loginUserDto.email(), loginUserDto.password());
            Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return new RecoveryJwtTokenDto(jwtTokenService.generateToken(userDetails));
        } catch (AuthenticationException e) {
            throw new UserNotFoundException(loginUserDto.email());
        }
    }

    /**
     * Registra um novo usuário no sistema.
     *
     * @param userRequestDTO objeto com as informações do novo usuário
     * @return o usuário registrado
     */
    @Transactional
    public User userRegister(UserRequestDTO userRequestDTO) throws DataAcessException {
        try {
            User user = convertToEntity(userRequestDTO);

            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            String message = String.format("{\"username\":\"%s\", \"operation\":\"CREATE\", \"timestamp\":\"%s\"}", user.getUsername(), timestamp);
            try {
                rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_NAME, message);
            } catch (Exception e) {
                sendMessage(message);
                log.error("Error sending message to RabbitMQ: ", e);
            }
            return userRepository.save(user);
        } catch (Exception e) {
            throw new DataAcessException("Error creating user.");
        }
    }

    /**
     * Converte um UserRequestDTO em um objeto User.
     *
     * @param userRequestDTO o DTO com os dados do usuário
     * @return o objeto User correspondente
     */
    public User convertToEntity(UserRequestDTO userRequestDTO) {
        Address address = new Address(
                userRequestDTO.getAddress().getCep(),
                userRequestDTO.getAddress().getLogradouro(),
                userRequestDTO.getAddress().getComplemento(),
                userRequestDTO.getAddress().getBairro(),
                userRequestDTO.getAddress().getLocalidade(),
                userRequestDTO.getAddress().getUf()
        );

        User user = modelMapper.map(userRequestDTO, User.class);
        user.setAddress(address);

        return user;
    }

    /**
     * Atualiza a senha do usuário.
     *
     * @param request DTO com os dados para atualização da senha
     * @throws DataAcessException se ocorrer um erro durante a atualização
     */
    @Transactional
    public void updatePassword(UpdatePasswordRequestDTO request) throws DataAcessException {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedEmail = auth != null ? auth.getName() : null;

            if (authenticatedEmail == null) {
                throw new AccessDeniedException("User not authenticated.");
            }

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new UserNotFoundException("User not found."));

            if (!user.getEmail().equals(authenticatedEmail)) {
                throw new AccessDeniedException("You can only change your own password.");
            }

            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                throw new DataAcessException("Old password incorrect.");
            }

            user.setPassword(passwordEncoder.encode(request.getNewPassword()));

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            String message = String.format(
                    "{\"username\":\"%s\", \"operation\":\"UPDATE\", \"timestamp\":\"%s\", \"performedBy\":\"%s\"}",
                    user.getUsername(), timestamp, authenticatedEmail
            );

            try {
                rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_NAME, message);
            } catch (Exception e) {
                sendMessage(message);
                log.error("Error sending message to RabbitMQ: {}", String.valueOf(e));
            }
            userRepository.save(user);
        } catch (Exception e) {
            throw new DataAcessException("Error updating password.");
        }
    }

    @Transactional
    public List<User> getAllUsers() throws DataAcessException {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            throw new DataAcessException("Error updating password.");
        }
    }

    @Transactional
    public User getUserByEmail(String email) throws DataAcessException {
        try {
            return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
        } catch (Exception e) {
            throw new DataAcessException("User not found.");
        }
    }

    private void sendMessage(String message) {
        int maxRetries = 3;
        int attempt = 0;

        while (attempt < maxRetries) {
            try {
                rabbitTemplate.convertAndSend(RabbitConfig.QUEUE_NAME, message);
                return;
            } catch (Exception e) {
                attempt++;
                log.warn("Attempt {} failed to send message to RabbitMQ: {}", attempt, e.getMessage());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted during retry delay.", ie);
                }
            }
        }
        log.error("Failed to send message to RabbitMQ after {} attempts.", maxRetries);
    }
}
