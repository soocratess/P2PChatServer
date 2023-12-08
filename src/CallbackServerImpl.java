import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Esta clase implementa la interfaz remota CallbackServerInterface.
 *
 * @author Sócrates Agudo Torrado
 */

public class CallbackServerImpl extends UnicastRemoteObject implements CallbackServerInterface {
    private HashMap<String, Usuario> clientesConectados;
    private BDAdmin bd;
    private static final String URL = "rmi://localhost:1099/";

    /**
     * Constructor de la clase CallbackServerImpl.
     * Este constructor inicializa un objeto de la clase CallbackServerImpl y crea un nuevo mapa de clientes utilizando Hashtable.
     * Además, inicializa la estructura para almacenar grupos de amistad.
     *
     * @throws RemoteException Si ocurre un error relacionado con la comunicación remota.
     */
    public CallbackServerImpl() throws RemoteException {
        super();
        clientesConectados = new HashMap<>();
        bd = new BDAdmin();
    }

    @Override
    public Usuario iniciarSesion(CallbackClientInterface cliente, String username, String contrasena) throws RemoteException {
        if (cliente == null) {
            System.out.println("No ha sido posible registrar el cliente: null");
            return null;
        }
        Usuario usuario = null;
        if (bd.iniciarSesion(username, contrasena)) {
            String url = URL + username;
            ArrayList<String> amigos = obtenerAmistades(username);
            ArrayList<Usuario> conectados = new ArrayList<>();
            for (String amigo : amigos) {
                conectados.add(clientesConectados.get(amigo));
            }
            usuario = new Usuario(cliente, username, url, amigos, conectados);

            clientesConectados.put(username, usuario);
            clienteConectado(usuario);
        }
        return usuario;
    }

    /**
     * Registra un cliente en el servidor si no existe previamente en el mapa de clientes.
     *
     * @param cliente Objeto que implementa la interfaz CallbackClientInterface y representa al cliente a registrar.
     * @throws RemoteException Si ocurre un error de comunicación remota.
     */
    public Usuario registrarUsuario(CallbackClientInterface cliente, String username, String contrasena) {
        // Comprobamos que Cliente no es null
        if (cliente == null) {
            System.out.println("No ha sido posible registrar el cliente: null");
            return null;
        }
        Usuario usuario = null;
        if (bd.registrarse(username, contrasena, "")) {
            String url = URL + username;
            usuario = new Usuario(cliente, username, url, new ArrayList<>(), new ArrayList<>());
            clientesConectados.put(username, usuario);
            // Realiza llamadas de retorno a todos los clientes registrados.
        }
        return usuario;
    }

    @Override
    public void eliminarUsuario(Usuario usuario, String contrasena) throws RemoteException {
        if (usuario == null) {
            System.out.println("No ha sido posible registrar el cliente: null");
            return;
        }
        bd.borrarUsuario(usuario.getUsername(), contrasena);
        clientesConectados.remove(usuario.getUsername());
        clienteDesconectado(usuario);

    }

    @Override
    public void cerrarSesion(Usuario usuario) throws RemoteException {
        if (usuario == null) {
            System.out.println("No ha sido posible registrar el cliente: null");
            return;
        }
        clientesConectados.remove(usuario.getUsername());
        clienteDesconectado(usuario);
    }

    @Override
    public void aceptarAmistad(Usuario usuario1, Usuario usuario2) throws RemoteException {
        if (bd.aceptarPeticion(usuario1.getUsername(), usuario2.getUsername())) {
            usuario1.anadirAmigo(usuario2);
            usuario2.anadirAmigo(usuario1);
            clienteConectado(usuario1);
            clienteConectado(usuario2);
        }
    }

    @Override
    public void pedirAmistad(String usuario1, String usuario2) {
        if (bd.enviarPeticion(usuario1, usuario2)) {

        }
    }

    @Override
    public void rechazarAmistad(String usuario1, String usuario2) {
        if (bd.rechazarAmistad(usuario1, usuario2)) {

        }
    }

    @Override
    public void eliminarAmigo(Usuario usuario1, Usuario usuario2) throws RemoteException {
        if (bd.borrarAmigo(usuario1.getUsername(), usuario2.getUsername())) {
            usuario1.eliminarAmigo(usuario2);
            usuario2.eliminarAmigo(usuario1);
            clienteDesconectado(usuario1);
            clienteDesconectado(usuario2);
        }
    }

    /**
     * Obtiene los miembros de un grupo de amistad específico.
     *
     * @param usuario El nombre del grupo de amistad del que se obtendrán los miembros.
     * @return Un conjunto de IDs de clientes que son miembros del grupo de amistad especificado.
     */
    public ArrayList<String> obtenerAmistades(String usuario) {
        ArrayList<String> amigos = new ArrayList<>(bd.obtenerAmistades(usuario));
        return amigos;
    }

    @Override
    public String obtenerDireccion(String usuario) {
        return null;
    }


    /**
     * Realiza llamadas de retorno a todos los clientes registrados en el servidor.
     * Para cada cliente registrado, invoca su método de callback para proporcionar información sobre la cantidad de clientes registrados.
     *
     * @throws RemoteException Si ocurre un error de comunicación remota.
     */
    private synchronized void clienteDesconectado(Usuario usuario) throws RemoteException {
        for (Usuario amigo : usuario.getAmigosConectados()) {
            amigo.getCliente().amigoConectado(usuario);
        }

    }

    private synchronized void clienteConectado(Usuario usuario) throws RemoteException {
        for (Usuario amigo : usuario.getAmigosConectados()) {
            amigo.getCliente().amigoDesconectado(usuario);
        }

    }

} // end CallbackServerImpl class
