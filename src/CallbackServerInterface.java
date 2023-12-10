import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * This is a remote interface for illustrating RMI
 * client callback.
 *
 * @author Sócrates Agudo Torrado
 */

public interface CallbackServerInterface extends Remote {
    public Usuario iniciarSesion(CallbackClientInterface cliente, String username, String contrasena) throws RemoteException;

    public Usuario registrarUsuario(CallbackClientInterface cliente, String username, String contrasena) throws RemoteException;

    public boolean eliminarUsuario(Usuario usuario, String contrasena) throws RemoteException;

    public void cerrarSesion(Usuario usuario) throws RemoteException;

    public Usuario aceptarAmistad(Usuario usuario1, String username2) throws RemoteException;

    public boolean pedirAmistad(String usuario1, String usuario2) throws RemoteException;

    public boolean rechazarAmistad(String usuario1, String usuario2) throws RemoteException;

    public boolean eliminarAmigo(Usuario usuario1, String username2) throws RemoteException;

    public ArrayList<String> obtenerAmistades(String usuario) throws RemoteException;

    public ArrayList<String> obtenerSolicitudes(String usuario) throws RemoteException;

    public String obtenerDireccion(String usuario) throws RemoteException;

}
