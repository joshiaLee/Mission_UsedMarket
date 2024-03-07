package com.example.market.facade;

import com.example.market.entity.UserEntity;
import com.example.market.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationFacade {
    private final UserService userService;
    public Authentication getAuth(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication;
    }

    public UserEntity getUserEntity(){
        return userService.searchByUsername(getAuth().getName());
    }
}
