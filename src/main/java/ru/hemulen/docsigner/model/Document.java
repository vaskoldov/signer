package ru.hemulen.docsigner.model;


public class Document {
    private String mnemonic;
    private String oid;
    private String descDoc;
    private String documentId;
    private String documentPath;
    private String documentContent;
    private String documentDescription;
    private String signExp;
    private String clientId;
    private String signContent;

    public Document() {
    }

    public String getSignContent() {
        return signContent;
    }

    public void setSignContent(String signContent) {
        this.signContent = signContent;
    }

    public String getDocumentContent() {
        return documentContent;
    }

    public void setDocumentContent(String documentContent) {
        this.documentContent = documentContent;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getDescDoc() {
        return descDoc;
    }

    public void setDescDoc(String descDoc) {
        this.descDoc = descDoc;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDocumentPath() {
        return documentPath;
    }

    public void setDocumentPath(String documentPath) {
        this.documentPath = documentPath;
    }

    public String getDocumentDescription() {
        return documentDescription;
    }

    public void setDocumentDescription(String documentDescription) {
        this.documentDescription = documentDescription;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSignExp() {
        return signExp;
    }

    public void setSignExp(String signExp) {
        this.signExp = signExp;
    }
}
