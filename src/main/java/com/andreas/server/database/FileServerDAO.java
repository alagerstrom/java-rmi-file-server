package com.andreas.server.database;

import com.andreas.common.FileMetaDTO;
import com.andreas.common.UserDTO;
import com.andreas.server.model.User;

import java.sql.SQLException;
import java.util.List;

public interface FileServerDAO {
    List<UserDTO> getAllUsers() throws DatabaseException;

    User insertUser(String username, String password) throws DatabaseException;

    UserDTO login(String username, String password) throws DatabaseException;

    List<FileMetaDTO> getFiles(UserDTO currentUser) throws DatabaseException;

    void insertFile(FileMetaDTO fileMeta) throws DatabaseException;

    void removeUser(UserDTO user) throws DatabaseException;
}
