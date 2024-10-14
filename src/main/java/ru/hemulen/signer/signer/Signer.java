package ru.hemulen.signer.signer;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.tsp.TimeStampResp;
import org.bouncycastle.tsp.*;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import ru.hemulen.crypto.DigitalSignatureFactory;
import ru.hemulen.crypto.DigitalSignatureProcessor;
import ru.hemulen.crypto.KeyStoreWrapper;
import ru.hemulen.crypto.exceptions.SignatureProcessingException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Properties;

@Component
public class Signer {
    private DigitalSignatureProcessor digitalSignatureProcessor;
    private PrivateKey privateKey;
    private X509Certificate certificate;
    private String tsaURL;

    public Signer() throws UnrecoverableKeyException, CertificateException, KeyStoreException, NoSuchAlgorithmException {
        Properties props = new Properties();
        try {
            props.load(new FileReader("./config/config.ini"));
        } catch (IOException e) {
            System.err.println("Не удалось прочитать файл настроек");
            e.printStackTrace(System.err);
            System.exit(1);
        }
        new Signer(props.getProperty("CONTAINER_ALIAS"), props.getProperty("CONTAINER_PASSWORD"));
        tsaURL = props.getProperty("TSA_URL");
    }
    public Signer(String keyAlias, String password) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        DigitalSignatureFactory.init();
        digitalSignatureProcessor = DigitalSignatureFactory.getDigitalSignatureProcessor();
        KeyStoreWrapper keyStoreWrapper = DigitalSignatureFactory.getKeyStoreWrapper();
        privateKey = keyStoreWrapper.getPrivateKey(keyAlias, password.toCharArray());
        certificate = keyStoreWrapper.getX509Certificate(keyAlias);
    }

/*    public Element signXMLDSigDetached(Element document2Sign, String signatureId) throws SignatureProcessingException {
        return digitalSignatureProcessor.signXMLDSigDetached(document2Sign, signatureId, privateKey, certificate);
    }*/

    public byte[] signPKCS7Detached(InputStream argContent2Sign, PrivateKey argPrivateKey, X509Certificate argCertificate) throws SignatureProcessingException {
        return digitalSignatureProcessor.signPKCS7Detached(argContent2Sign, argPrivateKey, argCertificate);
    }

/*    public File signPKCS7Detached(File file2sign) throws SignatureProcessingException, IOException {
        InputStream inputStream = new FileInputStream(file2sign);
        byte[] result = digitalSignatureProcessor.signPKCS7Detached(inputStream, privateKey, certificate);
        // Файл с подписью создается в том же каталоге и с тем же именем, но с расширением sig
        String signFileName = file2sign + ".sig";
        File signFile = new File(signFileName);
        FileOutputStream signOutputStream = new FileOutputStream(signFile);
        signOutputStream.write(Base64.getEncoder().encode(result));
        signOutputStream.flush();
        signOutputStream.close();
        return signFile;
    }*/

    /**
     * Метод получает штамп времени у провайдера, URL которого указан в application.properties
     * @param digest
     * @return
     */
    public TimeStampResponse getTimeStamp(byte[] digest) {
        try {
            TimeStampRequestGenerator reqgen = new TimeStampRequestGenerator();
            // Запрашиваем включение в ответ сертификата службы штампов времени
            reqgen.setCertReq(true);
            TimeStampRequest req = reqgen.generate(TSPAlgorithms.GOST3411, digest);

            byte request[] = req.getEncoded();

            URL url = new URL(tsaURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-type", "application/timestamp-query");
            con.setRequestProperty("Content-length", String.valueOf(request.length));
            OutputStream out = con.getOutputStream();
            try {
                out.write(request);
                out.flush();
            } finally {
                out.close();
            }

            if (con.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException("Received HTTP error: " + con.getResponseCode() + " - " + con.getResponseMessage());
            }
            InputStream in = con.getInputStream();
            TimeStampResp resp = TimeStampResp.getInstance(new ASN1InputStream(in).readObject());
            TimeStampResponse response = new TimeStampResponse(resp);
            response.validate(req);
            System.out.println(response.getTimeStampToken().getTimeStampInfo().getGenTime());
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }
    
    public X509Certificate getCertificate() {
        return this.certificate;
    }

}
