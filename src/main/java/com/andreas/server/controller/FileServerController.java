package com.andreas.server.controller;

import com.andreas.common.FileMetaDTO;
import com.andreas.common.FileServer;
import com.andreas.common.UserDTO;
import com.andreas.server.database.DatabaseException;
import com.andreas.server.database.FileServerDAO;
import com.andreas.server.database.FileServerDAOImpl;
import com.andreas.server.model.FileMetaData;
import com.andreas.server.model.User;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.List;

public class FileServerController extends UnicastRemoteObject implements FileServer{

    private final FileServerDAO fileServerDAO;

    public FileServerController() throws DatabaseException, RemoteException {
        fileServerDAO = new FileServerDAOImpl();
    }

    public UserDTO login(String username, String password) throws DatabaseException, RemoteException {
        return fileServerDAO.login(username, password);
    }

    @Override
    public UserDTO registerUser(String username, String password) throws RemoteException, DatabaseException {
        List<UserDTO> allUsers = getAllUsers();
        for (UserDTO userDTO : allUsers){
            if (userDTO.getName().equals(username))
                return null;
        }
        fileServerDAO.insertUser(username, password);
        try {
            return fileServerDAO.getUserByName(username);
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public void logout(UserDTO user) {
        //TODO: implement some kind of check
    }

    @Override
    public FileMetaDTO uploadFile(String filename, UserDTO currentUser, boolean readOnly, boolean publicAccess) throws RemoteException, DatabaseException {
        FileMetaData fileMetaData = new FileMetaData(filename, new User(currentUser.getId(), currentUser.getName()), readOnly, publicAccess);
        fileServerDAO.insertFile(fileMetaData);
        return fileMetaData;
    }

    @Override
    public List<FileMetaDTO> getFiles(UserDTO user) throws DatabaseException {
        return fileServerDAO.getFiles(user);
    }

    private List<UserDTO> getAllUsers() throws DatabaseException, RemoteException {
        return fileServerDAO.getAllUsers();
    }
}

