package com.coding.alb.controller;

import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @GetMapping
    public String defaultGet(){
        return "all's well";
    }
}
