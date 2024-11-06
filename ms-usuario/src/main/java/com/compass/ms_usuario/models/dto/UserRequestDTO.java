package com.compass.ms_usuario.models.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDTO {

    private String username;
    private String password;
    private String email;
    private String cep;
    private AddressRequestDTO address;
}
