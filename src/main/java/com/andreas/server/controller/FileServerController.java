package com.andreas.server.controller;

import com.andreas.common.FileMetaDTO;
import com.andreas.common.FileServer;
import com.andreas.common.NotLoggedInException;
import com.andreas.common.UserDTO;
import com.andreas.server.database.DatabaseException;
import com.andreas.server.database.FileServerDAO;
import com.andreas.server.database.FileServerDAOImpl;
import com.andreas.server.model.FileMetaData;
import com.andreas.server.model.LoginManager;
import com.andreas.server.model.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;

public class FileServerController extends UnicastRemoteObject implements FileServer{

    private final FileServerDAO fileServerDAO;
    private final LoginManager loginManager = new LoginManager();

    public FileServerController() throws DatabaseException, RemoteException {
        fileServerDAO = new FileServerDAOImpl();
    }

    public UserDTO login(String username, String password) throws DatabaseException, RemoteException {
        UserDTO user = fileServerDAO.login(username, password);
        loginManager.addLoggedInUser(user);
        return fileServerDAO.login(username, password);
    }

    @Override
    public UserDTO registerUser(String username, String password) throws RemoteException, DatabaseException {
        List<UserDTO> allUsers = getAllUsers();
        for (UserDTO userDTO : allUsers){
            if (userDTO.getName().equals(username))
                return null;
        }
        UserDTO user = fileServerDAO.insertUser(username, password);
        loginManager.addLoggedInUser(user);
        return user;
    }

    @Override
    public void logout(UserDTO user) throws NotLoggedInException {
        if (loginManager.isLoggedIn(user))
            throw new NotLoggedInException();
        loginManager.removeLoggedInUser(user);
    }

    @Override
    public FileMetaDTO uploadFile(String filename, UserDTO currentUser, boolean readOnly, boolean publicAccess) throws RemoteException, DatabaseException, NotLoggedInException {
        if (!loginManager.isLoggedIn(currentUser))
            throw new NotLoggedInException();
        FileMetaData fileMetaData = new FileMetaData(filename, new User(currentUser.getId(), currentUser.getName()), readOnly, publicAccess);
        fileServerDAO.insertFile(fileMetaData);
        return fileMetaData;
    }

    @Override
    public List<FileMetaDTO> getFiles(UserDTO user) throws DatabaseException, NotLoggedInException {
        if (!loginManager.isLoggedIn(user))
            throw new NotLoggedInException();
        return fileServerDAO.getFiles(user);
    }

    @Override
    public void unregister(UserDTO currentUser) throws DatabaseException, NotLoggedInException {
        if (!loginManager.isLoggedIn(currentUser))
            throw new NotLoggedInException();
        fileServerDAO.removeUser(currentUser);
    }

    private List<UserDTO> getAllUsers() throws DatabaseException, RemoteException {
        return fileServerDAO.getAllUsers();
    }
}

