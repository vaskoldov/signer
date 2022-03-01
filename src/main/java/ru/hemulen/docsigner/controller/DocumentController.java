package ru.hemulen.docsigner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hemulen.docsigner.entity.Content;
import ru.hemulen.docsigner.entity.DocumentEntity;
import ru.hemulen.docsigner.entity.DocumentResponseEntity;
import ru.hemulen.docsigner.entity.Error;
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

    @PostMapping(value = "unep")
    public ResponseEntity sendDocument(@RequestBody DocumentEntity document) {
        try {
            documentService.processDocument(document);
            DocumentResponseEntity response = new DocumentResponseEntity(new Content(document.getClientId()));
            return ResponseEntity.ok(response);
        } catch (DocumentFileNotExists e) {
            DocumentResponseEntity response = new DocumentResponseEntity(new Error("DOCUMENT_NOT_EXISTS",
                    String.format("По указанному пути - %s - файл документа не найден", document.getDocumentPath())));
            return ResponseEntity.badRequest().body(response);
        } catch (DocumentSignException e) {
            DocumentResponseEntity response = new DocumentResponseEntity(new Error("SIGN_ERROR",
                    String.format("Не удалось подписать файл %s", document.getDocumentPath())));
            return ResponseEntity.badRequest().body(response);
        } catch (FileOperationsException e) {
            DocumentResponseEntity response = new DocumentResponseEntity(new Error("FILE_MOVE_ERROR",
                    String.format("Не удалось переместить файл %s", document.getDocumentPath())));
            return ResponseEntity.badRequest().body(response);
        } catch (XMLTransformationException e) {
            DocumentResponseEntity response = new DocumentResponseEntity(new Error("XML_ERROR",
                    String.format("Не удалось сформировать ClientMessage для файла %s", document.getDocumentPath())));
            return ResponseEntity.badRequest().body(response);
        }

    }

    @PostMapping("ukep")
    public ResponseEntity sendDocumentUKEP(@RequestBody DocumentEntity document) {
        try {
            documentService.processDocumentUKEP(document);
            DocumentResponseEntity response = new DocumentResponseEntity(new Content(document.getClientId()));
            return ResponseEntity.ok(response);
        } catch (DocumentFileNotExists e) {
            DocumentResponseEntity response = new DocumentResponseEntity(new Error("DOCUMENT_NOT_EXISTS",
                    String.format("По указанному пути - %s - файл документа не найден", document.getDocumentPath())));
            return ResponseEntity.badRequest().body(response);
        } catch (DocumentSignException e) {
            DocumentResponseEntity response = new DocumentResponseEntity(new Error("SIGN_ERROR",
                    String.format("Не удалось подписать файл %s", document.getDocumentPath())));
            return ResponseEntity.badRequest().body(response);
        } catch (FileOperationsException e) {
            DocumentResponseEntity response = new DocumentResponseEntity(new Error("FILE_MOVE_ERROR",
                    String.format("Не удалось переместить файл %s", document.getDocumentPath())));
            return ResponseEntity.badRequest().body(response);
        } catch (XMLTransformationException e) {
            DocumentResponseEntity response = new DocumentResponseEntity(new Error("XML_ERROR",
                    String.format("Не удалось сформировать ClientMessage для файла %s", document.getDocumentPath())));
            return ResponseEntity.badRequest().body(response);
        }
    }


}
