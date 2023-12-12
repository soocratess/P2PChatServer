import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Sócrates Agudo Torrado
 * Sergio Álvarez Piñón
 */

// Clase CallbackServer:
// Esta clase representa el servidor que proporciona un servicio de callbacks
// a través de RMI (Remote Method Invocation).
public class CallbackServer {

    // Puerto RMI utilizado por el servidor
    private static final int RMIPORT = 1099;

    // URL del registro RMI
    private static final String REGISTRY_URL = "rmi://localhost:" + RMIPORT + "/callback";

    public static void main(String args[]) {
        try {
            // Inicia o recupera el registro RMI en el puerto especificado
            startRegistry(RMIPORT);

            // Crea una instancia del objeto CallbackServerImpl
            CallbackServerImpl exportedObj = new CallbackServerImpl();

            // Vincula el objeto remoto en la URL del registro
            Naming.rebind(REGISTRY_URL, exportedObj);

            System.out.println("Callback Server ready.");
        } catch (Exception re) {
            // Manejo de excepciones en caso de cualquier error durante la ejecución
            System.out.println(re.getMessage());
        }
    } // end main

    // Este método inicia un registro RMI en el localhost si no existe
    // todavía en el puerto especificado.
    private static void startRegistry(int RMIPortNum) throws RemoteException {
        try {
            // Intenta obtener el registro existente
            Registry registry = LocateRegistry.getRegistry(RMIPortNum);
            registry.list();
            // Esta llamada arrojará una excepción si el registro no existe aún.
        } catch (RemoteException e) {
            // No hay un registro válido en ese puerto, por lo que crea uno nuevo.
            Registry registry = LocateRegistry.createRegistry(RMIPortNum);
        }
    } // end startRegistry
} // end class

