package com.compass.ms_usuario.controllers;

import com.compass.ms_usuario.exceptions.DataAcessException;
import com.compass.ms_usuario.exceptions.UserAlreadyExistsException;
import com.compass.ms_usuario.exceptions.UserNotFoundException;
import com.compass.ms_usuario.models.Address;
import com.compass.ms_usuario.models.User;
import com.compass.ms_usuario.models.dto.*;
import com.compass.ms_usuario.services.UserService;
import com.compass.ms_usuario.services.ViaZipCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final ViaZipCodeService viaZipCodeService;


    @PostMapping("/auth")
    public ResponseEntity<RecoveryJwtTokenDto> authenticateUser(@RequestBody LoginUserDto loginUserDto) {
        try {
            RecoveryJwtTokenDto token = userService.authenticateUser(loginUserDto);
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> userRegister(@RequestBody UserRequestDTO userRequestDTO) {
        try {
            Address address = viaZipCodeService.searchAddressByZipCode(userRequestDTO.getCep());
            if (address == null) {
                return ResponseEntity.badRequest().body(null);
            }

            userRequestDTO.setAddress(new AddressRequestDTO(
                    address.getZipCode(),
                    address.getStreet(),
                    address.getComplement(),
                    address.getNeighborhood(),
                    address.getCity(),
                    address.getState()
            ));

            User user;
            try {
                user = userService.userRegister(userRequestDTO);
            } catch (UserAlreadyExistsException e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            } catch (DataAcessException e) {
                throw new RuntimeException(e);
            }

            UserResponseDTO userResponseDTO = new UserResponseDTO(
                    user.getUsername(),
                    user.getEmail(),
                    new AddressResponseDTO(
                            user.getAddress().getZipCode(),
                            user.getAddress().getStreet(),
                            user.getAddress().getComplement(),
                            user.getAddress().getNeighborhood(),
                            user.getAddress().getCity(),
                            user.getAddress().getState()
                    )
            );

            URI headerLocation = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(user.getId())
                    .toUri();
            return ResponseEntity.created(headerLocation).body(userResponseDTO);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PatchMapping("/update/password")
    public ResponseEntity<Void> updatePassword(@RequestBody UpdatePasswordRequestDTO updatePasswordRequestDTO) {
        try {
            userService.updatePassword(updatePasswordRequestDTO);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (DataAcessException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/get")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (DataAcessException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/get/{email}")
    public ResponseEntity <User> getUserByEmail(String email) {
        try {
            User users = userService.getUserByEmail(email);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (DataAcessException e) {
            throw new RuntimeException(e);
        }
    }

}
