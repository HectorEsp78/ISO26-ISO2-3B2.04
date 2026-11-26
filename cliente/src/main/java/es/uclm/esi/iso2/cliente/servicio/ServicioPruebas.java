package es.uclm.esi.iso2.cliente.servicio;

import es.uclm.esi.iso2.comun.dominio.FiltroPruebas;
import es.uclm.esi.iso2.comun.dominio.Incidencia;
import es.uclm.esi.iso2.comun.dominio.NuevaIncidencia;
import es.uclm.esi.iso2.comun.dominio.Prueba;
import es.uclm.esi.iso2.comun.dominio.ResumenPrueba;
import es.uclm.esi.iso2.comun.dominio.SolicitudPendiente;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Operaciones de CU10 que el cliente pide al servidor (DEC-078 y DEC-080). Todas llevan el
 * identificador del médico y lanzan {@link ExcepcionServicioPruebas} si el servidor las rechaza o
 * no se puede contactar con él.
 */
public interface ServicioPruebas {

    /** Solicitudes del médico con cita programada y sin prueba; {@code dni} vacío = todas (flujo 6.1). */
    List<SolicitudPendiente> listarPendientes(String idMedico, String dni);

    /** Registra una prueba realizada, con sus incidencias (flujo 6.2). */
    Prueba registrarRealizacion(String idMedico, long idSolicitud, LocalDateTime fechaHora, String observaciones,
            List<NuevaIncidencia> incidencias);

    /** Registra que la prueba no se ha realizado (flujo 6.3). */
    Prueba registrarCancelacion(String idMedico, long idSolicitud, String motivo);

    /** Añade una incidencia a una prueba ya registrada (flujo 6.5). */
    Incidencia anadirIncidencia(String idMedico, long idPrueba, NuevaIncidencia incidencia);

    /** Busca pruebas del médico (flujo 6.4). */
    List<ResumenPrueba> buscar(String idMedico, FiltroPruebas filtro);

    /** Prueba completa, con sus incidencias (flujo 6.6). */
    Prueba obtener(String idMedico, long idPrueba);
}
