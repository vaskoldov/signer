package ru.hemulen.docsigner.model;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import ru.hemulen.docsigner.exception.ResponseParseException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MessageResponse {
    private String clientId;
    private String messageId;
    private String type;
    private String code;
    private String description;
    private ArrayList<String> attachments;

    public MessageResponse() {
        attachments = new ArrayList<>();
    }

    public void parseStatus(String xml) throws ResponseParseException {
        Document xmlDocument;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setIgnoringElementContentWhitespace(true);
        InputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression xPathExpression;
        String query;
        try {
            xmlDocument = factory.newDocumentBuilder().parse(is);
            query = "/*[local-name()='Status']/*[local-name()='Code']";
            xPathExpression = xPath.compile(query);
            code = (String) xPathExpression.evaluate(xmlDocument.getDocumentElement(), XPathConstants.STRING);
            query = "/*[local-name()='Status']/*[local-name()='Description']";
            xPathExpression = xPath.compile(query);
            description = (String) xPathExpression.evaluate(xmlDocument.getDocumentElement(), XPathConstants.STRING);
        } catch (ParserConfigurationException | XPathExpressionException | IOException | SAXException e) {
            throw new ResponseParseException(String.format("Ошибка при чтении ответа c cleintId %s из XML", clientId));
        }
    }

    public void parseReject(String xml) throws ResponseParseException {
        Document xmlDocument;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setIgnoringElementContentWhitespace(true);
        InputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression xPathExpression;
        String query;
        try {
            xmlDocument = factory.newDocumentBuilder().parse(is);
            query = "/*[local-name()='Reject']/*[local-name()='Item']/*[local-name()='Code']";
            xPathExpression = xPath.compile(query);
            code = (String) xPathExpression.evaluate(xmlDocument.getDocumentElement(), XPathConstants.STRING);
            query = "/*[local-name()='Reject']/*[local-name()='Item']/*[local-name()='Description']";
            xPathExpression = xPath.compile(query);
            description = (String) xPathExpression.evaluate(xmlDocument.getDocumentElement(), XPathConstants.STRING);
        } catch (ParserConfigurationException | XPathExpressionException | IOException | SAXException e) {
            throw new ResponseParseException(String.format("Ошибка при чтении ответа c cleintId %s из XML", clientId));
        }
    }
    public void parseError(String xml) throws ResponseParseException {
        Document xmlDocument;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setIgnoringElementContentWhitespace(true);
        InputStream is = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        XPath xPath = XPathFactory.newInstance().newXPath();
        XPathExpression xPathExpression;
        String query;
        try {
            xmlDocument = factory.newDocumentBuilder().parse(is);
            query = "/*[local-name()='Error']/*[local-name()='Code']";
            xPathExpression = xPath.compile(query);
            code = (String) xPathExpression.evaluate(xmlDocument.getDocumentElement(), XPathConstants.STRING);
            query = "/*[local-name()='Error']/*[local-name()='Description']";
            xPathExpression = xPath.compile(query);
            description = (String) xPathExpression.evaluate(xmlDocument.getDocumentElement(), XPathConstants.STRING);
        } catch (ParserConfigurationException | XPathExpressionException | IOException | SAXException e) {
            throw new ResponseParseException(String.format("Ошибка при чтении ответа c cleintId %s из XML", clientId));
        }
    }
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAttachment(String attachment) {
        this.attachments.add(attachment);
    }

    public ArrayList<String> getAttachments(){
        return attachments;
    }


}