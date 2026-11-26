package es.uclm.esi.iso2.comun.dominio;

import java.time.LocalDateTime;

/**
 * Datos que introduce el usuario para asignar una cita (DEC-063). Se llama así para no
 * confundirlo con la solicitud de CU06.
 *
 * <p>Los campos pueden ser {@code null} si el usuario no los ha rellenado: es el servidor quien
 * decide qué falta. Si la duración es {@code null} se usa la del tipo de prueba.</p>
 */
public record DatosCita(String dniPaciente, Integer idTipoPrueba, Integer idRecurso, LocalDateTime inicio,
        Integer duracionMinutos) {
}
