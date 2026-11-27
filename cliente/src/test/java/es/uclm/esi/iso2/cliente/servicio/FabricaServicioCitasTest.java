package es.uclm.esi.iso2.cliente.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.comun.protocolo.Protocolo;

import java.util.Map;

import org.junit.jupiter.api.Test;

class FabricaServicioCitasTest {

    @Test
    void porDefectoUsaElServidorLocal() {
        ServicioCitasSocket servicio = assertInstanceOf(ServicioCitasSocket.class,
                FabricaServicioCitas.crear(clave -> null));
        assertEquals("localhost", servicio.host());
        assertEquals(Protocolo.PUERTO_POR_DEFECTO, servicio.puerto());
    }

    @Test
    void usaElServidorIndicado() {
        Map<String, String> config = Map.of(FabricaServicio.PROPIEDAD_HOST, "192.168.1.10",
                FabricaServicio.PROPIEDAD_PUERTO, "6000");
        ServicioCitasSocket servicio = assertInstanceOf(ServicioCitasSocket.class,
                FabricaServicioCitas.crear(config::get));
        assertEquals("192.168.1.10", servicio.host());
        assertEquals(6000, servicio.puerto());
    }

    @Test
    void modoSinServidor() {
        Map<String, String> config = Map.of(FabricaServicio.PROPIEDAD_MODO, FabricaServicio.MODO_MEMORIA);
        assertInstanceOf(ServicioCitasEnMemoria.class, FabricaServicioCitas.crear(config::get));
    }

    @Test
    void configuracionNoValida() {
        Map<String, String> config = Map.of(FabricaServicio.PROPIEDAD_PUERTO, "abc");
        assertThrows(IllegalArgumentException.class, () -> FabricaServicioCitas.crear(config::get));
    }

    @Test
    void excepcionSinDetalle() {
        ExcepcionServicioCitas e = new ExcepcionServicioCitas(CodigoError.ERROR_INTERNO);
        assertEquals("ERROR_INTERNO", e.getMessage());
    }
}
