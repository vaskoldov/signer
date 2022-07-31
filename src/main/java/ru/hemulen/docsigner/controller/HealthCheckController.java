package ru.hemulen.docsigner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/healthcheck")
public class HealthCheckController {
    @GetMapping
    public ResponseEntity healthCheck() {
        return (ResponseEntity) ResponseEntity.ok("Service is active!");
    }
}
