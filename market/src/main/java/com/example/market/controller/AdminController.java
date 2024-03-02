package com.example.market.controller;

import com.example.market.dto.UserDto;
import com.example.market.entity.UserEntity;
import com.example.market.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/admin")
@RestController
public class AdminController {
    private final UserService userService;

    @GetMapping("/apply-list")
    public List<UserDto> applyList(){
        return userService.userEntitySearchByAuthoritiesAndStatus("ROLE_USER", "Proceeding");
    }

    @PutMapping("/apply-admit/{id}")
    public String applyAdmit(
            @PathVariable("id") Long id){


        UserEntity userEntity = userService.searchById(id);

        userEntity.setAuthorities("ROLE_CEO");
        userEntity.setStatus("Admitted");

        userService.updateUser(userEntity);

        return "admitted";
    }

    @PutMapping("/apply-reject/{id}")
    public String applyReject(
            @PathVariable("id") Long id){

        UserEntity userEntity = userService.searchById(id);

        userEntity.setStatus("Rejected");
        userService.updateUser(userEntity);

        return "rejected";
    }

}
