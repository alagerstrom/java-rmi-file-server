package com.andreas.common;

import java.io.Serializable;

public interface FileMetaDTO extends Serializable{

    String getFilename();

    UserDTO getOwner();

    boolean readOnly();

    boolean publicAccess();

    default String getFileAccess(){
        if (publicAccess())
            return "Public";
        else return "Private";
    }

    default String getAccessRight(){
        if (!publicAccess())
            return "N/A";
        if (readOnly())
            return "Read only";
        else return "Read/Write";
    }

    int getSize();
}
