package com.andreas.server.controller;

import com.andreas.common.FileClient;
import com.andreas.common.dto.FileMetaDTO;
import com.andreas.common.FileServer;
import com.andreas.common.exceptions.AccessDeniedException;
import com.andreas.common.exceptions.NotLoggedInException;
import com.andreas.common.dto.UserDTO;
import com.andreas.common.exceptions.DatabaseException;
import com.andreas.server.database.FileServerDAO;
import com.andreas.server.database.FileServerDAOImpl;
import com.andreas.server.model.*;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class FileServerController extends UnicastRemoteObject implements FileServer{

    private final FileServerDAO fileServerDAO;
    private final LoginManager loginManager = new LoginManager();
    private final List<Subscription> subscriptions = new ArrayList<>();
    private final FileHandler fileHandler = new FileHandler();

    public FileServerController() throws DatabaseException, RemoteException {
        fileServerDAO = new FileServerDAOImpl();
    }

    public synchronized UserDTO login(String username, String password) throws DatabaseException, RemoteException {
        UserDTO user = fileServerDAO.login(username, password);
        loginManager.addLoggedInUser(user);
        return fileServerDAO.login(username, password);
    }

    @Override
    public synchronized UserDTO registerUser(String username, String password) throws RemoteException, DatabaseException {
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
    public synchronized void logout(UserDTO user) throws NotLoggedInException {
        if (!loginManager.isLoggedIn(user))
            throw new NotLoggedInException();
        loginManager.removeLoggedInUser(user);
    }

    @Override
    public synchronized FileMetaDTO uploadFile(String filename, UserDTO currentUser, boolean readOnly, boolean publicAccess, int size, byte[] data) throws IOException, DatabaseException, NotLoggedInException {
        if (!loginManager.isLoggedIn(currentUser))
            throw new NotLoggedInException();
        FileMetaData fileMetaData = new FileMetaData(filename, new User(currentUser.getId(), currentUser.getName()), readOnly, publicAccess, size);
        fileHandler.save(fileMetaData, data);
        fileServerDAO.insertFile(fileMetaData);
        return fileMetaData;
    }

    @Override
    public synchronized List<FileMetaDTO> getFiles(UserDTO user) throws DatabaseException, NotLoggedInException {
        if (!loginManager.isLoggedIn(user))
            throw new NotLoggedInException();
        return fileServerDAO.getFiles(user);
    }

    @Override
    public synchronized void unregister(UserDTO currentUser) throws DatabaseException, NotLoggedInException {
        if (!loginManager.isLoggedIn(currentUser))
            throw new NotLoggedInException();
        fileServerDAO.removeUser(currentUser);
    }

    @Override
    public synchronized void deleteFile(UserDTO currentUser, FileMetaDTO fileMeta) throws DatabaseException, RemoteException, AccessDeniedException {
        fileServerDAO.deleteFile(currentUser, fileMeta);
        fileHandler.delete(fileMeta);
        System.out.println("File deleted, subscriptions: " + subscriptions.size());
        for (Subscription subscription : subscriptions){
            if (subscription.getFileMeta().getFilename().equals(fileMeta.getFilename())){
                if (subscription.getUser().getId() != currentUser.getId()){
                    subscription.getClient().fileDeleted(currentUser, fileMeta);
                }
            }
        }
    }

    @Override
    public synchronized byte[] downloadFile(UserDTO currentUser, FileMetaDTO fileMeta) throws DatabaseException, IOException, AccessDeniedException {
        if (!fileServerDAO.hasAccessRights(currentUser, fileMeta))
            throw new AccessDeniedException();
        byte[] data = fileHandler.read(fileMeta);
        for (Subscription subscription : subscriptions){
            if (subscription.getFileMeta().getFilename().equals(fileMeta.getFilename())){
                if (subscription.getUser().getId() != currentUser.getId()){
                    subscription.getClient().fileDownloaded(currentUser, fileMeta);
                }
            }
        }
        return data;
    }

    @Override
    public synchronized void subscribe(FileClient fileClient, UserDTO currentUser, FileMetaDTO fileMeta) {
        subscriptions.add(new Subscription(fileClient, currentUser, fileMeta));
        System.out.println("Subscriptions: " + subscriptions.size());
    }

    private List<UserDTO> getAllUsers() throws DatabaseException, RemoteException {
        return fileServerDAO.getAllUsers();
    }
}

