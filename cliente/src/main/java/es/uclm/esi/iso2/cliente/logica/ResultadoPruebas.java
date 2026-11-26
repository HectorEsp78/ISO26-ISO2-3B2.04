package es.uclm.esi.iso2.cliente.logica;

import java.util.Optional;

/**
 * Resultado de una operación de las pantallas de pruebas (CU10).
 *
 * @param exito          {@code true} si la operación se ha hecho
 * @param valor          lo que devuelve la operación (solo si hay éxito); puede ser {@code null}
 * @param mensaje        mensaje para el usuario, de éxito o de error
 * @param campoConError  nombre del campo que hay que señalar (el del protocolo), o {@code null}
 * @param sinConexion    {@code true} si ha fallado por falta de conexión con el servidor
 * @param <T>            tipo del valor
 */
public record ResultadoPruebas<T>(boolean exito, T valor, String mensaje, String campoConError,
        boolean sinConexion) {

    public static <T> ResultadoPruebas<T> correcto(T valor, String mensaje) {
        return new ResultadoPruebas<>(true, valor, mensaje, null, false);
    }

    public static <T> ResultadoPruebas<T> error(String mensaje, String campoConError) {
        return new ResultadoPruebas<>(false, null, mensaje, campoConError, false);
    }

    public static <T> ResultadoPruebas<T> sinConexion(String mensaje) {
        return new ResultadoPruebas<>(false, null, mensaje, null, true);
    }

    public Optional<String> campo() {
        return Optional.ofNullable(campoConError);
    }
}
