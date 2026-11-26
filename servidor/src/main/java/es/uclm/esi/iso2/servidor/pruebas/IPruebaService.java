package es.uclm.esi.iso2.servidor.pruebas;

import es.uclm.esi.iso2.comun.dominio.FiltroPruebas;
import es.uclm.esi.iso2.comun.dominio.Incidencia;
import es.uclm.esi.iso2.comun.dominio.NuevaIncidencia;
import es.uclm.esi.iso2.comun.dominio.Prueba;
import es.uclm.esi.iso2.comun.dominio.ResumenPrueba;
import es.uclm.esi.iso2.comun.dominio.SolicitudPendiente;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Operaciones de CU10 «Registrar realización de prueba» (DEC-075). Todas reciben el
 * identificador del médico y solo trabajan con pacientes suyos (RN-02). Los errores se lanzan
 * como {@link PruebaException}.
 */
public interface IPruebaService {

    /** Solicitudes del médico con cita programada y sin prueba registrada; {@code dni} opcional. */
    List<SolicitudPendiente> buscarSolicitudesPendientes(String idMedico, String dni);

    /** Registra una prueba realizada y marca la cita y la solicitud como realizadas (DEC-076, DEC-077). */
    Prueba registrarRealizacion(String idMedico, long idSolicitud, LocalDateTime fechaHora, String observaciones,
            List<NuevaIncidencia> incidencias);

    /** Registra una prueba cancelada y cancela la cita y la solicitud con el mismo motivo. */
    Prueba registrarCancelacion(String idMedico, long idSolicitud, String motivo);

    /** Añade una incidencia a una prueba ya registrada. */
    Incidencia anadirIncidencia(String idMedico, long idPrueba, NuevaIncidencia incidencia);

    /** Pruebas del médico que cumplen el filtro, de la más reciente a la más antigua. */
    List<ResumenPrueba> buscarPruebas(String idMedico, FiltroPruebas filtro);

    /** Prueba completa, con sus incidencias. */
    Prueba consultarPrueba(String idMedico, long idPrueba);
}
