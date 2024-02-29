package com.example.market;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthorizationController {
    @GetMapping("/user-role")
    public String userRole(){
        return "userRole";
    }

    @GetMapping("/admin-role")
    public String adminRole(){
        return "adminRole";
    }

    @GetMapping("/write-authority")
    public String writeAuthority() {
        return "writeAuthority";
    }

    @GetMapping("/read-authority")
    public String readAuthority(){
        return "readAuthority";
    }

}
