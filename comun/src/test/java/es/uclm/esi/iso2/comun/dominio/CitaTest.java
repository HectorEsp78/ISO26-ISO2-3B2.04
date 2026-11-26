package es.uclm.esi.iso2.comun.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class CitaTest {

    private static final LocalDateTime DIEZ = LocalDateTime.of(2026, 12, 1, 10, 0);

    private static Cita cita(LocalDateTime inicio, int duracion) {
        return new Cita(1, "12345678Z", 1, 1, inicio, duracion, EstadoCita.PROGRAMADA, "");
    }

    @Test
    void elFinEsElInicioMasLaDuracion() {
        assertEquals(LocalDateTime.of(2026, 12, 1, 10, 30), cita(DIEZ, 30).fin());
    }

    /** Cita de 10:00 a 10:30 frente a otra que empieza a los minutos indicados desde las 10:00. */
    @ParameterizedTest(name = "otra empieza en +{0} min y dura {1} → solapa: {2}")
    @CsvSource({
            "-30, 30, false", // termina justo cuando empieza (límite)
            "-30, 31, true",  // termina un minuto después de que empiece
            "-10, 60, true",  // la contiene
            "10, 10, true",   // está dentro
            "29, 10, true",   // empieza un minuto antes del fin
            "30, 10, false",  // empieza justo al terminar (límite)
            "60, 10, false"   // muy después
    })
    void solape(int desplazamiento, int duracion, boolean esperado) {
        Cita a = cita(DIEZ, 30);
        Cita b = cita(DIEZ.plusMinutes(desplazamiento), duracion);
        assertEquals(esperado, a.solapaCon(b));
        assertEquals(esperado, b.solapaCon(a), "el solape es simétrico");
    }

    @Test
    void elMotivoNuloSeGuardaComoVacio() {
        assertEquals("", new Cita(0, "12345678Z", 1, 1, DIEZ, 30, EstadoCita.PROGRAMADA, null).motivoCancelacion());
    }

    @Test
    void conIdYConEstadoSoloCambianEseDato() {
        Cita original = cita(DIEZ, 30);
        Cita conId = original.conId(7);
        assertEquals(7, conId.id());
        assertEquals(original.inicio(), conId.inicio());

        Cita cancelada = original.conEstado(EstadoCita.CANCELADA, "No viene");
        assertEquals(EstadoCita.CANCELADA, cancelada.estado());
        assertEquals("No viene", cancelada.motivoCancelacion());
        assertEquals(original.id(), cancelada.id());
        assertTrue(original.solapaCon(cancelada));
        assertFalse(original.motivoCancelacion().equals(cancelada.motivoCancelacion()));
    }
}
