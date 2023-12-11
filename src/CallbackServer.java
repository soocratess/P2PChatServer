import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Esta clase representa el servidor de objetos para un objeto distribuido
 * de la clase Callback, que implementa la interfaz remota CallbackInterface.
 * @author M. L. Liu
 */

public class CallbackServer {
    private static final int RMIPORT = 1099;
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
        }// end try
        catch (Exception re) {
            System.out.println(re.getMessage());
        } // end catch
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
