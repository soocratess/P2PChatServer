import java.rmi.*;

/**
 * This is a remote interface for illustrating RMI
 * client callback.
 *
 * @author M. L. Liu
 */

public interface CallbackClientInterface
        extends java.rmi.Remote {
    // metodo para enviar o recibir mensajes

    // metodo para recibir clientes conectados

    public String notifyMe(String message)
            throws java.rmi.RemoteException;

    public String getClientId() throws RemoteException;


} // end interface
