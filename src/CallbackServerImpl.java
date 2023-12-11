import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;


public class CallbackServerImpl extends UnicastRemoteObject implements CallbackServerInterface {
    private HashMap<String, Usuario> clientesConectados;
    private BDAdmin bd;

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
        System.out.println("Se procede a iniciar sesion del usuario " + username);
        if (bd.iniciarSesion(username, contrasena)) {
            ArrayList<String> amigos = obtenerAmistades(username);
            ArrayList<Usuario> conectados = new ArrayList<>();
            for (String amigo : amigos) {
                conectados.add(clientesConectados.get(amigo));
            }
            ArrayList<String> solicitudes = obtenerSolicitudes(username);
            usuario = new Usuario(cliente, username, amigos, conectados, solicitudes);
            System.out.println("inicio de sesion correcto");
            clientesConectados.put(username, usuario);
            clienteConectado(usuario);
        }
        return usuario;
    }

    @Override
    public Usuario registrarUsuario(CallbackClientInterface cliente, String username, String contrasena) throws RemoteException {
        // Comprobamos que Cliente no es null
        if (cliente == null) {
            System.out.println("No ha sido posible registrar el cliente: null");
            return null;
        }
        Usuario usuario = null;
        if (bd.registrarse(username, contrasena, "rmi://localhost:1099/" + username)) {
            usuario = new Usuario(cliente, username);
            clientesConectados.put(username, usuario);
            // Realiza llamadas de retorno a todos los clientes registrados.
        }
        return usuario;
    }

    @Override
    public boolean eliminarUsuario(Usuario usuario, String contrasena) throws RemoteException {
        if (usuario == null) {
            System.out.println("No ha sido posible registrar el cliente: null");
            return false;
        }
        if (bd.borrarUsuario(usuario.getUsername(), contrasena)) {
            clientesConectados.remove(usuario.getUsername());
            clienteDesconectado(usuario);
            return true;
        } else return false;

    }

    @Override
    public void cerrarSesion(Usuario usuario) throws RemoteException {
        if (usuario == null) {
            System.out.println("No ha sido posible registrar el cliente: null");
            return;
        }
        usuario.desconectar();
        clientesConectados.remove(usuario.getUsername());
        clienteDesconectado(usuario);
    }

    @Override
    public Usuario aceptarAmistad(Usuario usuario1, String username2) throws RemoteException {
        Usuario usuario2 = null;
        if (bd.aceptarPeticion(username2, usuario1.getUsername())) {
            usuario2 = clientesConectados.get(username2);
            if (usuario2 != null) {
                usuario1.anadirAmigo(usuario2);
                usuario2.anadirAmigo(usuario1);
                //clienteConectado(usuario1);
                //clienteConectado(usuario2);
            } else {
                usuario1.anadirAmigo(username2);
                usuario2 = new Usuario(username2, false);
            }
        }
        return usuario2;
    }

    @Override
    public boolean pedirAmistad(String usuario1, String usuario2) throws RemoteException {
        if (bd.enviarPeticion(usuario1, usuario2)) {
            Usuario usuario = clientesConectados.get(usuario1);
            if (usuario != null) {
                usuario.anadirSolicitud(usuario2);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean rechazarAmistad(String usuario1, String usuario2) throws RemoteException {
        if (bd.rechazarAmistad(usuario1, usuario2)) {
            Usuario usuario = clientesConectados.get(usuario1);
            if (usuario != null) {
                usuario.eliminarSolicitud(usuario2);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean eliminarAmigo(Usuario usuario1, String username2) throws RemoteException {
        if (bd.borrarAmigo(usuario1.getUsername(), username2)) {
            usuario1.eliminarAmigo(username2);
            //clienteDesconectado(usuario1);
            Usuario usuario2 = clientesConectados.get(username2);
            if (usuario2 != null) {
                usuario2.eliminarAmigo(usuario1.getUsername());
                //clienteDesconectado(usuario2);
            }
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<String> obtenerAmistades(String usuario) throws RemoteException {
        ArrayList<String> amigos = new ArrayList<>(bd.obtenerAmistades(usuario));
        return amigos;
    }

    @Override
    public ArrayList<String> obtenerSolicitudes(String usuario) throws RemoteException {
        ArrayList<String> solicitudes = new ArrayList<>(bd.obtenerPeticiones(usuario));
        return solicitudes;
    }

    @Override
    public String obtenerDireccion(String usuario) throws RemoteException {
        return null;
    }

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
