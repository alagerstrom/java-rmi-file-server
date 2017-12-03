package com.andreas.client.view;

import com.andreas.common.FileClient;
import com.andreas.common.dto.FileMetaDTO;
import com.andreas.common.dto.UserDTO;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class SubscriptionHandler extends UnicastRemoteObject implements FileClient, Serializable {
    public SubscriptionHandler() throws RemoteException {
    }

    @Override
    public void fileDownloaded(UserDTO user, FileMetaDTO fileMeta) {
        Platform.runLater(()->{
            System.out.println("File downloaded");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Notification");
            alert.setHeaderText("File downloaded");
            alert.setContentText(user.getName() + " downloaded " + fileMeta.getFilename());
            alert.showAndWait();
        });

    }

    @Override
    public void fileDeleted(UserDTO user, FileMetaDTO fileMeta) {
        Platform.runLater(()->{
            System.out.println("File deleted");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Notification");
            alert.setHeaderText("File deleted");
            alert.setContentText(user.getName() + " deleted " + fileMeta.getFilename());
            alert.showAndWait();
        });
    }
}
