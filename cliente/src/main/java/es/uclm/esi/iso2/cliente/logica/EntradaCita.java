package es.uclm.esi.iso2.cliente.logica;

import es.uclm.esi.iso2.comun.dominio.Recurso;
import es.uclm.esi.iso2.comun.dominio.TipoPrueba;

/**
 * Lo que el usuario ha escrito o elegido en el formulario de cita, tal cual, antes de comprobarlo.
 *
 * @param dni      DNI/NIE del paciente
 * @param tipo     tipo de prueba elegido ({@code null} si no ha elegido ninguno)
 * @param recurso  recurso elegido ({@code null} si no ha elegido ninguno)
 * @param fecha    fecha escrita como {@code dd/mm/aaaa}
 * @param hora     hora escrita como {@code hh:mm}
 * @param duracion minutos escritos; vacío para usar la duración del tipo de prueba
 */
public record EntradaCita(String dni, TipoPrueba tipo, Recurso recurso, String fecha, String hora,
        String duracion) {
}
