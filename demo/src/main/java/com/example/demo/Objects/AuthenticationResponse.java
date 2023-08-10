package com.example.demo.Objects;

import lombok.Builder;
import lombok.Data;

/**
 * Class representing authentication response. It returns token required for authentication attempts
 *
 * @author Thorvas
 */
@Data
@Builder
public class AuthenticationResponse {

    private String token;
}
