package server;

import client.NotificationClientInterface;

public interface NotificationService extends java.rmi.Remote{

    public void addObserver(NotificationClientInterface observer) throws java.rmi.RemoteException;
    public void removeObserver(NotificationClientInterface observer) throws java.rmi.RemoteException;

}