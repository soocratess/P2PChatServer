import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.net.*;
import java.io.*;

/**
 * Esta clase representa el servidor de objetos para un objeto distribuido
 * de la clase Callback, que implementa la interfaz remota CallbackInterface.
 * @author M. L. Liu
 */

public class CallbackServer  {
  public static void main(String args[]) {
    // Preparación para leer desde la entrada estándar
    InputStreamReader is = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(is);
    String portNum, registryURL;
    try{
      System.out.println(
              "Enter the RMIregistry port number:");
      // Lee el número de puerto del RMIRegistry desde el usuario
      portNum = (br.readLine()).trim();
      int RMIPortNum = Integer.parseInt(portNum);

      // Inicia o recupera el registro RMI en el puerto especificado
      startRegistry(RMIPortNum);

      // Crea una instancia del objeto CallbackServerImpl
      CallbackServerImpl exportedObj = new CallbackServerImpl();

      // Crea una URL de registro RMI para el objeto
      registryURL = "rmi://localhost:" + portNum + "/callback";

      // Vincula el objeto remoto en la URL del registro
      Naming.rebind(registryURL, exportedObj);

      System.out.println("Callback Server ready.");
    }// end try
    catch (Exception re) {
      System.out.println(
              "Exception in HelloServer.main: " + re);
    } // end catch
  } // end main

  // Este método inicia un registro RMI en el localhost si no existe
  // todavía en el puerto especificado.
  private static void startRegistry(int RMIPortNum) throws RemoteException{
    try {
      // Intenta obtener el registro existente
      Registry registry = LocateRegistry.getRegistry(RMIPortNum);
      registry.list();
      // Esta llamada arrojará una excepción si el registro no existe aún.
    }
    catch (RemoteException e) {
      // No hay un registro válido en ese puerto, por lo que crea uno nuevo.
      Registry registry = LocateRegistry.createRegistry(RMIPortNum);
    }
  } // end startRegistry
} // end class
