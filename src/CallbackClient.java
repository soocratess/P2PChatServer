import java.io.*;
import java.rmi.*;

/**
 * Esta clase representa el cliente para un objeto distribuido de la clase
 * CallbackServerImpl, que implementa la interfaz remota CallbackServerInterface.
 * También acepta llamadas de retorno desde el servidor.
 *
 * @author M. L. Liu
 */

public class CallbackClient {
  public static void main(String args[]) {
    try {


      int RMIPort;
      String hostName;
      String nombreUsuario;

      InputStreamReader is =
              new InputStreamReader(System.in);
      BufferedReader br = new BufferedReader(is);

      // Captura el nombre del host del RMIRegistry desde el usuario
      System.out.println("Enter the RMIRegistry host name:");
      hostName = br.readLine();

      // Captura el número de puerto del RMIRegistry desde el usuario
      System.out.println("Enter the RMIregistry port number:");
      String portNum = br.readLine();
      RMIPort = Integer.parseInt(portNum);

      // Captura la duración en segundos durante la cual el cliente se mantendrá registrado
      System.out.println("Enter how many seconds to stay registered:");
      String timeDuration = br.readLine();
      int time = Integer.parseInt(timeDuration);

      // Construye la URL de registro RMI
      String registryURL = "rmi://localhost:" + portNum + "/callback";

      // Busca el objeto remoto y lo convierte en un objeto de interfaz
      CallbackServerInterface h = (CallbackServerInterface)Naming.lookup(registryURL);
      System.out.println("Lookup completed " );

      // Muestra el mensaje del servidor
      System.out.println("Server said " + h.sayHello());

      // Crea una instancia de CallbackClientImpl
      System.out.println("Introduce el nombre de usuario:");
      nombreUsuario = br.readLine();
      CallbackClientInterface callbackObj = new CallbackClientImpl(nombreUsuario);

      // Registra el cliente para llamadas de retorno
      h.registrarCliente(callbackObj);
      System.out.println("Registered for callback.");

      try {
        // Espera durante el tiempo especificado antes de continuar
        Thread.sleep(time * 1000);
      }
      catch (InterruptedException ex){ // Interrupción durante la espera
      }

      // Anula el registro del cliente para llamadas de retorno
      h.suprimirCliente(callbackObj);
      System.out.println("Unregistered for callback.");
    } // end try
    catch (Exception e) {
      System.out.println(
              "Exception in CallbackClient: " + e);
    } // end catch
  } // end main
} // end class
