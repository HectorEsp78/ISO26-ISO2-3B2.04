package es.uclm.esi.iso2.servidor.red;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uclm.esi.iso2.comun.dominio.EstadoPrueba;
import es.uclm.esi.iso2.comun.dominio.Prueba;
import es.uclm.esi.iso2.comun.protocolo.Protocolo;
import es.uclm.esi.iso2.comun.protocolo.ProtocoloPruebas;
import es.uclm.esi.iso2.servidor.pruebas.IPruebaService;
import es.uclm.esi.iso2.servidor.pruebas.PruebaException;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.comun.dominio.FiltroPruebas;
import es.uclm.esi.iso2.comun.dominio.Incidencia;
import es.uclm.esi.iso2.comun.dominio.NuevaIncidencia;
import es.uclm.esi.iso2.comun.dominio.ResumenPrueba;
import es.uclm.esi.iso2.comun.dominio.SolicitudPendiente;
import es.uclm.esi.iso2.comun.dominio.TipoIncidencia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Pruebas de las operaciones de CU10 a nivel de protocolo, con un servicio simulado que guarda lo
 * que recibe. La lógica de negocio se prueba en {@code PruebaServiceTest}.
 */
class OperacionesPruebasTest {

    private static final LocalDateTime FECHA = LocalDateTime.of(2026, 11, 26, 9, 30);
    private static final Prueba PRUEBA = new Prueba(7, 4, "12345678Z", "medico1", EstadoPrueba.REALIZADA, FECHA, "obs",
            "", LocalDateTime.of(2026, 11, 26, 10, 0), List.of());

    private ManejadorPeticiones manejador;
    private final StringBuilder llamadas = new StringBuilder();
    private PruebaException error;

    @BeforeEach
    void preparar() {
        llamadas.setLength(0);
        error = null;
        IPruebaService servicio = new IPruebaService() {
            @Override
            public List<SolicitudPendiente> buscarSolicitudesPendientes(String idMedico, String dni) {
                fallar();
                llamadas.append("pendientes:").append(idMedico).append(':').append(dni);
                return List.of();
            }

            @Override
            public Prueba registrarRealizacion(String idMedico, long idSolicitud, LocalDateTime fechaHora,
                    String observaciones, List<NuevaIncidencia> incidencias) {
                fallar();
                llamadas.append("realizar:").append(idMedico).append(':').append(idSolicitud).append(':')
                        .append(fechaHora).append(':').append(observaciones).append(':').append(incidencias);
                return PRUEBA;
            }

            @Override
            public Prueba registrarCancelacion(String idMedico, long idSolicitud, String motivo) {
                fallar();
                llamadas.append("cancelar:").append(idSolicitud).append(':').append(motivo);
                return PRUEBA;
            }

            @Override
            public Incidencia anadirIncidencia(String idMedico, long idPrueba, NuevaIncidencia incidencia) {
                fallar();
                llamadas.append("incidencia:").append(idPrueba).append(':').append(incidencia);
                return new Incidencia(1, idPrueba, TipoIncidencia.OTRA, "d", FECHA);
            }

            @Override
            public List<ResumenPrueba> buscarPruebas(String idMedico, FiltroPruebas filtro) {
                fallar();
                llamadas.append("buscar:").append(filtro);
                return List.of(PRUEBA.resumen());
            }

            @Override
            public Prueba consultarPrueba(String idMedico, long idPrueba) {
                fallar();
                llamadas.append("consultar:").append(idPrueba);
                return PRUEBA;
            }

            private void fallar() {
                if (error != null) {
                    throw error;
                }
            }
        };
        manejador = new ManejadorPeticiones();
        OperacionesPruebas.registrar(manejador, servicio);
    }

    @Test
    void listarPendientes() {
        assertEquals("OK;0", manejador.atender("LISTAR_SOLICITUDES_PENDIENTES;medico1;12345678Z"));
        assertEquals("pendientes:medico1:12345678Z", llamadas.toString());
        assertEquals("ERROR;PETICION_NO_VALIDA", manejador.atender("LISTAR_SOLICITUDES_PENDIENTES;medico1"));
    }

    @Test
    void registrarPruebaConIncidencias() {
        String respuesta = manejador.atender("REGISTRAR_PRUEBA;medico1;4;2026-11-26T09:30;obs\\;1;2;TECNICA;fallo;OTRA;x");
        assertEquals(ProtocoloPruebas.respuestaOk(PRUEBA), respuesta);
        assertEquals("realizar:medico1:4:2026-11-26T09:30:obs;1:[NuevaIncidencia[tipo=TECNICA, descripcion=fallo], "
                + "NuevaIncidencia[tipo=OTRA, descripcion=x]]", llamadas.toString());
    }

    @Test
    void registrarPruebaSinIncidenciasNiObservaciones() {
        manejador.atender("REGISTRAR_PRUEBA;medico1;4;;;0");
        assertEquals("realizar:medico1:4:null::[]", llamadas.toString());
    }

    @Test
    void registrarPruebaConPeticionesMalFormadas() {
        assertEquals("ERROR;PETICION_NO_VALIDA", manejador.atender("REGISTRAR_PRUEBA;medico1;4"));
        assertEquals("ERROR;PETICION_NO_VALIDA", manejador.atender("REGISTRAR_PRUEBA;medico1;4;;;2;TECNICA;x"));
        assertEquals("ERROR;PETICION_NO_VALIDA", manejador.atender("REGISTRAR_PRUEBA;medico1;4;;;abc"));
        assertEquals("ERROR;PETICION_NO_VALIDA", manejador.atender("REGISTRAR_PRUEBA;medico1;4;;;-1"));
        assertEquals("ERROR;CAMPO_NO_VALIDO;idSolicitud", manejador.atender("REGISTRAR_PRUEBA;medico1;abc;;;0"));
        assertEquals("ERROR;CAMPO_OBLIGATORIO;idSolicitud", manejador.atender("REGISTRAR_PRUEBA;medico1;;;;0"));
        assertEquals("ERROR;CAMPO_NO_VALIDO;fechaHora", manejador.atender("REGISTRAR_PRUEBA;medico1;4;ayer;;0"));
        assertEquals("ERROR;CAMPO_NO_VALIDO;tipo", manejador.atender("REGISTRAR_PRUEBA;medico1;4;;;1;RARA;x"));
        assertTrue(llamadas.isEmpty());
    }

    @Test
    void cancelarPrueba() {
        assertEquals(ProtocoloPruebas.respuestaOk(PRUEBA), manejador.atender("CANCELAR_PRUEBA;medico1;4;Ausente"));
        assertEquals("cancelar:4:Ausente", llamadas.toString());
    }

    @Test
    void anadirIncidencia() {
        assertEquals("OK;1;OTRA;d;2026-11-26T09:30:00",
                manejador.atender("ANADIR_INCIDENCIA;medico1;7;OTRA;d"));
        assertEquals("incidencia:7:NuevaIncidencia[tipo=OTRA, descripcion=d]", llamadas.toString());
    }

    @Test
    void buscarPruebas() {
        String respuesta = manejador.atender("BUSCAR_PRUEBAS;medico1;12345678Z;4;2026-11-01;2026-11-30;CANCELADA");
        assertEquals(ProtocoloPruebas.respuestaResumenes(List.of(PRUEBA.resumen())), respuesta);
        assertEquals("buscar:" + new FiltroPruebas("12345678Z", 4L, LocalDate.of(2026, 11, 1),
                LocalDate.of(2026, 11, 30), EstadoPrueba.CANCELADA), llamadas.toString());
        assertEquals("ERROR;CAMPO_NO_VALIDO;desde", manejador.atender("BUSCAR_PRUEBAS;medico1;;;ayer;;"));
        assertEquals("ERROR;CAMPO_NO_VALIDO;estado", manejador.atender("BUSCAR_PRUEBAS;medico1;;;;;X"));
        assertEquals("ERROR;CAMPO_NO_VALIDO;idSolicitud", manejador.atender("BUSCAR_PRUEBAS;medico1;;x;;;"));
    }

    @Test
    void obtenerPrueba() {
        assertEquals(ProtocoloPruebas.respuestaOk(PRUEBA), manejador.atender("OBTENER_PRUEBA;medico1;7"));
        assertEquals("ERROR;CAMPO_OBLIGATORIO;idPrueba", manejador.atender("OBTENER_PRUEBA;medico1;"));
    }

    @Test
    void erroresDelServicioSeConviertenEnRespuestas() {
        error = new PruebaException(CodigoError.PRUEBA_YA_EXISTE, "9");
        assertEquals("ERROR;PRUEBA_YA_EXISTE;9", manejador.atender("CANCELAR_PRUEBA;medico1;4;x"));
        error = new PruebaException(CodigoError.SOLICITUD_SIN_CITA);
        assertEquals(Protocolo.respuestaErrorConDetalle(CodigoError.SOLICITUD_SIN_CITA, null),
                manejador.atender("OBTENER_PRUEBA;medico1;7"));
    }
}
