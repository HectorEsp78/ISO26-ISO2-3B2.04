package es.uclm.esi.iso2.cliente.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

class FabricaServicioPruebasTest {

    private static ServicioPruebas crear(Map<String, String> propiedades) {
        return FabricaServicioPruebas.crear(propiedades::get);
    }

    @Test
    void porDefectoUsaSocketsEnLocalhost5050() {
        ServicioPruebasSocket s = assertInstanceOf(ServicioPruebasSocket.class, crear(Map.of()));
        assertEquals("localhost", s.host());
        assertEquals(5050, s.puerto());
    }

    @Test
    void hostYPuertoConfigurados() {
        ServicioPruebasSocket s = assertInstanceOf(ServicioPruebasSocket.class, crear(Map.of(
                FabricaServicio.PROPIEDAD_HOST, " 192.168.1.10 ", FabricaServicio.PROPIEDAD_PUERTO, "6000")));
        assertEquals("192.168.1.10", s.host());
        assertEquals(6000, s.puerto());
    }

    @Test
    void modoMemoria() {
        assertInstanceOf(ServicioPruebasEnMemoria.class,
                crear(Map.of(FabricaServicio.PROPIEDAD_MODO, FabricaServicio.MODO_MEMORIA)));
    }

    @Test
    void configuracionNoValida() {
        assertThrows(IllegalArgumentException.class, () -> crear(Map.of(FabricaServicio.PROPIEDAD_MODO, "otro")));
        assertThrows(IllegalArgumentException.class, () -> crear(Map.of(FabricaServicio.PROPIEDAD_PUERTO, "abc")));
        assertThrows(IllegalArgumentException.class, () -> crear(Map.of(FabricaServicio.PROPIEDAD_PUERTO, "0")));
        assertThrows(IllegalArgumentException.class, () -> crear(Map.of(FabricaServicio.PROPIEDAD_PUERTO, "70000")));
    }

    @Test
    void idMedico() {
        assertEquals("medico1", FabricaServicioPruebas.idMedico(Map.<String, String>of()::get));
        assertEquals("ana", FabricaServicioPruebas.idMedico(Map.of(FabricaServicioPruebas.PROPIEDAD_MEDICO, " ana ")::get));
    }
}
