package es.uclm.esi.iso2.cliente.servicio;

import es.uclm.esi.iso2.comun.dominio.ErrorCampo;
import es.uclm.esi.iso2.comun.dominio.EstadoPrueba;
import es.uclm.esi.iso2.comun.dominio.FiltroPruebas;
import es.uclm.esi.iso2.comun.dominio.Incidencia;
import es.uclm.esi.iso2.comun.dominio.NuevaIncidencia;
import es.uclm.esi.iso2.comun.dominio.Prueba;
import es.uclm.esi.iso2.comun.dominio.ResumenPrueba;
import es.uclm.esi.iso2.comun.dominio.SolicitudPendiente;
import es.uclm.esi.iso2.comun.dominio.ValidadorPrueba;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Servicio de pruebas sin red ni base de datos (DEC-059 y DEC-080).
 *
 * <p>Aplica las mismas reglas y devuelve los mismos errores que el servidor (DEC-078), de modo
 * que sirve para las pruebas y para usar la interfaz sin servidor. Las solicitudes pendientes se
 * añaden con {@link #anadirPendiente}; los datos se pierden al cerrar el cliente.</p>
 */
public class ServicioPruebasEnMemoria implements ServicioPruebas {

    private record Solicitud(SolicitudPendiente datos, String idMedico) {
    }

    private final Clock reloj;
    private final Map<Long, Solicitud> solicitudes = new LinkedHashMap<>();
    private final Map<Long, Prueba> pruebas = new LinkedHashMap<>();
    private long siguientePrueba = 1;
    private long siguienteIncidencia = 1;

    public ServicioPruebasEnMemoria() {
        this(Clock.systemDefaultZone());
    }

    public ServicioPruebasEnMemoria(Clock reloj) {
        this.reloj = reloj;
    }

    /** Servicio con unas solicitudes de ejemplo para el médico indicado, para probar la interfaz. */
    public static ServicioPruebasEnMemoria conDatosDeEjemplo(String idMedico) {
        ServicioPruebasEnMemoria servicio = new ServicioPruebasEnMemoria();
        LocalDate manana = LocalDate.now(servicio.reloj).plusDays(1);
        servicio.anadirPendiente(idMedico, new SolicitudPendiente(1, "12345678Z", "Lucía García Martín",
                "Radiografía de tórax", "NORMAL", 1, manana.atTime(9, 0)));
        servicio.anadirPendiente(idMedico, new SolicitudPendiente(2, "X1234567L", "Ahmed El Idrissi",
                "Ecografía abdominal", "URGENTE", 2, manana.atTime(10, 0)));
        servicio.anadirPendiente(idMedico, new SolicitudPendiente(3, "12345678Z", "Lucía García Martín",
                "Mamografía", "NORMAL", 3, manana.atTime(11, 30)));
        return servicio;
    }

    /** Añade una solicitud con cita programada y sin prueba, de un médico. */
    public synchronized void anadirPendiente(String idMedico, SolicitudPendiente solicitud) {
        solicitudes.put(solicitud.idSolicitud(), new Solicitud(solicitud, idMedico));
    }

    @Override
    public synchronized List<SolicitudPendiente> listarPendientes(String idMedico, String dni) {
        lanzarSiHayError(ValidadorPrueba.validarMedico(idMedico));
        String filtro = dni == null ? "" : dni.strip().toUpperCase();
        return solicitudes.values().stream()
                .filter(s -> s.idMedico().equals(idMedico.strip()))
                .filter(s -> !yaTienePrueba(s.datos().idSolicitud()))
                .filter(s -> filtro.isEmpty() || s.datos().dniPaciente().equals(filtro))
                .map(Solicitud::datos)
                .sorted(Comparator.comparing(SolicitudPendiente::inicioCita))
                .toList();
    }

    @Override
    public synchronized Prueba registrarRealizacion(String idMedico, long idSolicitud, LocalDateTime fechaHora,
            String observaciones, List<NuevaIncidencia> incidencias) {
        lanzarSiHayError(ValidadorPrueba.validarMedico(idMedico));
        List<NuevaIncidencia> nuevas = incidencias == null ? List.of() : incidencias;
        lanzarSiHayError(ValidadorPrueba.validarRealizacion(fechaHora, observaciones, nuevas, ahora()));
        Solicitud solicitud = comprobarSolicitud(idMedico.strip(), idSolicitud);
        LocalDateTime ahora = ahora();
        List<Incidencia> incidenciasPrueba = new ArrayList<>();
        for (NuevaIncidencia i : nuevas) {
            incidenciasPrueba.add(new Incidencia(siguienteIncidencia++, siguientePrueba, i.tipo(),
                    ValidadorPrueba.normalizar(i.descripcion()), ahora));
        }
        return guardar(new Prueba(siguientePrueba, idSolicitud, solicitud.datos().dniPaciente(), idMedico.strip(),
                EstadoPrueba.REALIZADA, fechaHora, ValidadorPrueba.normalizar(observaciones), "", ahora,
                incidenciasPrueba));
    }

    @Override
    public synchronized Prueba registrarCancelacion(String idMedico, long idSolicitud, String motivo) {
        lanzarSiHayError(ValidadorPrueba.validarMedico(idMedico));
        lanzarSiHayError(ValidadorPrueba.validarMotivo(motivo));
        Solicitud solicitud = comprobarSolicitud(idMedico.strip(), idSolicitud);
        return guardar(new Prueba(siguientePrueba, idSolicitud, solicitud.datos().dniPaciente(), idMedico.strip(),
                EstadoPrueba.CANCELADA, null, "", ValidadorPrueba.normalizar(motivo), ahora(), List.of()));
    }

    @Override
    public synchronized Incidencia anadirIncidencia(String idMedico, long idPrueba, NuevaIncidencia incidencia) {
        lanzarSiHayError(ValidadorPrueba.validarMedico(idMedico));
        lanzarSiHayError(ValidadorPrueba.validarIncidencia(incidencia));
        Prueba prueba = pruebaDelMedico(idMedico.strip(), idPrueba);
        Incidencia nueva = new Incidencia(siguienteIncidencia++, idPrueba, incidencia.tipo(),
                ValidadorPrueba.normalizar(incidencia.descripcion()), ahora());
        List<Incidencia> todas = new ArrayList<>(prueba.incidencias());
        todas.add(nueva);
        pruebas.put(idPrueba, prueba.conIncidencias(todas));
        return nueva;
    }

    @Override
    public synchronized List<ResumenPrueba> buscar(String idMedico, FiltroPruebas filtro) {
        lanzarSiHayError(ValidadorPrueba.validarMedico(idMedico));
        FiltroPruebas f = filtro == null ? FiltroPruebas.SIN_FILTROS : filtro;
        lanzarSiHayError(ValidadorPrueba.validarFiltro(f));
        String dni = f.dniPaciente() == null ? "" : f.dniPaciente().strip().toUpperCase();
        return pruebas.values().stream()
                .filter(p -> p.idMedico().equals(idMedico.strip()))
                .filter(p -> dni.isEmpty() || p.dniPaciente().equals(dni))
                .filter(p -> f.idSolicitud() == null || p.idSolicitud() == f.idSolicitud())
                .filter(p -> f.desde() == null || !p.fechaRegistro().toLocalDate().isBefore(f.desde()))
                .filter(p -> f.hasta() == null || !p.fechaRegistro().toLocalDate().isAfter(f.hasta()))
                .filter(p -> f.estado() == null || p.estado() == f.estado())
                .sorted(Comparator.comparing(Prueba::fechaRegistro).thenComparingLong(Prueba::id).reversed())
                .map(Prueba::resumen)
                .toList();
    }

    @Override
    public synchronized Prueba obtener(String idMedico, long idPrueba) {
        lanzarSiHayError(ValidadorPrueba.validarMedico(idMedico));
        return pruebaDelMedico(idMedico.strip(), idPrueba);
    }

    // ---- Auxiliares ----

    private LocalDateTime ahora() {
        return LocalDateTime.now(reloj).truncatedTo(ChronoUnit.SECONDS);
    }

    private boolean yaTienePrueba(long idSolicitud) {
        return pruebas.values().stream().anyMatch(p -> p.idSolicitud() == idSolicitud);
    }

    private Solicitud comprobarSolicitud(String idMedico, long idSolicitud) {
        Solicitud solicitud = solicitudes.get(idSolicitud);
        if (solicitud == null) {
            throw new ExcepcionServicioPruebas(CodigoError.SOLICITUD_NO_ENCONTRADA);
        }
        if (!solicitud.idMedico().equals(idMedico)) {
            throw new ExcepcionServicioPruebas(CodigoError.SOLICITUD_NO_PERMITIDA);
        }
        pruebas.values().stream().filter(p -> p.idSolicitud() == idSolicitud).findFirst().ifPresent(p -> {
            throw new ExcepcionServicioPruebas(CodigoError.PRUEBA_YA_EXISTE, String.valueOf(p.id()), null);
        });
        return solicitud;
    }

    private Prueba pruebaDelMedico(String idMedico, long idPrueba) {
        Prueba prueba = pruebas.get(idPrueba);
        if (prueba == null) {
            throw new ExcepcionServicioPruebas(CodigoError.PRUEBA_NO_ENCONTRADA);
        }
        if (!prueba.idMedico().equals(idMedico)) {
            throw new ExcepcionServicioPruebas(CodigoError.PRUEBA_NO_PERMITIDA);
        }
        return prueba;
    }

    private Prueba guardar(Prueba prueba) {
        pruebas.put(prueba.id(), prueba);
        siguientePrueba++;
        return prueba;
    }

    private static void lanzarSiHayError(Optional<ErrorCampo> error) {
        error.ifPresent(e -> {
            throw new ExcepcionServicioPruebas(e.codigo(), e.campo(), null);
        });
    }
}
