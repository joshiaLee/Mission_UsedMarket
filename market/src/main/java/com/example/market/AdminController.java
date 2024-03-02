package com.example.market;

import com.example.market.article.dto.UserDto;
import com.example.market.service.JPAUserDetailsManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminController {
    private final JPAUserDetailsManager manager;

    @GetMapping("/apply-list")
    public List<UserDto> applyList(){
        return manager.userEntitySearchByAuthoritiesAndStatus("ROLE_USER", "Proceeding");
    }
}
