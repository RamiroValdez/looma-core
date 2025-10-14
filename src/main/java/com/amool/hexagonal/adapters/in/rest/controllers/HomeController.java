package com.amool.hexagonal.adapters.in.rest.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HomeController {

    public HomeController() {}

    @GetMapping("/hello")
    public Map<String, String> hello(){
        return Map.of("message", "test");
    }

}

