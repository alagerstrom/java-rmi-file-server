package com.andreas.common;

import com.andreas.server.database.DatabaseException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface FileServer extends Remote {

    UserDTO login(String username, String password) throws RemoteException, DatabaseException;

    UserDTO registerUser(String username, String password) throws RemoteException, DatabaseException;

    void logout(UserDTO user) throws RemoteException, NotLoggedInException;

    FileMetaDTO uploadFile(String filename, UserDTO currentUser, boolean readOnly, boolean publicAccess) throws RemoteException, DatabaseException, NotLoggedInException;

    List<FileMetaDTO> getFiles(UserDTO user) throws RemoteException, DatabaseException, NotLoggedInException;

    void unregister(UserDTO currentUser) throws DatabaseException, RemoteException, NotLoggedInException;
}
