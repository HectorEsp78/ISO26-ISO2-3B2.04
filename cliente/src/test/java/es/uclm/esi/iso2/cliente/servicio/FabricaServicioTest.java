package es.uclm.esi.iso2.cliente.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FabricaServicioTest {

    private static ServicioPacientes crear(Map<String, String> propiedades) {
        return FabricaServicio.crear(propiedades::get);
    }

    @Test
    void porDefectoUsaSocketsEnLocalhost5050() {
        ServicioPacientesSocket s = assertInstanceOf(ServicioPacientesSocket.class, crear(Map.of()));
        assertEquals("localhost", s.host());
        assertEquals(5050, s.puerto());
    }

    @Test
    void valoresEnBlancoUsanLosValoresPorDefecto() {
        ServicioPacientesSocket s = assertInstanceOf(ServicioPacientesSocket.class, crear(Map.of(
                FabricaServicio.PROPIEDAD_MODO, " ",
                FabricaServicio.PROPIEDAD_HOST, "",
                FabricaServicio.PROPIEDAD_PUERTO, " ")));
        assertEquals("localhost", s.host());
        assertEquals(5050, s.puerto());
    }

    @Test
    void hostYPuertoConfigurados() {
        ServicioPacientesSocket s = assertInstanceOf(ServicioPacientesSocket.class, crear(Map.of(
                FabricaServicio.PROPIEDAD_MODO, FabricaServicio.MODO_SOCKET,
                FabricaServicio.PROPIEDAD_HOST, " 192.168.1.10 ",
                FabricaServicio.PROPIEDAD_PUERTO, "6000")));
        assertEquals("192.168.1.10", s.host());
        assertEquals(6000, s.puerto());
    }

    @Test
    void modoMemoria() {
        ServicioPacientesEnMemoria s = assertInstanceOf(ServicioPacientesEnMemoria.class,
                crear(Map.of(FabricaServicio.PROPIEDAD_MODO, FabricaServicio.MODO_MEMORIA)));
        assertEquals(3, s.todos().size());
    }

    @Test
    void modoDesconocido() {
        assertThrows(IllegalArgumentException.class, () -> crear(Map.of(FabricaServicio.PROPIEDAD_MODO, "rest")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "0", "65536", "-1"})
    void puertoNoValido(String puerto) {
        assertThrows(IllegalArgumentException.class,
                () -> crear(Map.of(FabricaServicio.PROPIEDAD_PUERTO, puerto)));
    }

    @Test
    void puertosEnLosLimites() {
        assertEquals(1, ((ServicioPacientesSocket) crear(Map.of(FabricaServicio.PROPIEDAD_PUERTO, "1"))).puerto());
        assertEquals(65535,
                ((ServicioPacientesSocket) crear(Map.of(FabricaServicio.PROPIEDAD_PUERTO, "65535"))).puerto());
    }
}
