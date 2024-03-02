package com.example.market.controller;

import com.example.market.jwt.JwtRequestDto;
import com.example.market.jwt.JwtResponseDto;
import com.example.market.jwt.JwtTokenUtils;
import com.example.market.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/token")
@RequiredArgsConstructor
public class TokenController {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserService service;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/issue")
    public JwtResponseDto token(
            @RequestBody
            JwtRequestDto dto
    ){
        if (!service.userExists(dto.getUsername()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        UserDetails userDetails = service.loadUserByUsername(dto.getUsername());

        if(!passwordEncoder
                .matches(dto.getPassword(), userDetails.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        String jwt = jwtTokenUtils.generateToken(userDetails);
        JwtResponseDto response = new JwtResponseDto();
        response.setToken(jwt);

        return response;

    }

}
