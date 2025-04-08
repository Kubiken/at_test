package com.example.demorestassured.service;

import com.example.demorestassured.manager.AllureFilesManager;
import com.example.demorestassured.manager.TestRunnerManager;
import com.example.demorestassured.models.request.RunChecklistRequest;
import lombok.AllArgsConstructor;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

@Service
@AllArgsConstructor
public class TestRunnerServiceImpl implements TestRunnerService {

    private final TestRunnerManager trm;
    private final AllureFilesManager allureFilesManager;
    @Override
    public byte[] runTests(RunChecklistRequest request) {

        try {
            allureFilesManager.clearDirectory();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        trm.runTests(request);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

        try {
            allureFilesManager.packFilesToZip(zipOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
