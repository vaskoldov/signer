package ru.hemulen.signer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.util.unit.DataSize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import ru.hemulen.signer.exception.DocumentFileNotExists;
import ru.hemulen.signer.exception.DocumentSignException;
import ru.hemulen.signer.exception.InvalidRequestParameters;
import ru.hemulen.signer.exception.InvalidXMLException;
import ru.hemulen.signer.model.Error;
import ru.hemulen.signer.model.Request;
import ru.hemulen.signer.model.Response;
import ru.hemulen.signer.service.SignService;

import javax.servlet.MultipartConfigElement;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping("/api/v2")
public class MainController {
    @Autowired
    private SignService signService;

    @PreAuthorize("hasAuthority('USER')")
    @PostMapping(value = "pkcs7")
    public ResponseEntity signPKCS7(@RequestParam("file") MultipartFile file, HttpServletResponse response) {
        try {
            byte[] sign = signService.processPKCS7(file);
            byte[] encoded = Base64.getEncoder().encode(sign);
            response.setContentType("application/octet-stream");
            response.setContentLength(encoded.length);
            response.getOutputStream().write(encoded);
            response.flushBuffer();
        } catch (DocumentSignException|IOException е) {
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok(response);
    }

    /*
    //Этот метод не используется и поэтому пока вырезан
    @PostMapping("xmldsig")
    public ResponseEntity sendXMLDSig(@RequestBody Request request) {
        Response response = new Response();
        try {
            String signFilePath = signService.processXMLDSig(request);
            response.setSignPath(signFilePath);
            return ResponseEntity.ok(response);
        } catch (DocumentFileNotExists e) {
            Error error = new Error("DOCUMENT_NOT_EXISTS",
                    String.format("По указанному пути - %s - файл документа не найден", request.getFilePath()));
            response.setError(error);
            return ResponseEntity.badRequest().body(response);
        } catch (DocumentSignException e) {
            Error error = new Error("SIGN_ERROR",
                    String.format("Не удалось подписать файл %s", request.getFilePath()));
            response.setError(error);
            return ResponseEntity.badRequest().body(response);
        } catch (InvalidRequestParameters e) {
            Error error = new Error("INVALID_REQUEST_PARAMETERS",
                    "Не указан подписываемый XML-элемент");
            response.setError(error);
            return ResponseEntity.badRequest().body(response);
        } catch (InvalidXMLException e) {
            Error error = new Error ("INVALID_XML",
                    "В файле либо отсутствует подписываемый элемент, либо содержится более одного подписываемого элемента");
            response.setError(error);
            return ResponseEntity.badRequest().body(response);
        }
    }
     */

}
