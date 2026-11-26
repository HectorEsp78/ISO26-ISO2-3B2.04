package es.uclm.esi.iso2.servidor.persistencia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Abre conexiones con la base de datos. Cada operación abre su propia conexión y la cierra al
 * terminar, de modo que los hilos que atienden a distintos clientes no comparten conexiones.
 */
@FunctionalInterface
public interface FabricaConexiones {

    Connection abrir() throws SQLException;

    /** Fábrica basada en {@link DriverManager}: sirve para MySQL y para H2 en las pruebas. */
    static FabricaConexiones jdbc(String url, String usuario, String contrasena) {
        return () -> DriverManager.getConnection(url, usuario, contrasena);
    }
}
