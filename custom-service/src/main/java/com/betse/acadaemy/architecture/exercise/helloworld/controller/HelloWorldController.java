package com.betse.acadaemy.architecture.exercise.helloworld.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class HelloWorldController {
    @Value("${name:Default}")
    private String name;

    @GetMapping("/hello")
    public String hello() {
        String message = "Hello World!, " + name + " - " + new Date();
        System.out.println(message);

        return message;
    }
}
