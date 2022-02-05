package ru.hemulen.docsigner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/documents")
public class DocumentController {
    @PostMapping
    public ResponseEntity sendDocument(){
        try {

            return ResponseEntity.ok("Документ подписан и отправлен в СМЭВ");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
