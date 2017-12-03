package com.andreas.client.controller;

import com.andreas.common.Constants;
import com.andreas.common.FileServer;
import com.andreas.common.UserDTO;
import com.andreas.server.database.DatabaseException;
import com.andreas.server.model.FileMetaData;
import com.andreas.server.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.MalformedURLException;
import java.nio.channels.CompletionHandler;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.CompletableFuture;

public class ClientController {
    private static ClientController instance = new ClientController();
    private FileServer fileServer;
    private UserDTO currentUser;

    public static ClientController getInstance(){
        return instance;
    }

    private ClientController(){

    }

    public void connect(CompletionHandler<Void, String> completionHandler){
        CompletableFuture.runAsync(()->{
            try {
                if (fileServer == null)
                    this.fileServer = (FileServer) Naming.lookup(Constants.FILE_SERVER_REGISTRY_NAME);
                completionHandler.completed(null, "Connected.");
            } catch (NotBoundException | MalformedURLException | RemoteException e) {
                System.err.println("Failed to get reference to FileServer.");
                completionHandler.failed(e, "Failed to connect.");
                e.printStackTrace();
            }
        });
    }

    public void login(String username, String password, CompletionHandler<Void, String> completionHandler) {
        CompletableFuture.runAsync(()->{
            try {
                UserDTO user = fileServer.login(username, password);
                if (user != null){
                    currentUser = user;
                    completionHandler.completed(null, "Logged in.");
                }else{
                    completionHandler.failed(null, "Wrong username or password");
                }
            } catch (RemoteException | DatabaseException e) {
                completionHandler.failed(e, "Server error.");
            }
        });
    }

    public void registerUser(String username, String password, CompletionHandler<Void, String> completionHandler) {
        CompletableFuture.runAsync(()->{
            try {
                UserDTO user = fileServer.registerUser(username, password);
                if (user != null){
                    currentUser = user;
                    completionHandler.completed(null, "Registered.");
                }else{
                    completionHandler.failed(null, "User already exists.");
                }
            } catch (RemoteException | DatabaseException e) {
                completionHandler.failed(e, "Server error.");
            }
        });

    }

    public void logout(CompletionHandler<Void, Void> completionHandler) {
        CompletableFuture.runAsync(()->{
            try {
                fileServer.logout(currentUser);
                completionHandler.completed(null, null);
            } catch (RemoteException e) {
                completionHandler.failed(e,null);
            }
        });


    }

    public ObservableList<FileMetaData> getAllFiles() {
        ObservableList<FileMetaData> fileMetaData = FXCollections.observableArrayList();
        fileMetaData.add(new FileMetaData("Kalle.jpg",
                new User("Kalle"), true, false));
        return fileMetaData;
    }
}
