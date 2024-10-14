package ru.hemulen.signer;

import ru.CryptoPro.JCP.Digest.AbstractGostDigest;
import ru.CryptoPro.JCP.Digest.GostDigest;
import ru.CryptoPro.JCP.JCP;
import ru.CryptoPro.JCP.params.OID;
import java.io.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.util.CollectionStore;
import ru.CryptoPro.CAdES.CAdESSignature;
import ru.CryptoPro.CAdES.CAdESSigner;
import ru.CryptoPro.CAdES.CAdESType;
import ru.CryptoPro.CAdES.exception.*;

import java.nio.file.Files;
import java.nio.file.Paths;
class ProxyAuthenticator extends Authenticator {

    private String user, password;

    public ProxyAuthenticator(String user, String password) {
        this.user = user;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password.toCharArray());
    }
}

public class run {
    public static String getPublicKeyOid(PrivateKey privateKey) {

        String privateKeyAlgorithm = privateKey.getAlgorithm();

        if (privateKeyAlgorithm.equalsIgnoreCase(JCP.GOST_EL_2012_256_NAME) ||
                privateKeyAlgorithm.equalsIgnoreCase(JCP.GOST_DH_2012_256_NAME)) {
            return JCP.GOST_PARAMS_SIG_2012_256_KEY_OID;
        } // if
        else if (privateKeyAlgorithm.equalsIgnoreCase(JCP.GOST_EL_2012_512_NAME) ||
                privateKeyAlgorithm.equalsIgnoreCase(JCP.GOST_DH_2012_512_NAME)) {
            return JCP.GOST_PARAMS_SIG_2012_512_KEY_OID;
        } // if

        return JCP.GOST_EL_KEY_OID;

    }

    public static String getDigestOid(PrivateKey privateKey) {

        String privateKeyAlgorithm = privateKey.getAlgorithm();

        if (privateKeyAlgorithm.equalsIgnoreCase(JCP.GOST_EL_2012_256_NAME) ||
                privateKeyAlgorithm.equalsIgnoreCase(JCP.GOST_DH_2012_256_NAME)) {
            return JCP.GOST_DIGEST_2012_256_OID;
        } // if
        else if (privateKeyAlgorithm.equalsIgnoreCase(JCP.GOST_EL_2012_512_NAME) ||
                privateKeyAlgorithm.equalsIgnoreCase(JCP.GOST_DH_2012_512_NAME)) {
            return JCP.GOST_DIGEST_2012_512_OID;
        } // if

        return JCP.GOST_DIGEST_OID;
    }


    public static String toHexString(byte[] array) {
        final char[] hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
                'B', 'C', 'D', 'E', 'F'};
        StringBuffer ss = new StringBuffer(array.length * 3);
        for (int i = 0; i < array.length; i++) {
            ss.append(' ');
            ss.append(hex[(array[i] >>> 4) & 0xf]);
            ss.append(hex[array[i] & 0xf]);
        }
        return ss.toString();
    }
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException, KeyStoreException, CertificateException, UnrecoverableKeyException, CAdESException {
        //proxy if need
        Authenticator.setDefault(new ProxyAuthenticator("user", "pass"));
        System.setProperty("http.proxyHost", "proxy.xxx.local");
        System.setProperty("http.proxyPort", "3128");
        //proxy if need

        //System.setProperty("ru.CryptoPro.reprov.enableCRLDP", "true");
        System.setProperty("com.sun.security.enableCRLDP", "true");
        System.setProperty("com.ibm.security.enableCRLDP", "true");
        String cer_story = "HDImageStore";
        String cer_alias = "certname";
        String cer_pass_str = "pass";
        char[] cer_pass = cer_pass_str.toCharArray();

        KeyStore keyStore = KeyStore.getInstance( cer_story );
        keyStore.load( null, null );
        PrivateKey privateKey = (PrivateKey) keyStore.getKey( cer_alias, cer_pass );

        X509Certificate cert = (X509Certificate) keyStore.getCertificate( cer_alias );
        List<X509Certificate> chain = Arrays.asList( cert );
        //new sign
        CAdESSignature cadesSignature = new CAdESSignature( true );
        //add certs in sign
        Collection<X509CertificateHolder> holderList = new ArrayList<X509CertificateHolder>();
        for (X509Certificate cert1 : chain)
        {
            holderList.add(new X509CertificateHolder(cert1.getEncoded()));
        }
        cadesSignature.setCertificateStore(new CollectionStore(holderList));
        //add certs in sign

        //data to sign
        byte[] src = Files.readAllBytes(Paths.get("C:\\_localsoft\\FSSP_FILES\\answers\\arc\\res_13504107761.xml"));

        // Создаем подписанта CAdES-BES.
        cadesSignature.addSigner( "JCP", getDigestOid(privateKey),getPublicKeyOid(privateKey),privateKey, chain, CAdESType.CAdES_T, "http://www.cryptopro.ru/tsp/", false );

        // Будущая подпись в виде массива.
        ByteArrayOutputStream signatureStream = new ByteArrayOutputStream();
        // Подготовка контекста.
        cadesSignature.open( signatureStream );
        // Хеширование.
        cadesSignature.update( src );

        // Создание подписи с выводом в signatureStream.
        cadesSignature.close();
        signatureStream.close();
        // Получаем подпись в виде массива.
        byte[] cadesCms = signatureStream.toByteArray();

        FileOutputStream out = new FileOutputStream("res_13504107761.xml.sig");
        out.write(cadesCms);
        out.close();
        System.out.println("END");
    }
}