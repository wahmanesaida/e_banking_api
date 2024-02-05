package com.ecommerce.api.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello {

    @GetMapping("/hi")
    public String hello(){
        return "welcome to you saida wish you good luck";
    }
    
}
