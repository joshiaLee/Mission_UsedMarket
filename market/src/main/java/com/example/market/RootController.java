package com.example.market;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {
    @GetMapping("/home")
    public String root(){
        return "index";
    }

    @GetMapping("/no-auth")
    public String noAuth(){
        return "no auth success!";
    }

    @GetMapping("/require-auth")
    public String reAuth(){
        return "auth success!";
    }
}
