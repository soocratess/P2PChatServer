import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * Sócrates Agudo Torrado
 * Sergio Álvarez Piñón
 * */

// Clase CallbackServerImpl:
// Esta clase implementa la interfaz CallbackServerInterface y proporciona
// la lógica de negocio para gestionar las operaciones relacionadas con la
// conexión de usuarios y la interacción entre ellos en un sistema de chat.

public class CallbackServerImpl extends UnicastRemoteObject implements CallbackServerInterface {
    // Mapa que almacena a los usuarios conectados por su nombre de usuario
    private HashMap<String, Usuario> clientesConectados;

    // Objeto para interactuar con la base de datos
    private BDAdmin bd;

    // Constructor de la clase
    public CallbackServerImpl() throws RemoteException {
        super();
        clientesConectados = new HashMap<>();
        bd = new BDAdmin();
    }

    // Método para iniciar sesión de un usuario
    @Override
    public Usuario iniciarSesion(CallbackClientInterface cliente, String username, String contrasena) throws RemoteException {
        // Verifica que el cliente no sea nulo
        if (cliente == null) {
            System.out.println("No ha sido posible registrar el cliente: null");
            return null;
        }

        Usuario usuario = null;

        // Verifica las credenciales en la base de datos
        if (bd.iniciarSesion(username, contrasena)) {
            System.out.println("Inicio de sesión exitoso, user: " + username);
            ArrayList<String> amigos = obtenerAmistades(username);
            ArrayList<Usuario> conectados = new ArrayList<>();

            // Obtiene la lista de amigos conectados
            for (String amigo : amigos) {
                conectados.add(clientesConectados.get(amigo));
            }

            ArrayList<String> solicitudes = obtenerSolicitudes(username);
            usuario = new Usuario(cliente, username, amigos, conectados, solicitudes);

            clientesConectados.put(username, usuario);
            clienteConectado(usuario);
        }
        return usuario;
    }

    // Método para registrar un nuevo usuario
    @Override
    public Usuario registrarUsuario(CallbackClientInterface cliente, String username, String contrasena) throws RemoteException {
        // Verifica que el cliente no sea nulo
        if (cliente == null) {
            System.out.println("No ha sido posible registrar el cliente: null");
            return null;
        }

        Usuario usuario = null;

        // Registra al usuario en la base de datos
        if (bd.registrarse(username, contrasena, "rmi://localhost:1099/" + username)) {
            usuario = new Usuario(cliente, username);
            clientesConectados.put(username, usuario);

            System.out.println("Registro exitoso del usuario: " + username);
        }
        return usuario;
    }

    // Método para eliminar un usuario
    @Override
    public boolean eliminarUsuario(Usuario usuario, String contrasena) throws RemoteException {
        // Verifica que el usuario no sea nulo
        if (usuario == null) {
            System.out.println("No ha sido posible registrar el cliente: null");
            return false;
        }

        // Elimina al usuario de la base de datos
        if (bd.borrarUsuario(usuario.getUsername(), contrasena)) {
            clientesConectados.remove(usuario.getUsername());
            clienteDesconectado(usuario);
            System.out.println("Borrado del usuario " + usuario.getUsername() + " exitoso");
            return true;
        } else {
            System.out.println("No ha sido posible eliminar al usuario: " + usuario.getUsername());
            return false;
        }
    }

    // Método para cerrar sesión de un usuario
    @Override
    public void cerrarSesion(Usuario usuario) throws RemoteException {
        // Verifica que el usuario no sea nulo
        if (usuario == null) {
            System.out.println("No ha sido posible registrar el cliente: null");
            return;
        }

        // Desconecta al usuario
        usuario.desconectar();
        clientesConectados.remove(usuario.getUsername());
        clienteDesconectado(usuario);
        System.out.println("Sesión cerrada exitosamente, user: " + usuario.getUsername());
    }

    // Método para aceptar una solicitud de amistad
    @Override
    public Usuario aceptarAmistad(Usuario usuario1, String username2) throws RemoteException {
        Usuario usuario2 = null;

        // Acepta la petición de amistad en la base de datos
        if (bd.aceptarPeticion(username2, usuario1.getUsername())) {
            usuario2 = clientesConectados.get(username2);
            System.out.println("Amistad entre " + usuario1.getUsername() + " y " + username2 + " registrada correctamente");
            if (usuario2 != null) {
                usuario1.anadirAmigo(usuario2);
                usuario2.anadirAmigo(usuario1);
            } else {
                usuario1.anadirAmigo(username2);
                usuario2 = new Usuario(username2, false);
            }
        }
        return usuario2;
    }

    // Método para enviar una solicitud de amistad
    @Override
    public boolean pedirAmistad(String usuario1, String usuario2) throws RemoteException {
        if (bd.enviarPeticion(usuario1, usuario2)) {
            Usuario usuario = clientesConectados.get(usuario1);
            if (usuario != null) {
                usuario.anadirSolicitud(usuario2);
            }
            System.out.println("Petición de amistad de " + usuario1 + " a " + usuario2 + " registrada correctamente");
            return true;
        }
        System.out.println("No se ha registrado la petición de amistad de " + usuario1 + " a " + usuario2);
        return false;
    }

    // Método para rechazar una solicitud de amistad
    @Override
    public boolean rechazarAmistad(String usuario1, String usuario2) throws RemoteException {
        if (bd.rechazarAmistad(usuario1, usuario2)) {
            Usuario usuario = clientesConectados.get(usuario1);
            if (usuario != null) {
                usuario.eliminarSolicitud(usuario2);
            }
            System.out.println("Petición de amistad de " + usuario1 + " a " + usuario2 + " rechazada correctamente");
            return true;
        }
        System.out.println("No se ha cancelado la petición de amistad de " + usuario1 + " a " + usuario2);
        return false;
    }

    // Método para eliminar a un amigo
    @Override
    public boolean eliminarAmigo(Usuario usuario1, String username2) throws RemoteException {
        if (bd.borrarAmigo(usuario1.getUsername(), username2)) {
            usuario1.eliminarAmigo(username2);
            Usuario usuario2 = clientesConectados.get(username2);

            if (usuario2 != null) {
                usuario2.eliminarAmigo(usuario1.getUsername());
            }
            System.out.println("Amistad entre " + usuario1.getUsername() + " y " + username2 + " eliminada correctamente");
            return true;
        }
        System.out.println("No ha podido eliminarse el amigo " + username2 + " de " + usuario1.getUsername());
        return false;
    }

    // Método para obtener la lista de amigos de un usuario
    @Override
    public ArrayList<String> obtenerAmistades(String usuario) throws RemoteException {
        System.out.println("Obteniendo amigos de " + usuario);
        ArrayList<String> amigos = new ArrayList<>(bd.obtenerAmistades(usuario));
        return amigos;
    }

    // Método para obtener la lista de solicitudes pendientes de un usuario
    @Override
    public ArrayList<String> obtenerSolicitudes(String usuario) throws RemoteException {
        System.out.println("Obteniendo solicitudes de amistad para " + usuario);
        ArrayList<String> solicitudes = new ArrayList<>(bd.obtenerPeticiones(usuario));
        return solicitudes;
    }

    // Método para obtener la dirección de un usuario (no implementado)
    @Override
    public String obtenerDireccion(String usuario) throws RemoteException {
        return null;
    }

    // Método privado para notificar a los amigos de un usuario que se ha desconectado
    private synchronized void clienteDesconectado(Usuario usuario) throws RemoteException {
        System.out.println("Se inician los callbacks a los clientes amigos de " + usuario.getUsername() + " porque se ha desconectado");
        for (Usuario amigo : usuario.getAmigosConectados()) {
            amigo.getCliente().amigoConectado(usuario);
        }
        System.out.println("Callbacks a los clientes amigos de " + usuario.getUsername() + " terminados");
    }

    // Método privado para notificar a los amigos de un usuario que se ha conectado
    private synchronized void clienteConectado(Usuario usuario) throws RemoteException {
        System.out.println("Se inician los callbacks a los clientes amigos de " + usuario.getUsername() + " porque se ha conectado");
        for (Usuario amigo : usuario.getAmigosConectados()) {
            amigo.getCliente().amigoDesconectado(usuario);
        }
        System.out.println("Callbacks a los clientes amigos de " + usuario.getUsername() + " terminados");
    }
} // end CallbackServerImpl class

