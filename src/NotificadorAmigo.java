import java.rmi.RemoteException;

public class NotificadorAmigo extends Thread {
    private Usuario origen;
    private Usuario destino;
    private boolean amistad;

    public NotificadorAmigo(Usuario origen, Usuario destino, boolean amistad) {
        this.origen = origen;
        this.destino = destino;
        this.amistad = amistad;
    }

    @Override
    public void run() {
        try {
            if (amistad)
                nuevoAmigo();
            else amigoEliminado();
        } catch (RemoteException e) {
            System.out.println("Error al notificar amigos: " + e.getMessage());
            return;
        }
    }

    private void nuevoAmigo() throws RemoteException {
        if (destino != null && origen != null) {
            if (destino.getCliente() != null)
                destino.getCliente().nuevoAmigo(origen);
        }
    }

    private void amigoEliminado() throws RemoteException {
        if (destino != null && origen != null) {
            if (destino.getCliente() != null)
                destino.getCliente().amigoBorrado(origen);
        }
    }

}
