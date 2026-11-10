import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("SolicitudPrueba")
class SolicitudPruebaTest {

    private SolicitudPrueba solicitud;

    @BeforeEach
    void setUp() {
        solicitud = new SolicitudPrueba(3);
    }

    @Test
    @DisplayName("Una solicitud nueva está PENDIENTE y sin informe")
    void solicitudNuevaEstaPendiente() {
        assertEquals(SolicitudPrueba.Estado.PENDIENTE, solicitud.getEstado());
        assertNull(solicitud.getInforme());
    }

    @Test
    @DisplayName("Registrar la realización cambia el estado a REALIZADA")
    void registrarRealizacionCambiaElEstado() {
        // Arrange: solicitud creada en setUp()
        // Act
        solicitud.registrarRealizacion();
        // Assert
        assertEquals(SolicitudPrueba.Estado.REALIZADA, solicitud.getEstado());
    }

    @Test
    @DisplayName("No se puede registrar dos veces la realización")
    void registrarRealizacionDosVecesLanzaExcepcion() {
        solicitud.registrarRealizacion();

        IllegalStateException e = assertThrows(IllegalStateException.class,
                solicitud::registrarRealizacion);
        assertEquals("Ya realizada", e.getMessage());
    }

    @Test
    @DisplayName("Se puede registrar el informe de una prueba realizada")
    void registrarInformeEnPruebaRealizada() {
        solicitud.registrarRealizacion();

        solicitud.registrarInforme("Sin hallazgos patológicos");

        assertEquals("Sin hallazgos patológicos", solicitud.getInforme());
    }

    @Test
    @DisplayName("No se puede registrar el informe de una prueba pendiente")
    void registrarInformeEnPruebaPendienteLanzaExcepcion() {
        assertThrows(IllegalStateException.class,
                () -> solicitud.registrarInforme("Informe"));
        assertNull(solicitud.getInforme());
    }

    @Test
    @DisplayName("No se puede registrar el informe dos veces")
    void registrarInformeDosVecesLanzaExcepcion() {
        solicitud.registrarRealizacion();
        solicitud.registrarInforme("Primero");

        assertThrows(IllegalStateException.class,
                () -> solicitud.registrarInforme("Segundo"));
        assertEquals("Primero", solicitud.getInforme());
    }

    @ParameterizedTest(name = "informe \"{0}\" es inválido")
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t\n"})
    @DisplayName("Rechaza informes nulos o en blanco")
    void rechazaInformeVacio(String texto) {
        solicitud.registrarRealizacion();

        assertThrows(IllegalArgumentException.class,
                () -> solicitud.registrarInforme(texto));
    }

    @ParameterizedTest(name = "prioridad {0} es válida")
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("Acepta prioridades entre 1 y 5 (valores límite incluidos)")
    void aceptaPrioridadesValidas(int prioridad) {
        SolicitudPrueba sp = new SolicitudPrueba(prioridad);
        assertEquals(prioridad, sp.getPrioridad());
    }

    @ParameterizedTest(name = "prioridad {0} es inválida")
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 6, Integer.MAX_VALUE})
    @DisplayName("Rechaza prioridades fuera de rango")
    void rechazaPrioridadesFueraDeRango(int prioridad) {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> new SolicitudPrueba(prioridad));
        assertEquals("Prioridad fuera de rango", e.getMessage());
    }
}
