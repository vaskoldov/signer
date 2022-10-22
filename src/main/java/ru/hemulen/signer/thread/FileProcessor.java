package ru.hemulen.signer.thread;

import org.springframework.stereotype.Component;
import ru.hemulen.crypto.exceptions.SignatureProcessingException;
import ru.hemulen.signer.signer.Signer;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.Properties;

@Component
public class FileProcessor extends Thread{
    private String inputDir;
    private String outputDir;
    private long sleepTime;
    private Signer signer;

    public FileProcessor() {

    }
    @Override
    //@PostConstruct
    public void run() {
        System.out.println("Запуск процесса опроса каталога с файлами на подпись");
        Properties props = new Properties();
        try {
            props.load(new FileReader("./config/config.ini"));
        } catch (IOException e) {
            System.err.println("Не удалось прочитать файл настроек");
            e.printStackTrace(System.err);
            System.exit(1);
        }
        inputDir = props.getProperty("PATH_IN");
        outputDir = props.getProperty("PATH_OUT");
        sleepTime = Long.parseLong(props.getProperty("SLEEP_TIME"));
        try {
            signer = new Signer(props.getProperty("CONTAINER_ALIAS"), props.getProperty("CONTAINER_PASSWORD"));
        } catch (Exception e) {
            System.err.println("Не удалось инициализировать крипто-модуль");
            e.printStackTrace(System.err);
            System.exit(1);
        }

        Path pathIn = Paths.get(inputDir);
        Path pathOut = Paths.get(outputDir);
        if (!pathIn.toFile().exists() || !pathOut.toFile().exists()) {
            System.err.println("Отсутствует входной или выходной каталоги");
            System.exit(1);
        }
        while (true) {
            File[] files = pathIn.toFile().listFiles();
            if (files.length == 0) {
                try {
                    sleep(sleepTime);
                } catch (InterruptedException e) {
                    System.err.println("Ошибка при выполнении команды sleep");
                    e.printStackTrace(System.err);
                }
            }
            for (File file : files) {
                if (file.isDirectory()) {continue;}
                try {
                    File sigFile = signer.signPKCS7Detached(file);
                    Files.move(file.toPath(), pathOut.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
                    Files.move(sigFile.toPath(), pathOut.resolve(sigFile.getName()), StandardCopyOption.REPLACE_EXISTING);
                } catch (SignatureProcessingException | IOException e) {
                    System.err.println("Ошибка при подписании файла " + file.getName());
                    e.printStackTrace(System.err);
                    // При ошибке подписания оставляем файл в исходном каталоге для последующих попыток
                    continue;
                }
            }
        }
    }
}
