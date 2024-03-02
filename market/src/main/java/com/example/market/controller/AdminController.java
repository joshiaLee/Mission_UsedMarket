package com.example.market.controller;

import com.example.market.dto.UserDto;
import com.example.market.dto.CustomUserDetails;
import com.example.market.entity.UserEntity;
import com.example.market.service.JPAUserDetailsManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/apply-admit/{id}")
    public String applyAdmit(
            @PathVariable("id") Long id){

        UserEntity userEntity = manager.searchById(id);
        userEntity.setAuthorities("ROLE_CEO");
        userEntity.setStatus("Admitted");

        manager.updateUser(CustomUserDetails.fromUserEntity(userEntity));

        return "admitted";
    }

    @PutMapping("/apply-reject/{id}")
    public String applyReject(
            @PathVariable("id") Long id){

        UserEntity userEntity = manager.searchById(id);
        userEntity.setStatus("Rejected");

        manager.updateUser(CustomUserDetails.fromUserEntity(userEntity));

        return "rejected";
    }

}
