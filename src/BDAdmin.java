import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;

public class BDAdmin {
    private static final String URL = "jdbc:sqlite:C:/Users/sergi/OneDrive/Documentos/Clase/CoDis/Practicas/P2PChatServer/res/chat.db";
    private Connection connection;

    public BDAdmin() {
        conectar();
    }

    public void conectar() {
        try {
            // Establecer la conexión a la base de datos
            connection = DriverManager.getConnection(URL);
            System.out.println("Conexión establecida a la base de datos.");
        } catch (SQLException e) {
            System.out.println("Error al conectarse a la bd: " + e.getMessage());
        }
    }

    public void desconectar() {
        try {
            // Cerrar la conexión a la base de datos
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexion: " + e.getMessage());
        }
    }

    public boolean iniciarSesion(String username, String contrasena) {
        try {
            String sql = "SELECT * FROM USUARIO WHERE username = ? AND contrasena = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, contrasena);
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al iniciar sesion: " + e.getMessage());
            return false;
        }
    }

    public boolean registrarse(String username, String contrasena, String direccionObjetoRemoto) {
        try {
            String sql = "INSERT INTO USUARIO (username, contrasena, direccion_objeto_remoto) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, contrasena);
                statement.setString(3, direccionObjetoRemoto);
                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean borrarUsuario(String username, String contrasena) {
        try {
            // Preparar la declaración SQL para borrar el usuario con verificación de contraseña
            String sql = "DELETE FROM USUARIO WHERE username = ? AND contrasena = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Establecer los valores de los parámetros
                statement.setString(1, username);
                statement.setString(2, contrasena);

                // Ejecutar la consulta y obtener el número de filas afectadas
                int filasAfectadas = statement.executeUpdate();

                // Devolver true si se eliminó al menos una fila, indicando éxito
                return filasAfectadas > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error al borrar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean buscarUsuario(String username) {
        try {
            String sql = "SELECT * FROM USUARIO WHERE username = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<String> obtenerAmistades(String usuario) {
        try {
            String sql = "SELECT * FROM AMISTAD WHERE pendiente = 0 AND (usuario_que_pide = ? OR usuario_que_recibe = ?)";
            ArrayList<String> amigos = new ArrayList<>();
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, usuario);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String u1 = resultSet.getString("usuario_que_pide");
                        String u2 = resultSet.getString("usuario_que_recibe");
                        if (!u1.equalsIgnoreCase(usuario))
                            amigos.add(u1);
                        else amigos.add(u2);
                    }
                    return amigos;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
            return null;
        }
    }

    public ArrayList<String> obtenerPeticiones(String usuario) {
        String sql = "SELECT usuario_que_pide FROM AMISTAD WHERE pendiente = 0 AND usuario_que_recibe = ?";
        ArrayList<String> solicitudes = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, usuario);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    solicitudes.add(resultSet.getString("usuario_que_pide"));
                }
                return solicitudes;
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
            return null;
        }
    }

    public boolean enviarPeticion(String origen, String destino) {
        String sql = "INSERT INTO AMISTAD (usuario_que_pide, usuario_que_recibe, pendiente) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, origen);
            statement.setString(2, destino);
            statement.setInt(3, 1);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al pedir amistad: " + e.getMessage());
            return false;
        }
    }

    public boolean aceptarPeticion(String origen, String destino) {
        String sql = "UPDATE AMISTAD SET pendiente = 0 WHERE usuario_que_pide = ? AND usuario_que_recibe = ? AND pendiente = 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, origen);
            statement.setString(2, destino);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al aceptar amistad: " + e.getMessage());
            return false;
        }
    }

    public boolean borrarAmigo(String origen, String destino) {
        String sql = "DELETE FROM AMISTAD WHERE ((usuario_que_pide = ? AND usuario_que_recibe = ?) OR (usuario_que_pide = ? AND usuario_que_recibe = ?)) AND pendiente=0";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, origen);
            statement.setString(2, destino);
            statement.setString(3, destino);
            statement.setString(4, origen);

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar amistad: " + e.getMessage());
            return false;
        }
    }

    public boolean rechazarAmistad(String origen, String destino) {
        String sql = "DELETE FROM AMISTAD WHERE usuario_que_pide = ? AND usuario_que_recibe = ? AND pendiente = 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, origen);
            statement.setString(2, destino);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error al rechazar peticion de amistad: " + e.getMessage());
            return false;
        }
    }
}
