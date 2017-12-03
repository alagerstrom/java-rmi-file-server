package com.andreas.common;

import com.andreas.common.dto.FileMetaDTO;
import com.andreas.common.dto.UserDTO;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileClient extends Remote {

    void fileDownloaded(UserDTO user, FileMetaDTO fileMeta) throws RemoteException;

    void fileDeleted(UserDTO user, FileMetaDTO fileMeta) throws RemoteException;

}
