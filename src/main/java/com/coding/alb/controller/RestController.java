package com.coding.alb.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @GetMapping
    public ResponseEntity<String> defaultGet(){
        return ResponseEntity.ok("Request forwarded successfully.");
    }

}
