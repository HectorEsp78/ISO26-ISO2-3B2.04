package es.uclm.esi.iso2.cliente.logica;

import es.uclm.esi.iso2.comun.dominio.CampoPaciente;
import es.uclm.esi.iso2.comun.dominio.Paciente;

import java.util.List;

/**
 * Resultado de una operación de la pantalla de pacientes.
 *
 * @param exito           {@code true} si la operación se ha hecho
 * @param paciente        paciente resultante (solo si hay éxito)
 * @param mensaje         mensaje para el usuario, de éxito o de error
 * @param camposConError  campos del formulario que hay que señalar (vacío si no hay ninguno)
 * @param sinConexion     {@code true} si ha fallado por falta de conexión con el servidor (FA-08)
 */
public record Resultado(boolean exito, Paciente paciente, String mensaje, List<CampoPaciente> camposConError,
        boolean sinConexion) {

    public Resultado {
        camposConError = List.copyOf(camposConError);
    }

    public static Resultado correcto(Paciente paciente, String mensaje) {
        return new Resultado(true, paciente, mensaje, List.of(), false);
    }

    public static Resultado error(String mensaje, List<CampoPaciente> camposConError) {
        return new Resultado(false, null, mensaje, camposConError, false);
    }

    public static Resultado sinConexion(String mensaje) {
        return new Resultado(false, null, mensaje, List.of(), true);
    }
}
