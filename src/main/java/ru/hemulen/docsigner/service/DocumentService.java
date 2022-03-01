package ru.hemulen.docsigner.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ru.hemulen.crypto.exceptions.SignatureProcessingException;
import ru.hemulen.docsigner.entity.DocumentEntity;
import ru.hemulen.docsigner.entity.DocumentResponseEntity;
import ru.hemulen.docsigner.exception.DocumentFileNotExists;
import ru.hemulen.docsigner.exception.DocumentSignException;
import ru.hemulen.docsigner.exception.FileOperationsException;
import ru.hemulen.docsigner.exception.XMLTransformationException;
import ru.hemulen.docsigner.signer.Signer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class DocumentService {
    Signer signer;
    String containerAlias;
    String containerPassword;
    String adapterOutPath;
    String adapterInPath;

    public DocumentService()  {
        containerAlias = "fsor012012";
        containerPassword = "12345678";
        adapterOutPath = "C:/Hemulen/fsor/integration/files/FSOR01_3T/out";
        adapterInPath = "C:/Hemulen/fsor/integration/files/FSOR01_3T/in";
        try {
            signer = new Signer(containerAlias, containerPassword);

        } catch (UnrecoverableKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException e) {

        }
    }

    public String processDocument(DocumentEntity document) throws DocumentSignException, DocumentFileNotExists, XMLTransformationException, FileOperationsException {
        document.setClientId(UUID.randomUUID().toString());
        // Подписываем документ
        File signFile = signDocument(document);
        // Формируем XML c запросом
        String clientMessage = createClientMessage(document);
        saveClientMessage(document, clientMessage);
        return  document.getClientId();
    }

    public String processDocumentUKEP(DocumentEntity document) throws DocumentSignException, DocumentFileNotExists, XMLTransformationException, FileOperationsException {
        document.setClientId(UUID.randomUUID().toString());
        // Подписываем документ
        File signFile = signDocument(document);
        // Формируем XML c запросом
        String clientMessage = createClientMessageUKEP(document);
        saveClientMessage(document, clientMessage);
        return document.getClientId();
    }
    private File signDocument (DocumentEntity document) throws DocumentFileNotExists, DocumentSignException {
        File documentFile = Paths.get(document.getDocumentPath()).toFile();
        if (!documentFile.exists()) {
            throw new DocumentFileNotExists("Указанный файл c документом не найден");
        }
        try {
            // Копируем подписываемый файл в каталог ./signed/[OID]
            Path targetPath = Paths.get(documentFile.toPath().getParent().toString(), "signed", document.getClientId());
            File targetFile = Paths.get(targetPath.toString(), documentFile.getName()).toFile();
            if (!targetPath.getParent().toFile().exists()) {
                Files.createDirectory(targetPath.getParent());
            }
            if (!targetPath.toFile().exists()) {
                Files.createDirectory(targetPath);
            }
            Files.move(documentFile.toPath(), targetFile.toPath());
            documentFile = targetFile;
            // Обновляем местоположение файла в документе тоже
            document.setDocumentPath(documentFile.getPath());
            File docSign = signer.signPKCS7Detached(documentFile);
            return docSign;
        } catch (SignatureProcessingException | IOException e) {
            throw new DocumentSignException("Не удалось подписать документ");
        }
    }

    public void saveClientMessage(DocumentEntity document, String clientMessage) throws FileOperationsException {
        Path outPath = Paths.get(adapterOutPath, document.getClientId() + ".xml");
        FileWriter fw = null;
        try {
            fw = new FileWriter(outPath.toFile());
            fw.write(clientMessage);
            fw.close();
        } catch (IOException e) {
            throw new FileOperationsException("Не удалось сохранить xml-файл с запросом");
        }
    }

    private String createClientMessage(DocumentEntity document) throws XMLTransformationException {
        try {
            // Определяем переменные, которые будут использоваться в запросе
            String clientIdValue = document.getClientId();
            String contractUUID = UUID.randomUUID().toString();
            String signUUID = UUID.randomUUID().toString();
            String currentTime = getCurrentTimestamp();
            String expireTime = getExpireTimestamp(currentTime);
            // ========= Заголовок ClientMessage и метаданные запроса =========
            Document root = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element rootElement = root.createElementNS("urn://x-artefacts-smev-gov-ru/services/service-adapter/types", "tns:ClientMessage");
            root.appendChild(rootElement);
            Element itSystem = root.createElement("tns:itSystem");
            itSystem.appendChild(root.createTextNode(document.getMnemonic()));
            rootElement.appendChild(itSystem);
            Element requestMessage = root.createElement("tns:RequestMessage");
            rootElement.appendChild(requestMessage);
            Element requestMetadata = root.createElement("tns:RequestMetadata");
            requestMessage.appendChild(requestMetadata);
            Element clientId = root.createElement("tns:clientId");
            clientId.appendChild(root.createTextNode(clientIdValue));
            requestMetadata.appendChild(clientId);
            Element requestContent = root.createElement("tns:RequestContent");
            requestMessage.appendChild(requestContent);
            Element content = root.createElement("tns:content");
            requestContent.appendChild(content);
            Element messagePrimaryContent = root.createElement("tns:MessagePrimaryContent");
            content.appendChild(messagePrimaryContent);
            // ======= Бизнес-запрос вида сведений =======
            Element requestSigContract = root.createElementNS("urn://gosuslugi/sig-contract/1.0.2", "sig:RequestSigContract");
            requestSigContract.setAttribute("Id", "ID_" + clientIdValue);
            requestSigContract.setAttribute("timestamp", currentTime);
            requestSigContract.setAttribute("routeNumber", "MNSV03");
            messagePrimaryContent.appendChild(requestSigContract);
            Element OID = root.createElement("sig:OID");
            OID.appendChild(root.createTextNode(document.getOid()));
            requestSigContract.appendChild(OID);
            Element signExp = root.createElement("sig:signExp");
            signExp.appendChild(root.createTextNode(expireTime));
            requestSigContract.appendChild(signExp);
            Element descDoc = root.createElement("sig:descDoc");
            descDoc.appendChild(root.createTextNode(document.getDescDoc()));
            requestSigContract.appendChild(descDoc);
            Element contracts = root.createElement("sig:Contracts");
            requestSigContract.appendChild(contracts);
            Element contract = root.createElement("sig:Contract");
            contracts.appendChild(contract);
            Element docElement = root.createElement("sig:Document");
            docElement.setAttribute("docId", document.getDocumentId());
            docElement.setAttribute("mimeType", "application/pdf");
            docElement.setAttribute("uuid", contractUUID);
            docElement.setAttribute("description", document.getDocumentDescription());
            contract.appendChild(docElement);
            Element signElement = root.createElement("sig:Signature");
            signElement.setAttribute("docId", "Подпись оператора");
            signElement.setAttribute("mimeType", "application/sig");
            signElement.setAttribute("uuid", signUUID);
            contract.appendChild(signElement);
            Element backlink = root.createElement("sig:Backlink");
            backlink.appendChild(root.createTextNode("https://volnamobile.ru/"));
            requestSigContract.appendChild(backlink);
            // ========== Блок вложений ClientMessage =========
            Element attachmentHeaderList = root.createElement("tns:AttachmentHeaderList");
            content.appendChild(attachmentHeaderList);
            Element docAttachmentHeader = root.createElement("tns:AttachmentHeader");
            attachmentHeaderList.appendChild(docAttachmentHeader);
            Element contractId = root.createElement("tns:Id");
            contractId.appendChild(root.createTextNode(contractUUID));
            docAttachmentHeader.appendChild(contractId);
            Element docPath = root.createElement("tns:filePath");
            docPath.appendChild(root.createTextNode(document.getDocumentPath()));
            docAttachmentHeader.appendChild(docPath);
            Element docTransferMethod = root.createElement("tns:TransferMethod");
            docTransferMethod.appendChild(root.createTextNode("REFERENCE"));
            docAttachmentHeader.appendChild(docTransferMethod);
            Element signAttachmentHeader = root.createElement("tns:AttachmentHeader");
            attachmentHeaderList.appendChild(signAttachmentHeader);
            Element signId = root.createElement("tns:Id");
            signId.appendChild(root.createTextNode(signUUID));
            signAttachmentHeader.appendChild(signId);
            Element signPath = root.createElement("tns:filePath");
            signPath.appendChild(root.createTextNode(document.getDocumentPath() + ".sig"));
            signAttachmentHeader.appendChild(signPath);
            Element signTransferMethod = root.createElement("tns:TransferMethod");
            signTransferMethod.appendChild(root.createTextNode("REFERENCE"));
            signAttachmentHeader.appendChild(signTransferMethod);
            return getStringFromDocument(root);
        } catch (ParserConfigurationException | TransformerException | ParseException e) {
            throw new XMLTransformationException("Не удалось сформировать XML с запросом");
        }
    }

    private String createClientMessageUKEP(DocumentEntity document) throws XMLTransformationException {
        try {
            // Определяем переменные, которые будут использоваться в запросе
            String clientIdValue = document.getClientId();
            String contractUUID = UUID.randomUUID().toString();
            String signUUID = UUID.randomUUID().toString();
            String currentTime = getCurrentTimestamp();
            String expireTime = getExpireTimestamp(currentTime);
            // ========= Заголовок ClientMessage и метаданные запроса =========
            Document root = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element rootElement = root.createElementNS("urn://x-artefacts-smev-gov-ru/services/service-adapter/types", "tns:ClientMessage");
            root.appendChild(rootElement);
            Element itSystem = root.createElement("tns:itSystem");
            itSystem.appendChild(root.createTextNode(document.getMnemonic()));
            rootElement.appendChild(itSystem);
            Element requestMessage = root.createElement("tns:RequestMessage");
            rootElement.appendChild(requestMessage);
            Element requestMetadata = root.createElement("tns:RequestMetadata");
            requestMessage.appendChild(requestMetadata);
            Element clientId = root.createElement("tns:clientId");
            clientId.appendChild(root.createTextNode(clientIdValue));
            requestMetadata.appendChild(clientId);
            Element requestContent = root.createElement("tns:RequestContent");
            requestMessage.appendChild(requestContent);
            Element content = root.createElement("tns:content");
            requestContent.appendChild(content);
            Element messagePrimaryContent = root.createElement("tns:MessagePrimaryContent");
            content.appendChild(messagePrimaryContent);
            // ======= Бизнес-запрос вида сведений =======
            Element requestSigContract = root.createElementNS("urn://gosuslugi/sig-contract-ukep/1.0.0", "sig:RequestSignUkep");
            requestSigContract.setAttribute("Id", "ID_" + clientIdValue);
            requestSigContract.setAttribute("timestamp", currentTime);
            requestSigContract.setAttribute("routeNumber", "MNSV03");
            messagePrimaryContent.appendChild(requestSigContract);
            Element OID = root.createElement("sig:OID");
            OID.appendChild(root.createTextNode(document.getOid()));
            requestSigContract.appendChild(OID);
            Element signExp = root.createElement("sig:signExp");
            signExp.appendChild(root.createTextNode(expireTime));
            requestSigContract.appendChild(signExp);
            Element descDoc = root.createElement("sig:descDoc");
            descDoc.appendChild(root.createTextNode(document.getDescDoc()));
            requestSigContract.appendChild(descDoc);
            Element contracts = root.createElement("sig:Contracts");
            requestSigContract.appendChild(contracts);
            Element contract = root.createElement("sig:Contract");
            contracts.appendChild(contract);
            Element docElement = root.createElement("sig:Document");
            docElement.setAttribute("docId", document.getDocumentId());
            docElement.setAttribute("mimeType", "application/pdf");
            docElement.setAttribute("uuid", contractUUID);
            docElement.setAttribute("description", document.getDocumentDescription());
            contract.appendChild(docElement);
            Element signElement = root.createElement("sig:Signature");
            signElement.setAttribute("docId", "Подпись оператора");
            signElement.setAttribute("mimeType", "application/sig");
            signElement.setAttribute("uuid", signUUID);
            contract.appendChild(signElement);
            Element backlink = root.createElement("sig:Backlink");
            backlink.appendChild(root.createTextNode("https://volnamobile.ru/"));
            requestSigContract.appendChild(backlink);
            // ========== Блок вложений ClientMessage =========
            Element attachmentHeaderList = root.createElement("tns:AttachmentHeaderList");
            content.appendChild(attachmentHeaderList);
            Element docAttachmentHeader = root.createElement("tns:AttachmentHeader");
            attachmentHeaderList.appendChild(docAttachmentHeader);
            Element contractId = root.createElement("tns:Id");
            contractId.appendChild(root.createTextNode(contractUUID));
            docAttachmentHeader.appendChild(contractId);
            Element docPath = root.createElement("tns:filePath");
            docPath.appendChild(root.createTextNode(document.getDocumentPath()));
            docAttachmentHeader.appendChild(docPath);
            Element docTransferMethod = root.createElement("tns:TransferMethod");
            docTransferMethod.appendChild(root.createTextNode("REFERENCE"));
            docAttachmentHeader.appendChild(docTransferMethod);
            Element signAttachmentHeader = root.createElement("tns:AttachmentHeader");
            attachmentHeaderList.appendChild(signAttachmentHeader);
            Element signId = root.createElement("tns:Id");
            signId.appendChild(root.createTextNode(signUUID));
            signAttachmentHeader.appendChild(signId);
            Element signPath = root.createElement("tns:filePath");
            signPath.appendChild(root.createTextNode(document.getDocumentPath() + ".sig"));
            signAttachmentHeader.appendChild(signPath);
            Element signTransferMethod = root.createElement("tns:TransferMethod");
            signTransferMethod.appendChild(root.createTextNode("REFERENCE"));
            signAttachmentHeader.appendChild(signTransferMethod);
            return getStringFromDocument(root);
        } catch (ParserConfigurationException | TransformerException | ParseException e) {
            throw new XMLTransformationException("Не удалось сформировать XML с запросом");
        }
    }

    private String getStringFromDocument(Document doc) throws TransformerException {
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        return writer.toString();
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return dateFormat.format(new Date());
    }

    private String getExpireTimestamp(String currentTimestamp) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Date current = dateFormat.parse(currentTimestamp);
        Calendar expired = Calendar.getInstance();
        expired.setTime(current);
        expired.add(Calendar.DATE, 14); // У абонента есть две недели на подписание документа
        return dateFormat.format(expired.getTime());
    }
}
