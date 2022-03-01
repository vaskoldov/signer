package ru.hemulen.docsigner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

    @PostMapping(value = "unep", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity sendDocument(@RequestBody DocumentEntity document, @RequestParam MultipartFile file) {
        try {
            System.out.println(file.getName());
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

    @PostMapping("ukep")
    public ResponseEntity sendDocumentUKEP(@RequestBody DocumentEntity document) {
        try {
            documentService.processDocumentUKEP(document);
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
