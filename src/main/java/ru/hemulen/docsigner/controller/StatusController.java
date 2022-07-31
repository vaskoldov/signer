package ru.hemulen.docsigner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hemulen.docsigner.exception.ResponseParseException;
import ru.hemulen.docsigner.model.Error;
import ru.hemulen.docsigner.model.StatusResponse;
import ru.hemulen.docsigner.service.StatusService;

@RestController
@RequestMapping("/status")
public class StatusController {
    @Autowired
    StatusService service;

    @GetMapping(path = "{clientId}")
    public ResponseEntity<StatusResponse> getStatus(@PathVariable String clientId) {
        try {
            StatusResponse response = service.getStatus(clientId);
            return ResponseEntity.ok(response);
        } catch (ResponseParseException e) {
            Error error = new Error();
            error.setCode("STATUS_PARSE_ERROR");
            error.setDescription("Ошибка при разборе ответов на запрос");
            StatusResponse response = new StatusResponse();
            response.setError(error);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
