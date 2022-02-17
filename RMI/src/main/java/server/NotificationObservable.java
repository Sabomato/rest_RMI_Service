package server;

import client.NotificationClientInterface;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class NotificationObservable extends UnicastRemoteObject implements NotificationService {

    public static String SERVICE_NAME ="NotificationService";
    private final ArrayList<NotificationClientInterface> observers;
    boolean registered;
    //public FilterRMI filterRMI;

    public NotificationObservable() throws RemoteException {
        super();
        //filterRMI = new FilterRMI();
        observers = new ArrayList<>();
        registered =false;

    }



    public void connectToRMI(){
        try {

            Naming.rebind("rmi://localhost/" + SERVICE_NAME,this);
            registered = true;
        } catch (MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void addObserver(NotificationClientInterface observer) throws RemoteException {
        observers.add(observer);
    }

    @Override
    public void removeObserver(NotificationClientInterface observer) throws RemoteException {
        observers.remove(observer);
    }
    public void notifyObservers() {

        for (int i = 0; i < observers.size();i++) {
          try {
                observers.get(i).notification();
            } catch (IOException e) {
              //System.err.println("Unable to contact" + ((UnicastRemoteObject)observers.get(i)).getRef());
                observers.remove(observers.get(i));
                System.err.println("User left unexpectedly" );
            }
       }

    }
    /*
    class FilterRMI extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            notifyObservers();
        }
    }
*/

}



