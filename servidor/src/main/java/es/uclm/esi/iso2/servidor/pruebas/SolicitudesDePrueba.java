package es.uclm.esi.iso2.servidor.pruebas;

import java.util.List;
import java.util.Optional;

/**
 * Lo que CU10 necesita de CU06 «Solicitudes de prueba».
 *
 * <p>Es una interfaz provisional hasta que CU06 publique la suya: cuando exista, basta una clase
 * que adapte {@code IServicioSolicitudes} a esta interfaz y se pasa en {@code Servidor.crear}
 * en lugar de {@link #SIN_CU06}. Hay que acordar con CU06 los estados de la solicitud y
 * quién guarda el id de la cita (DEC-077).</p>
 */
public interface SolicitudesDePrueba {

    /** Solicitud con ese id, si existe. */
    Optional<SolicitudDePrueba> buscar(long idSolicitud);

    /** Solicitudes con cita asignada de un médico; {@code dni} es opcional ({@code null} = todas). */
    List<SolicitudDePrueba> conCitaDelMedico(String idMedico, String dni);

    /** Pasa la solicitud a {@code REALIZADA}. */
    void marcarRealizada(long idSolicitud);

    /** Pasa la solicitud a {@code CANCELADA}. */
    void marcarCancelada(long idSolicitud);

    /** Implementación vacía mientras CU06 no exista: no hay solicitudes. */
    SolicitudesDePrueba SIN_CU06 = new SolicitudesDePrueba() {
        @Override
        public Optional<SolicitudDePrueba> buscar(long idSolicitud) {
            return Optional.empty();
        }

        @Override
        public List<SolicitudDePrueba> conCitaDelMedico(String idMedico, String dni) {
            return List.of();
        }

        @Override
        public void marcarRealizada(long idSolicitud) {
            throw new IllegalStateException("CU06 no está conectado");
        }

        @Override
        public void marcarCancelada(long idSolicitud) {
            throw new IllegalStateException("CU06 no está conectado");
        }
    };
}
