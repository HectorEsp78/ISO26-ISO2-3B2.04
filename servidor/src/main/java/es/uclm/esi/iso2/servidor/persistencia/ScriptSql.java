package es.uclm.esi.iso2.servidor.persistencia;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Ejecuta los scripts SQL del classpath ({@code /sql/...}). Lo usan las pruebas para crear el
 * esquema en H2 con el mismo script que se usa en MySQL (DEC-070).
 *
 * <p>Admite comentarios de línea ({@code --}) y sentencias separadas por {@code ;} al final de
 * línea. No admite {@code ;} dentro de un literal al final de una línea.</p>
 */
public final class ScriptSql {

    private ScriptSql() {
    }

    /** Ejecuta todas las sentencias del recurso indicado. */
    public static void ejecutar(FabricaConexiones conexiones, String recurso) {
        try (Connection conexion = conexiones.abrir(); Statement sentencia = conexion.createStatement()) {
            for (String sql : sentencias(leer(recurso))) {
                sentencia.execute(sql);
            }
        } catch (SQLException e) {
            throw new ErrorPersistencia("Error al ejecutar " + recurso, e);
        }
    }

    /** Separa el texto de un script en sentencias, sin comentarios ni líneas vacías. */
    static List<String> sentencias(String script) {
        List<String> sentencias = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        for (String linea : script.split("\\R")) {
            String limpia = linea.strip();
            if (limpia.isEmpty() || limpia.startsWith("--")) {
                continue;
            }
            actual.append(limpia).append('\n');
            if (limpia.endsWith(";")) {
                sentencias.add(actual.substring(0, actual.lastIndexOf(";")).strip());
                actual.setLength(0);
            }
        }
        if (!actual.isEmpty()) {
            sentencias.add(actual.toString().strip());
        }
        return sentencias;
    }

    private static String leer(String recurso) {
        try (InputStream entrada = ScriptSql.class.getResourceAsStream(recurso)) {
            if (entrada == null) {
                throw new IllegalArgumentException("No se encuentra el script " + recurso);
            }
            return new String(entrada.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
