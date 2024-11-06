package com.compass.ms_usuario.services;

import com.compass.ms_usuario.exceptions.DataAcessException;
import com.compass.ms_usuario.exceptions.UserNotFoundException;
import com.compass.ms_usuario.models.User;
import com.compass.ms_usuario.models.dto.*;
import com.compass.ms_usuario.repositories.UserRepository;
import com.compass.ms_usuario.securities.JwtTokenService;
import com.compass.ms_usuario.securities.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenService jwtTokenService;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAuthenticateUser() {
        LoginUserDto loginUserDto = new LoginUserDto("user@example.com", "password");
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);
        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtTokenService.generateToken(userDetails)).thenReturn("mocked-jwt-token");

        RecoveryJwtTokenDto recoveryJwtTokenDto = userService.authenticateUser(loginUserDto);

        assertNotNull(recoveryJwtTokenDto);
    }

    @Test
    void testUserRegister() throws DataAcessException {
        UserRequestDTO userRequestDTO = getUserRequestDTO();

        User user = new User();
        when(modelMapper.map(userRequestDTO, User.class)).thenReturn(user);
        when(passwordEncoder.encode(any())).thenReturn("encoded-password");
        when(userRepository.save(any())).thenReturn(user);

        User registeredUser = userService.userRegister(userRequestDTO);

        assertNotNull(registeredUser);
        assertEquals("encoded-password", registeredUser.getPassword());
        verify(rabbitTemplate).convertAndSend(anyString(), anyString());
    }

    private static UserRequestDTO getUserRequestDTO() {
        AddressRequestDTO addressRequestDTO = new AddressRequestDTO();
        addressRequestDTO.setCep("12345-678");
        addressRequestDTO.setLogradouro("Rua Exemplo");
        addressRequestDTO.setComplemento("Apto 1");
        addressRequestDTO.setBairro("Bairro Exemplo");
        addressRequestDTO.setLocalidade("Cidade Exemplo");
        addressRequestDTO.setUf("EX");

        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setAddress(addressRequestDTO);
        userRequestDTO.setPassword("password123");
        return userRequestDTO;
    }


    @Test
    void testUpdatePassword_UserNotFound() {
        UpdatePasswordRequestDTO request = new UpdatePasswordRequestDTO("username", "oldPassword", "newPassword");
        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updatePassword(request));
    }

    @Test
    void testUpdatePassword_OldPasswordDoesNotMatch() {
        UpdatePasswordRequestDTO request = new UpdatePasswordRequestDTO("username", "oldPassword", "newPassword");
        User user = new User();
        user.setPassword("differentOldPassword");
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        assertThrows(DataAcessException.class, () -> userService.updatePassword(request));
    }

    @Test
    void testGetAllUsers() throws DataAcessException {
        when(userRepository.findAll()).thenReturn(List.of(new User()));

        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertFalse(users.isEmpty());
    }
}
