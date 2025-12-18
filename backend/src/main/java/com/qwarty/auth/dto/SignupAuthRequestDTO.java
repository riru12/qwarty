package com.qwarty.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupAuthRequestDTO {
    private String email;
    private String username;
    private String password;
}