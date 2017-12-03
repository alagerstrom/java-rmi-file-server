package com.andreas.common;

import com.andreas.common.dto.FileMetaDTO;
import com.andreas.common.dto.UserDTO;
import com.andreas.common.exceptions.AccessDeniedException;
import com.andreas.common.exceptions.NotLoggedInException;
import com.andreas.common.exceptions.DatabaseException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FileServer extends Remote {

    UserDTO login(String username, String password) throws RemoteException, DatabaseException;

    UserDTO registerUser(String username, String password) throws RemoteException, DatabaseException;

    void logout(UserDTO user) throws RemoteException, NotLoggedInException;

    FileMetaDTO uploadFile(String filename, UserDTO currentUser, boolean readOnly, boolean publicAccess, int size) throws RemoteException, DatabaseException, NotLoggedInException;

    List<FileMetaDTO> getFiles(UserDTO user) throws RemoteException, DatabaseException, NotLoggedInException;

    void unregister(UserDTO currentUser) throws DatabaseException, RemoteException, NotLoggedInException;

    void deleteFile(UserDTO currentUser, FileMetaDTO fileMeta) throws DatabaseException, RemoteException, AccessDeniedException;

    void downloadFile(UserDTO currentUser, FileMetaDTO fileMeta) throws DatabaseException, RemoteException, AccessDeniedException;

    void subscribe(FileClient fileClient, UserDTO currentUser, FileMetaDTO fileMeta) throws RemoteException;
}
