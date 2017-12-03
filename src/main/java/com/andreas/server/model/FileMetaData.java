package com.andreas.server.model;

import com.andreas.common.dto.FileMetaDTO;
import com.andreas.common.dto.UserDTO;

public class FileMetaData implements FileMetaDTO{
    private String filename;
    private User owner;
    private int size;
    private boolean readOnly;
    private boolean publicAccess;

    public FileMetaData(String filename, User owner, boolean readOnly, boolean publicAccess, int size) {
        this.filename = filename;
        this.owner = owner;
        this.readOnly = readOnly;
        this.publicAccess = publicAccess;
        this.size = size;
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

    @Override
    public int getSize() {
        return size;
    }

}
