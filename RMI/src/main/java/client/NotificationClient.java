package client;

import server.NotificationObservable;
import server.NotificationService;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class NotificationClient extends UnicastRemoteObject implements NotificationClientInterface {

    protected NotificationClient() throws RemoteException {
    }

    @Override
    public void notification(){
        System.out.println("New request at RESTSERVICE");
    }

    public static void main(String[] args) {
        String objectURL;

        NotificationClient myNotificationService ;
        NotificationService remoteNotificationService;

        if(args.length != 1){
            System.out.println("The location of the RMI must be passed as an argument");
            System.out.println();
            return;
        }
        objectURL = "rmi://"+args[0]+"/" + NotificationObservable.SERVICE_NAME;

        try {
            remoteNotificationService = (NotificationService) Naming.lookup(objectURL);
            System.out.println("Connected to server");

            myNotificationService = new NotificationClient();

            remoteNotificationService.addObserver(myNotificationService);

        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }

    }
}


