package com.project.dto;

import java.util.List;

public record LoginResponse (
        String email,
        List<String> roles
){}