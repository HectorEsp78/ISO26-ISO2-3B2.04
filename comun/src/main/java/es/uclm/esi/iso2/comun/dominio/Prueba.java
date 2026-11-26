package es.uclm.esi.iso2.comun.dominio;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Prueba diagnóstica registrada por un médico, realizada o cancelada (CU10, DEC-074).
 *
 * @param id                   identificador; {@code 0} mientras no se ha guardado
 * @param idSolicitud          solicitud de CU06 de la que procede; solo hay una prueba por solicitud
 * @param dniPaciente          DNI/NIE del paciente; se copia de la solicitud
 * @param idMedico             médico que registra la prueba (1 a 20 caracteres, DEC-072)
 * @param estado               {@code REALIZADA} o {@code CANCELADA}
 * @param fechaHoraRealizacion obligatoria si está {@code REALIZADA}; {@code null} si está {@code CANCELADA}
 * @param observaciones        opcional, como máximo 1000 caracteres; cadena vacía si no hay
 * @param motivoCancelacion    obligatorio si está {@code CANCELADA}; cadena vacía si está {@code REALIZADA}
 * @param fechaRegistro        la asigna el servidor
 * @param incidencias          incidencias de la prueba; lista vacía si no tiene
 */
public record Prueba(long id, long idSolicitud, String dniPaciente, String idMedico, EstadoPrueba estado,
        LocalDateTime fechaHoraRealizacion, String observaciones, String motivoCancelacion,
        LocalDateTime fechaRegistro, List<Incidencia> incidencias) {

    public Prueba {
        observaciones = observaciones == null ? "" : observaciones;
        motivoCancelacion = motivoCancelacion == null ? "" : motivoCancelacion;
        incidencias = incidencias == null ? List.of() : List.copyOf(incidencias);
    }

    /** Copia de la prueba con otro identificador (al guardarla por primera vez). */
    public Prueba conId(long nuevoId) {
        return new Prueba(nuevoId, idSolicitud, dniPaciente, idMedico, estado, fechaHoraRealizacion,
                observaciones, motivoCancelacion, fechaRegistro, incidencias);
    }

    /** Copia de la prueba con otras incidencias. */
    public Prueba conIncidencias(List<Incidencia> nuevas) {
        return new Prueba(id, idSolicitud, dniPaciente, idMedico, estado, fechaHoraRealizacion,
                observaciones, motivoCancelacion, fechaRegistro, nuevas);
    }

    /** Datos de la prueba sin sus incidencias, para las listas de resultados. */
    public ResumenPrueba resumen() {
        return new ResumenPrueba(id, idSolicitud, dniPaciente, idMedico, estado, fechaHoraRealizacion,
                observaciones, motivoCancelacion, fechaRegistro, incidencias.size());
    }
}
