package com.andreas.server.model;

import com.andreas.common.dto.FileMetaDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class FileHandler {
    private static final String SERVER_DIRECTORY = "server_content";

    public FileHandler(){
        File file = new File(SERVER_DIRECTORY);
        if (!file.exists())
            file.mkdir();
    }
    public void save(FileMetaDTO fileMeta, byte[] data) throws IOException {
        File file = new File(SERVER_DIRECTORY + File.separator + fileMeta.getFilename());
        try(FileOutputStream fileOutputStream = new FileOutputStream(file)){
            fileOutputStream.write(data);
            fileOutputStream.close();
        }
    }
    public byte[] read(FileMetaDTO fileMeta) throws IOException {
        File file = new File(SERVER_DIRECTORY + File.separator + fileMeta.getFilename());
        return Files.readAllBytes(file.toPath());
    }

    public void delete(FileMetaDTO fileMeta) {
        File file = new File(SERVER_DIRECTORY + File.separator + fileMeta.getFilename());
        if (file.exists())
            file.delete();
    }
}
