package com.authentication.authenticationapp.dto;


import lombok.Data;

@Data
public class OtpValidationDto {
    private String username;
    private String otp;
}
