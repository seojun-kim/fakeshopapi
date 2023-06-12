package com.example.fakeshopapi.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class RefreshTokenDTO {

    @NotEmpty
    private String refreshToken;
}
