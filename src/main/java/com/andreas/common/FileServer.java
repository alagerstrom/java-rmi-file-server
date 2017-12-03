package com.andreas.common;

import com.andreas.server.database.DatabaseException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FileServer extends Remote {

    UserDTO login(String username, String password) throws RemoteException, DatabaseException;

    UserDTO registerUser(String username, String password) throws RemoteException, DatabaseException;

    void logout(UserDTO user) throws RemoteException;

    FileMetaDTO uploadFile(String filename, UserDTO currentUser, boolean readOnly, boolean publicAccess) throws RemoteException, DatabaseException;

    List<FileMetaDTO> getFiles(UserDTO user) throws RemoteException, DatabaseException;
}
