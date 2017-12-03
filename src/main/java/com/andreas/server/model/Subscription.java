package com.andreas.server.model;

import com.andreas.common.FileClient;
import com.andreas.common.dto.FileMetaDTO;
import com.andreas.common.dto.UserDTO;

public class Subscription {
    private FileClient client;
    private UserDTO user;
    private FileMetaDTO fileMeta;

    public Subscription(FileClient client, UserDTO user, FileMetaDTO fileMeta) {
        this.client = client;
        this.user = user;
        this.fileMeta = fileMeta;
    }

    public FileClient getClient() {
        return client;
    }

    public UserDTO getUser() {
        return user;
    }

    public FileMetaDTO getFileMeta() {
        return fileMeta;
    }
}
