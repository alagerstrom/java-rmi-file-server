package com.andreas.client.controller;

import com.andreas.client.view.SubscriptionHandler;
import com.andreas.common.*;
import com.andreas.common.dto.FileMetaDTO;
import com.andreas.common.dto.UserDTO;
import com.andreas.common.exceptions.AccessDeniedException;
import com.andreas.common.exceptions.NotLoggedInException;
import com.andreas.common.exceptions.DatabaseException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.channels.CompletionHandler;
import java.nio.file.Files;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.concurrent.CompletableFuture;

public class ClientController {
    private static ClientController instance = new ClientController();
    private FileServer fileServer;
    private UserDTO currentUser;
    private ObservableList<FileMetaDTO> files = FXCollections.observableArrayList();

    public static ClientController getInstance() {
        return instance;
    }

    private ClientController() {

    }

    public void connect(CompletionHandler<Void, String> completionHandler) {
        CompletableFuture.runAsync(() -> {
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
        CompletableFuture.runAsync(() -> {
            try {
                UserDTO user = fileServer.login(username, password);
                if (user != null) {
                    currentUser = user;
                    completionHandler.completed(null, "Logged in.");
                } else {
                    completionHandler.failed(null, "Wrong username or password");
                }
            } catch (RemoteException | DatabaseException e) {
                completionHandler.failed(e, "Server error.");
            }
        });
    }

    public void registerUser(String username, String password, CompletionHandler<Void, String> completionHandler) {
        CompletableFuture.runAsync(() -> {
            try {
                UserDTO user = fileServer.registerUser(username, password);
                if (user != null) {
                    currentUser = user;
                    completionHandler.completed(null, "Registered.");
                } else {
                    completionHandler.failed(null, "User already exists.");
                }
            } catch (RemoteException | DatabaseException e) {
                completionHandler.failed(e, "Server error.");
            }
        });
    }

    public void logout(CompletionHandler<Void, Void> completionHandler) {
        files.clear();
        CompletableFuture.runAsync(() -> {
            try {
                fileServer.logout(currentUser);
                completionHandler.completed(null, null);
            } catch (NotLoggedInException | RemoteException e) {
                completionHandler.failed(e, null);
            }
        });
    }

    public ObservableList<FileMetaDTO> getAllFiles() {
        refreshFiles();
        return files;
    }

    public void uploadFile(File file, boolean readOnly, boolean publicAccess) {
        int size = (int) file.length();
        String filename = file.getName();
        try {
            FileMetaDTO fileMetaDTO = null;
            try {
                fileMetaDTO = fileServer.uploadFile(filename, currentUser, readOnly, publicAccess, size, Files.readAllBytes(file.toPath()));
            } catch (IOException e) {
                System.err.println("Failed to read file.");
                e.printStackTrace();
            }
            files.add(fileMetaDTO);
        } catch (NotLoggedInException | DatabaseException e) {
            e.printStackTrace();
        }
    }

    public void unregister(CompletionHandler<Void, Void> completionHandler) {
        files.clear();
        try {
            fileServer.unregister(currentUser);
            completionHandler.completed(null, null);
        } catch (DatabaseException | RemoteException | NotLoggedInException e) {
            completionHandler.failed(e, null);
        }

    }

    public void deleteFile(FileMetaDTO fileMeta, CompletionHandler<Void, String> completionHandler) {
        CompletableFuture.runAsync(() -> {
            try {
                fileServer.deleteFile(currentUser, fileMeta);
                files.remove(fileMeta);
                completionHandler.completed(null, null);
            } catch (DatabaseException | RemoteException e) {
                completionHandler.failed(e, "Server error.");
            } catch (AccessDeniedException e) {
                completionHandler.failed(e, "Access denied.");
            }
        });
    }

    public void subscribe(FileMetaDTO fileMeta) {
        try {
            fileServer.subscribe(new SubscriptionHandler(), currentUser, fileMeta);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void download(File saveToFile, FileMetaDTO fileToDownload, CompletionHandler<Void, String> completionHandler) {
        CompletableFuture.runAsync(()->{
            try {
                byte[] data = fileServer.downloadFile(currentUser, fileToDownload);
                try(FileOutputStream fileOutputStream = new FileOutputStream(saveToFile)){
                    fileOutputStream.write(data);
                    fileOutputStream.close();
                }
                completionHandler.completed(null, null);
            } catch (DatabaseException | RemoteException e) {
                completionHandler.failed(e, "Server error.");
            } catch (AccessDeniedException e) {
                completionHandler.failed(e, "Access denied.");
            } catch (IOException e) {
                completionHandler.failed(e, "File not found.");
            }
        });
    }

    public void refreshFiles() {
        files.clear();
        CompletableFuture.runAsync(() -> {
            try {
                files.addAll(fileServer.getFiles(currentUser));
            } catch (NotLoggedInException | RemoteException | DatabaseException e) {
                e.printStackTrace();
            }
        });
    }
}
