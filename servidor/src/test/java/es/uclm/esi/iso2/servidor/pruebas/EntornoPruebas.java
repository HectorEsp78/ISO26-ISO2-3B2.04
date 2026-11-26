package es.uclm.esi.iso2.servidor.pruebas;

import es.uclm.esi.iso2.comun.dominio.Cita;
import es.uclm.esi.iso2.comun.dominio.DatosCita;
import es.uclm.esi.iso2.comun.dominio.EstadoPaciente;
import es.uclm.esi.iso2.comun.dominio.Paciente;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.servidor.citas.CitaDAO;
import es.uclm.esi.iso2.servidor.citas.CitaService;
import es.uclm.esi.iso2.servidor.pacientes.IPacienteService;
import es.uclm.esi.iso2.servidor.pacientes.PacienteException;
import es.uclm.esi.iso2.servidor.persistencia.FabricaConexiones;
import es.uclm.esi.iso2.servidor.persistencia.ScriptSql;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Piezas comunes de las pruebas de CU10: H2 con los scripts reales, CU13 real, y versiones
 * simuladas de CU06 y CU12. «Ahora» es el 26/11/2026 a las 10:00.
 */
final class EntornoPruebas {

    static final ZoneId ZONA = ZoneId.of("Europe/Madrid");
    static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 26, 10, 0);
    static final Clock RELOJ = Clock.fixed(AHORA.atZone(ZONA).toInstant(), ZONA);
    static final String DNI = "12345678Z";
    static final String MEDICO = "medico1";
    static final String OTRO_MEDICO = "medico2";

    private static final AtomicInteger CONTADOR = new AtomicInteger();

    final FabricaConexiones conexiones;
    final CitaService citas;
    final SolicitudesSimuladas solicitudes = new SolicitudesSimuladas();
    final PruebaDAO dao;
    final PruebaService servicio;

    EntornoPruebas() {
        conexiones = FabricaConexiones.jdbc(
                "jdbc:h2:mem:pruebas" + CONTADOR.incrementAndGet() + ";MODE=MySQL;DB_CLOSE_DELAY=-1", "sa", "");
        ScriptSql.ejecutar(conexiones, "/sql/01_esquema.sql");
        ScriptSql.ejecutar(conexiones, "/sql/02_datos.sql");
        citas = new CitaService(new CitaDAO(conexiones), dni -> EstadoPaciente.ACTIVO, RELOJ);
        dao = new PruebaDAO(conexiones);
        servicio = new PruebaService(dao, citas, solicitudes, new PacientesSimulados(), RELOJ);
    }

    /** Crea una cita programada el 01/12/2026 a la hora indicada y una solicitud del médico que la tiene. */
    SolicitudDePrueba solicitudConCita(String medico, int hora) {
        Cita cita = citas.asignarCita(new DatosCita(DNI, 1, 1, LocalDateTime.of(2026, 12, 1, hora, 0), null));
        return solicitudes.anadir(medico, DNI, cita.id());
    }

    /** CU06 simulado: guarda las solicitudes en memoria y recuerda los cambios de estado. */
    static final class SolicitudesSimuladas implements SolicitudesDePrueba {

        final Map<Long, SolicitudDePrueba> porId = new HashMap<>();
        final Map<Long, String> estados = new HashMap<>();
        boolean fallaAlMarcar;
        private long siguiente = 1;

        SolicitudDePrueba anadir(String medico, String dni, Long idCita) {
            SolicitudDePrueba s = new SolicitudDePrueba(siguiente++, dni, medico, "Radiografía de tórax", "NORMAL",
                    idCita);
            porId.put(s.idSolicitud(), s);
            estados.put(s.idSolicitud(), "PENDIENTE");
            return s;
        }

        @Override
        public Optional<SolicitudDePrueba> buscar(long idSolicitud) {
            return Optional.ofNullable(porId.get(idSolicitud));
        }

        @Override
        public List<SolicitudDePrueba> conCitaDelMedico(String idMedico, String dni) {
            return porId.values().stream()
                    .filter(s -> s.idMedico().equals(idMedico) && (dni == null || s.dniPaciente().equals(dni)))
                    .toList();
        }

        @Override
        public void marcarRealizada(long idSolicitud) {
            cambiar(idSolicitud, "REALIZADA");
        }

        @Override
        public void marcarCancelada(long idSolicitud) {
            cambiar(idSolicitud, "CANCELADA");
        }

        private void cambiar(long idSolicitud, String estado) {
            if (fallaAlMarcar) {
                throw new IllegalStateException("fallo simulado de CU06");
            }
            estados.put(idSolicitud, estado);
        }
    }

    /** CU12 simulado: un solo paciente conocido. */
    static final class PacientesSimulados implements IPacienteService {

        @Override
        public Paciente buscar(String dni) {
            if (!DNI.equals(dni)) {
                throw new PacienteException(CodigoError.PACIENTE_NO_ENCONTRADO);
            }
            return new Paciente(DNI, "CMCM000000000001", "Lucía", "García Martín", null, null, "600111222", null, null,
                    EstadoPaciente.ACTIVO);
        }

        @Override
        public Paciente alta(Paciente paciente) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Paciente modificar(Paciente paciente) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Paciente baja(String dni) {
            throw new UnsupportedOperationException();
        }

        @Override
        public EstadoPaciente comprobar(String dni) {
            return EstadoPaciente.ACTIVO;
        }
    }
}
