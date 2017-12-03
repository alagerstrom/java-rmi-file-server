package com.andreas.server.startup;

import com.andreas.common.Constants;
import com.andreas.server.controller.FileServerController;
import com.andreas.server.database.DatabaseException;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {
    public static void main(String[] args) {
        new Server();
    }

    private Server() {
        try {
            startRMI();
            System.out.println("File Server started successfully.");
        } catch (DatabaseException | RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void startRMI() throws DatabaseException, RemoteException, MalformedURLException {
        FileServerController controller = new FileServerController();
        try{
            LocateRegistry.getRegistry().list();
        }catch (RemoteException noRegistry){
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
        Naming.rebind(Constants.FILE_SERVER_REGISTRY_NAME, controller);

    }
}
