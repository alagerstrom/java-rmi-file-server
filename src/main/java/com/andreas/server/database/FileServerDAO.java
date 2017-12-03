package com.andreas.server.database;

import com.andreas.common.dto.FileMetaDTO;
import com.andreas.common.dto.UserDTO;
import com.andreas.common.exceptions.AccessDeniedException;
import com.andreas.common.exceptions.DatabaseException;
import com.andreas.server.model.User;

import java.util.List;

public interface FileServerDAO {
    List<UserDTO> getAllUsers() throws DatabaseException;

    User insertUser(String username, String password) throws DatabaseException;

    UserDTO login(String username, String password) throws DatabaseException;

    List<FileMetaDTO> getFiles(UserDTO currentUser) throws DatabaseException;

    void insertFile(FileMetaDTO fileMeta) throws DatabaseException;

    void removeUser(UserDTO user) throws DatabaseException;

    void deleteFile(UserDTO currentUser, FileMetaDTO fileMeta) throws DatabaseException, AccessDeniedException;

    boolean hasAccessRights(UserDTO currentUser, FileMetaDTO fileMeta) throws DatabaseException;

    boolean hasWritePermissions(UserDTO currentUser, FileMetaDTO fileMeta) throws DatabaseException;
}
