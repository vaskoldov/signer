package ru.hemulen.docsigner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hemulen.docsigner.entity.DocumentEntity;
import ru.hemulen.docsigner.exception.DocumentFileNotExists;
import ru.hemulen.docsigner.exception.DocumentSignException;
import ru.hemulen.docsigner.exception.FileOperationsException;
import ru.hemulen.docsigner.exception.XMLTransformationException;
import ru.hemulen.docsigner.service.DocumentService;

@RestController
@RequestMapping("/document/sign")
public class DocumentController {
    @Autowired
    private DocumentService documentService;

    @PostMapping
    public ResponseEntity sendDocument(@RequestBody DocumentEntity document) {
        try {
            documentService.processDocument(document);
            return ResponseEntity.ok("Документ подписан и отправлен в СМЭВ");
        } catch (DocumentFileNotExists e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (DocumentSignException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (FileOperationsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (XMLTransformationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
