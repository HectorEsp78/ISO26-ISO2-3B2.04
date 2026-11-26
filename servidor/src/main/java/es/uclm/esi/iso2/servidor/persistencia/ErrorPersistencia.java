package es.uclm.esi.iso2.servidor.persistencia;

/**
 * Fallo inesperado al acceder a la base de datos. El servidor responde {@code ERROR_INTERNO}.
 */
public class ErrorPersistencia extends RuntimeException {

    public ErrorPersistencia(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
