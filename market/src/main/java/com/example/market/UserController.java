package com.example.market;

import com.example.market.article.dto.UserDto;
import com.example.market.entity.CustomUserDetails;
import com.example.market.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {
    // UserDetailsManager에 주입되는것은 내가 만든 JPAUserDetatilsManeger
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
        log.info(authentication.getName());
        log.info(((CustomUserDetails) authentication.getPrincipal()).getUsername());
        log.info(((CustomUserDetails) authentication.getPrincipal()).getEmail());
        log.info(((CustomUserDetails) authentication.getPrincipal()).getPassword());
        log.info(((CustomUserDetails) authentication.getPrincipal()).getPhone());
        return "my-profile";
    }

    @GetMapping("/register")
    public String signUpForm(){
        return "register-form";
    }

    // 회원가입
    @PostMapping("/register")
    public String signUpRequest(
            @RequestBody
            UserDto userDto
    ){

        if(userDto.getPassword().equals(userDto.getPasswordCheck()))
            manager.createUser(CustomUserDetails.builder()
                            .username(userDto.getUsername())
                            .password(passwordEncoder.encode(userDto.getPassword()))
                            .authorities("ROLE_UNACTIVATED")
                    .build());
        else{
            log.info("비밀번호와 비밀번호 확인이 다릅니다.");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        return "redirect:/users/login";
    }

    // 추가정보 기입
    @PostMapping("/add-info")
    public String signUpAdd(
            @RequestBody
            UserDto userDto,
            Authentication authentication
    ){
        boolean isValid = StringUtils.isNotBlank(userDto.getNickname()) &&
                StringUtils.isNotBlank(userDto.getName()) &&
                userDto.getAge() != null && // Assuming age is an Integer
                StringUtils.isNotBlank(userDto.getEmail()) &&
                StringUtils.isNotBlank(userDto.getPhone());


        if (!isValid) {
            log.info("데이터 오류");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "모든 데이터를 입력해야 합니다.");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        customUserDetails.setNickname(userDto.getNickname());
        customUserDetails.setName(userDto.getName());
        customUserDetails.setAge(userDto.getAge());
        customUserDetails.setEmail(userDto.getEmail());
        customUserDetails.setPhone(userDto.getPhone());

        // 승급
        customUserDetails.setAuthorities("ROLE_USER");



        manager.updateUser(customUserDetails);

        return "redirect:/users/home";
    }
}
