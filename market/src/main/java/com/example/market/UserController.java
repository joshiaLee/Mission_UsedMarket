package com.example.market;

import com.example.market.entity.CustomUserDetails;
import com.example.market.service.JPAUserDetailsManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationFacade authFacade;
    @GetMapping("/home")
    public String home(){
        log.info(SecurityContextHolder.getContext().getAuthentication().getName());
        log.info(authFacade.getAuth().getName());
        return "index";
    }

    @GetMapping("/login")
    public String loginForm(){
        return "login-form";
    }

    @GetMapping("/my-profile")
    public String myProfile(
            Authentication authentication
    ){
//        log.info(authentication.getName());
//        log.info(((User) authentication.getPrincipal()).getPassword());
        log.info(((CustomUserDetails) authentication.getPrincipal()).getEmail());
        log.info(((CustomUserDetails) authentication.getPrincipal()).getPassword());
        log.info(((CustomUserDetails) authentication.getPrincipal()).getPhone());
        return "my-profile";
    }

    @GetMapping("/register")
    public String signUpForm(){
        return "register-form";
    }

    @PostMapping("/register")
    public String signUpRequest(
            @RequestParam("username")
            String username,
            @RequestParam("password")
            String password,
            @RequestParam("password-check")
            String passwordCheck
    ){
        if(password.equals(passwordCheck))
//            manager.createUser(User.withUsername(username)
//                    .password(passwordEncoder.encode(password))
//                    .build());
            manager.createUser(CustomUserDetails.builder()
                            .username(username)
                            .password(passwordEncoder.encode(password))
                    .build());

        return "redirect:/users/login";
    }
}
