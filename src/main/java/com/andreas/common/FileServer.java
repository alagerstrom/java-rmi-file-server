package com.andreas.common;

import com.andreas.server.database.DatabaseException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileServer extends Remote {

    UserDTO login(String username, String password) throws RemoteException, DatabaseException;

    UserDTO registerUser(String username, String password) throws RemoteException, DatabaseException;

    void logout(UserDTO user) throws RemoteException;
}
