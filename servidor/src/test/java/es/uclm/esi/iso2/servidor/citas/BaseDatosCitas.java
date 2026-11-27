package es.uclm.esi.iso2.servidor.citas;

import es.uclm.esi.iso2.servidor.persistencia.FabricaConexiones;
import es.uclm.esi.iso2.servidor.persistencia.ScriptSql;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Base de datos H2 en memoria para las pruebas de CU13, creada con los mismos scripts que MySQL
 * (esquema y datos de ejemplo). Cada llamada crea una base de datos nueva y vacía de citas.
 */
final class BaseDatosCitas {

    private static final AtomicInteger CONTADOR = new AtomicInteger();

    private BaseDatosCitas() {
    }

    static FabricaConexiones nueva() {
        String url = "jdbc:h2:mem:citas" + CONTADOR.incrementAndGet() + ";MODE=MySQL;DB_CLOSE_DELAY=-1";
        FabricaConexiones conexiones = FabricaConexiones.jdbc(url, "sa", "");
        ScriptSql.ejecutar(conexiones, "/sql/01_esquema.sql");
        ScriptSql.ejecutar(conexiones, "/sql/02_datos.sql");
        return conexiones;
    }
}
