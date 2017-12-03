package com.andreas.common;

public interface FileMetaDTO {

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
        if (readOnly())
            return "Read only";
        else return "Read/Write";
    }

}
