package com.andreas.server.model;

import com.andreas.common.FileMetaDTO;
import com.andreas.common.UserDTO;

public class FileMetaData implements FileMetaDTO{
    private String filename;
    private User owner;
    private boolean readOnly;
    private boolean publicAccess;

    public FileMetaData(String filename, User owner, boolean readOnly, boolean publicAccess) {
        this.filename = filename;
        this.owner = owner;
        this.readOnly = readOnly;
        this.publicAccess = publicAccess;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public UserDTO getOwner() {
        return owner;
    }

    @Override
    public boolean readOnly() {
        return readOnly;
    }

    @Override
    public boolean publicAccess() {
        return publicAccess;
    }
}
