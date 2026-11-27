package es.uclm.esi.iso2.cliente.logica;

import java.util.List;

/**
 * Resultado de una operación de las pantallas de citas (CU13). Es como {@link Resultado} de CU12,
 * pero el valor puede ser una cita, una lista de citas o el catálogo, y los campos con error se
 * indican por su nombre (los de {@link GestorCitasCliente}).
 *
 * @param exito          {@code true} si la operación se ha hecho
 * @param valor          lo que devuelve la operación (solo si hay éxito)
 * @param mensaje        mensaje para el usuario, de éxito o de error
 * @param camposConError campos del formulario que hay que señalar (vacío si no hay ninguno)
 * @param sinConexion    {@code true} si ha fallado por falta de conexión con el servidor
 * @param <T>            tipo del valor
 */
public record ResultadoCitas<T>(boolean exito, T valor, String mensaje, List<String> camposConError,
        boolean sinConexion) {

    public ResultadoCitas {
        camposConError = List.copyOf(camposConError);
    }

    public static <T> ResultadoCitas<T> correcto(T valor, String mensaje) {
        return new ResultadoCitas<>(true, valor, mensaje, List.of(), false);
    }

    public static <T> ResultadoCitas<T> error(String mensaje, List<String> camposConError) {
        return new ResultadoCitas<>(false, null, mensaje, camposConError, false);
    }

    public static <T> ResultadoCitas<T> sinConexion(String mensaje) {
        return new ResultadoCitas<>(false, null, mensaje, List.of(), true);
    }
}
