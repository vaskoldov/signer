package ru.hemulen.docsigner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hemulen.docsigner.exception.ResponseParseException;
import ru.hemulen.docsigner.model.Error;
import ru.hemulen.docsigner.model.ResultResponse;
import ru.hemulen.docsigner.model.StatusResponse;
import ru.hemulen.docsigner.service.ResultService;
import ru.hemulen.docsigner.service.StatusService;

@RestController
@RequestMapping("/result")
public class ResultController {
    @Autowired
    ResultService service;

    @GetMapping("{clientId}")
    public ResponseEntity<ResultResponse> getResult(@PathVariable String clientId) {
        try {
            ResultResponse response = service.getResult(clientId);
            return ResponseEntity.ok(response);
        } catch (ResponseParseException e) {
            Error error = new Error();
            error.setCode("RESULT_PARSE_ERROR");
            error.setDescription("Ошибка при разборе ответов на запрос");
            ResultResponse response = new ResultResponse();
            response.setError(error);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
