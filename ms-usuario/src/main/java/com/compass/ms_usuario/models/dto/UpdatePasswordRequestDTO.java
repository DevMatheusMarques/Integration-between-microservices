package com.compass.ms_usuario.models.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequestDTO {

    private String username;
    private String oldPassword;
    private String newPassword;
}
