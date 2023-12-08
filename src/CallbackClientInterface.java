/**
 * This is a remote interface for illustrating RMI
 * client callback.
 *
 * @author M. L. Liu
 */

public interface CallbackClientInterface
        extends java.rmi.Remote {

    public void amigoConectado(Usuario usuario)
            throws java.rmi.RemoteException;

    public void amigoDesconectado(Usuario usuario)
            throws java.rmi.RemoteException;


} // end interface
