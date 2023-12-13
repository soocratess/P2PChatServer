import java.rmi.RemoteException;

public class NotificadorAmigos extends Thread {
    private Usuario usuario;
    private boolean conexion;

    public NotificadorAmigos(Usuario usuario, boolean conexion) {
        this.usuario = usuario;
        this.conexion = conexion;
    }

    @Override
    public void run() {
        try {
            if (conexion) {
                clienteConectado(usuario);
            } else {
                clienteDesconectado(usuario);
            }
        } catch (RemoteException e) {
            System.out.println("Error al notificar amigos: " + e.getMessage());
            return;
        }
    }

    private void clienteConectado(Usuario usuario) throws RemoteException {
        // Lógica de notificación a amigos cuando se conecta
        if (!usuario.getAmigosConectados().isEmpty())
            for (Usuario amigo : usuario.getAmigosConectados()) {
                amigo.getCliente().amigoConectado(usuario);
            }
    }

    private void clienteDesconectado(Usuario usuario) throws RemoteException {
        // Lógica de notificación a amigos cuando se desconecta
        if (!usuario.getAmigosConectados().isEmpty())
            for (Usuario amigo : usuario.getAmigosConectados()) {
                amigo.getCliente().amigoDesconectado(usuario);
            }
    }

    private void nuevoAmigo(Usuario usuario) throws RemoteException {

    }
}
