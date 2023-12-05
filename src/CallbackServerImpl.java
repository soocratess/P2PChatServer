import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

/**
 * Esta clase implementa la interfaz remota CallbackServerInterface.
 *
 * @author Sócrates Agudo Torrado
 */

public class CallbackServerImpl extends UnicastRemoteObject implements CallbackServerInterface {
    private Hashtable<String, CallbackClientInterface> clientes;
    private Map<String, Set<String>> amistades; // Estructura para almacenar grupos de amistad


    /**
     * Constructor de la clase CallbackServerImpl.
     * Este constructor inicializa un objeto de la clase CallbackServerImpl y crea un nuevo mapa de clientes utilizando Hashtable.
     * Además, inicializa la estructura para almacenar grupos de amistad.
     *
     * @throws RemoteException Si ocurre un error relacionado con la comunicación remota.
     */
    public CallbackServerImpl() throws RemoteException {
        super();
        clientes = new Hashtable<>();
        amistades = new HashMap<>();
    }

    /**
     * Método de saludo remoto.
     * Este método permite que el servidor responda con un mensaje de saludo "hello" cuando se invoca de manera remota.
     *
     * @return Un mensaje de saludo "hello".
     * @throws RemoteException Si ocurre un error de comunicación remota.
     */
    public String sayHello() throws java.rmi.RemoteException {
        return ("hello");
    }


    /**
     * Registra un cliente en el servidor si no existe previamente en el mapa de clientes.
     *
     * @param cliente Objeto que implementa la interfaz CallbackClientInterface y representa al cliente a registrar.
     * @throws RemoteException Si ocurre un error de comunicación remota.
     */
    public synchronized void registrarCliente(CallbackClientInterface cliente) throws RemoteException {
        // Comprobamos que Cliente no es null
        if (cliente == null || cliente.getClientId() == null) {
            System.out.println("No ha sido posible registrar el cliente: null");
            return;
        }

        // Verifica si el cliente ya está registrado en el mapa de clientes por su ID único.
        if (!clientes.containsKey(cliente.getClientId())) {
            // Si el cliente no está registrado, lo agrega al mapa de clientes.
            clientes.put(cliente.getClientId(), cliente);
            crearGrupoAmistad(cliente.getClientId());
            System.out.println("Registrado nuevo cliente con ID: " + cliente.getClientId());

            // Realiza llamadas de retorno a todos los clientes registrados.
            hacerCallbacks();
        }
    }


    /**
     * Suprime un cliente registrado en el servidor.
     *
     * @param cliente Objeto que implementa la interfaz CallbackClientInterface y representa al cliente a suprimir.
     * @throws RemoteException Si ocurre un error de comunicación remota.
     */
    public synchronized void suprimirCliente(CallbackClientInterface cliente) throws RemoteException {
        // Comprueba que el objeto Cliente no sea nulo y que tenga un ID válido.
        if (cliente == null || cliente.getClientId() == null) {
            System.out.println("No ha sido posible suprimir el cliente: null");
            return;
        }

        // Intenta eliminar al cliente del mapa de clientes por su ID.
        if (clientes.remove(cliente.getClientId()) != null) {
            // Si se encuentra y se elimina correctamente, muestra un mensaje de cliente suprimido.
            System.out.println("Unregistered client with ID: " + cliente.getClientId());
        } else {
            // Si no se encuentra en el mapa, muestra un mensaje de que el ID del cliente no se encontró.
            System.out.println("Unregister: Client ID not found.");
        }
    }

    /**
     * Obtiene y muestra la cantidad de clientes conectados en el servidor.
     *
     * @param cliente Objeto que implementa la interfaz CallbackClientInterface y representa al cliente que solicita la cantidad de clientes conectados.
     *                En el futuro se necesitará para obtener su lista de amigos
     * @throws RemoteException Si ocurre un error de comunicación remota.
     */
    public synchronized void getCantidadClientes(CallbackClientInterface cliente) throws RemoteException {
        // Comprueba que el objeto Cliente no sea nulo y que tenga un ID válido.
        if (cliente == null || cliente.getClientId() == null) {
            System.out.println("No ha sido posible obtener la cantidad de clientes: Cliente nulo o ID nulo");
            return;
        }

        // Verifica si el mapa de clientes es nulo.
        if (this.clientes == null) {
            System.out.println("Error: El mapa de clientes es nulo");
            return;
        }

        // Muestra la cantidad de usuarios conectados en el servidor.
        System.out.println("Cantidad de usuarios conectados: " + this.clientes.size());
    }


    /**
     * Obtiene y muestra la lista de usuarios conectados en el servidor (las claves del mapa de clientes).
     *
     * @param cliente Objeto que implementa la interfaz CallbackClientInterface y representa al cliente que solicita la lista de usuarios conectados.
     * @throws RemoteException Si ocurre un error de comunicación remota.
     */
    public synchronized void getListaUsuarios(CallbackClientInterface cliente) throws RemoteException {
        // Comprueba que el objeto Cliente no sea nulo y que tenga un ID válido.
        if (cliente == null || cliente.getClientId() == null) {
            System.out.println("No ha sido posible obtener la lista de usuarios: Cliente nulo o ID nulo");
            return;
        }

        // Verifica si el mapa de clientes es nulo.
        if (this.clientes == null) {
            System.out.println("Error: El mapa de clientes es nulo");
            return;
        }

        // Muestra la lista de usuarios conectados en el servidor (las claves del mapa de clientes).
        System.out.println("Lista de usuarios conectados: ");
        for (String userId : clientes.keySet()) {
            System.out.println("\t" + userId);
        }
    }

    /**
     * Envía una dirección de contacto desde un usuario (usuarioEnvia) a otro usuario (usuarioEnviar) a través del servidor.
     * Esta función verifica si los objetos pasados como parámetros son nulos y luego envía la dirección de contacto al usuario destinatario
     * utilizando el método de callback del usuario destinatario.
     *
     * @param usuarioEnvia   Objeto que implementa la interfaz CallbackClientInterface y representa al usuario que envía la dirección de contacto.
     * @param usuarioEnviar  El nombre del usuario destinatario al que se enviará la dirección de contacto.
     * @param direccionObjeto La dirección de contacto que se enviará.
     * @throws RemoteException Si ocurre un error de comunicación remota.
     */
    public synchronized void enviarContacto(CallbackClientInterface usuarioEnvia, String usuarioEnviar, String direccionObjeto) throws java.rmi.RemoteException {

        System.out.println("------------ Enviando direccion ------------");

        // Comprueba si los objetos pasados como parámetros son nulos
        if (usuarioEnvia == null || usuarioEnviar == null || direccionObjeto == null){
            System.out.println("Error al enviar contacto: null");
            return;
        }

        System.out.println("Enviando dirección de " + usuarioEnvia.getClientId() + " a " + usuarioEnviar);

        // Invoca el método de callback del usuario destinatario para enviar la dirección de contacto.
        CallbackClientInterface destinatario = clientes.get(usuarioEnviar);
        if (destinatario != null) {
            destinatario.notifyMe(direccionObjeto);
            System.out.println("Dirección enviada correctamente.");
        } else {
            System.out.println("Usuario destinatario no encontrado en el servidor.");
        }

        System.out.println("------------ Dirección enviada ------------");
    }

    /**
     * Crea un nuevo grupo de amistad con un nombre dado.
     *
     * @param groupName El nombre del grupo de amistad a crear.
     */
    public void crearGrupoAmistad(String groupName) {
        // Verificar si el grupo ya existe y, si no, crearlo.
        if (!amistades.containsKey(groupName)) {
            amistades.put(groupName, new HashSet<>());
        }
    }

    /**
     * Agrega una amistad entre dos usuarios al grupo de amistad existente.
     *
     * @param usuario1 El primer usuario a agregar como amigo.
     * @param usuario2 El segundo usuario a agregar como amigo.
     */
    public void agregarAmistad(String usuario1, String usuario2) {
        // Verificar si el grupo de amistad existe.
        if (amistades.containsKey(usuario1) && amistades.containsKey(usuario2)) {

            // Añadir amigo al usuario1
            Set<String> groupMembers1 = amistades.get(usuario1);
            groupMembers1.add(usuario2);

            // Añadir amigo al usuario2
            Set<String> groupMembers2 = amistades.get(usuario2);
            groupMembers2.add(usuario1);
        }
    }


    /**
     * Obtiene los miembros de un grupo de amistad específico.
     *
     * @param groupName El nombre del grupo de amistad del que se obtendrán los miembros.
     * @return Un conjunto de IDs de clientes que son miembros del grupo de amistad especificado.
     */
    public Set<String> obtenerAmistades(String groupName) {
        // Verificar si el grupo de amistad existe.
        if (amistades.containsKey(groupName)) {
            return amistades.get(groupName);
        }
        return Collections.emptySet(); // Si el grupo no existe, devolvemos un conjunto vacío.
    }



    /**
     * Realiza llamadas de retorno a todos los clientes registrados en el servidor.
     * Para cada cliente registrado, invoca su método de callback para proporcionar información sobre la cantidad de clientes registrados.
     *
     * @throws RemoteException Si ocurre un error de comunicación remota.
     */
    private synchronized void hacerCallbacks() throws java.rmi.RemoteException {
        System.out.println("**************************************\n" + "Callbacks iniciados ---");

        // Itera sobre cada entrada en el mapa de clientes
        for (Map.Entry<String, CallbackClientInterface> clientEntry : clientes.entrySet()) {
            String clientId = clientEntry.getKey();
            CallbackClientInterface client = clientEntry.getValue();

            System.out.println("Realizando llamada de retorno al cliente con ID: " + clientId);

            // Invoca el método de callback en cada cliente para proporcionar información sobre la cantidad de clientes registrados.
            client.notifyMe("Número de clientes registrados = " + clientes.size());
        }

        System.out.println("********************************\n" + "Llamadas de retorno completadas ---");
    }

} // end CallbackServerImpl class
