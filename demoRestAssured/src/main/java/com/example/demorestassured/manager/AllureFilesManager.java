package com.example.demorestassured.manager;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class AllureFilesManager {

    @Value("${allure.path}")
    private String allureFilesPath;

    public void clearDirectory() throws IOException {
        Files.newDirectoryStream(Path.of(allureFilesPath), Files::isRegularFile)
                .forEach(p -> {
                    try {
                        Files.delete(p);
                    } catch (IOException e) {
                    }
                });
    }

    public void packFilesToZip(ZipOutputStream zipOutputStream) throws IOException {
        Files.newDirectoryStream(Path.of(allureFilesPath), Files::isRegularFile)
                .forEach(p -> {
                    ZipEntry entry = new ZipEntry(p.getFileName().toString());
                    try {
                        zipOutputStream.putNextEntry(entry);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

        zipOutputStream.finish();
        zipOutputStream.flush();
        IOUtils.closeQuietly(zipOutputStream);
    }
}
