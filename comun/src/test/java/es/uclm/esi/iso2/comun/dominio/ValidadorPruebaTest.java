package es.uclm.esi.iso2.comun.dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uclm.esi.iso2.comun.protocolo.CodigoError;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class ValidadorPruebaTest {

    private static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 26, 10, 0);
    private static final NuevaIncidencia INCIDENCIA = new NuevaIncidencia(TipoIncidencia.TECNICA, "Fallo del equipo");

    private static void assertError(String campo, CodigoError codigo, java.util.Optional<ErrorCampo> error) {
        assertEquals(new ErrorCampo(campo, codigo), error.orElseThrow());
    }

    @Test
    void normalizarQuitaEspaciosYAceptaNulo() {
        assertEquals("a", ValidadorPrueba.normalizar("  a "));
        assertEquals("", ValidadorPrueba.normalizar(null));
    }

    @Test
    void medico() {
        assertTrue(ValidadorPrueba.validarMedico("medico1").isEmpty());
        assertTrue(ValidadorPrueba.validarMedico("x".repeat(20)).isEmpty());
        assertError("idMedico", CodigoError.CAMPO_OBLIGATORIO, ValidadorPrueba.validarMedico("  "));
        assertError("idMedico", CodigoError.CAMPO_OBLIGATORIO, ValidadorPrueba.validarMedico(null));
        assertError("idMedico", CodigoError.CAMPO_NO_VALIDO, ValidadorPrueba.validarMedico("x".repeat(21)));
    }

    @Test
    void realizacionValida() {
        assertTrue(ValidadorPrueba.validarRealizacion(AHORA, "", List.of(), AHORA).isEmpty());
        assertTrue(ValidadorPrueba.validarRealizacion(AHORA.minusHours(1), "x".repeat(1000), null, AHORA).isEmpty());
        assertTrue(ValidadorPrueba.validarRealizacion(AHORA, null, List.of(INCIDENCIA), AHORA).isEmpty());
    }

    @Test
    void realizacionConErrores() {
        assertError("fechaHora", CodigoError.CAMPO_OBLIGATORIO,
                ValidadorPrueba.validarRealizacion(null, "", List.of(), AHORA));
        assertError("fechaHora", CodigoError.FECHA_FUTURA,
                ValidadorPrueba.validarRealizacion(AHORA.plusMinutes(1), "", List.of(), AHORA));
        assertError("observaciones", CodigoError.CAMPO_NO_VALIDO,
                ValidadorPrueba.validarRealizacion(AHORA, "x".repeat(1001), List.of(), AHORA));
        assertError("tipo", CodigoError.CAMPO_OBLIGATORIO, ValidadorPrueba.validarRealizacion(AHORA, "",
                List.of(INCIDENCIA, new NuevaIncidencia(null, "x")), AHORA));
    }

    @Test
    void motivo() {
        assertTrue(ValidadorPrueba.validarMotivo("Paciente ausente").isEmpty());
        assertTrue(ValidadorPrueba.validarMotivo("x".repeat(255)).isEmpty());
        assertError("motivo", CodigoError.CAMPO_OBLIGATORIO, ValidadorPrueba.validarMotivo(" "));
        assertError("motivo", CodigoError.CAMPO_NO_VALIDO, ValidadorPrueba.validarMotivo("x".repeat(256)));
    }

    @Test
    void incidencia() {
        assertTrue(ValidadorPrueba.validarIncidencia(INCIDENCIA).isEmpty());
        assertTrue(ValidadorPrueba.validarIncidencia(new NuevaIncidencia(TipoIncidencia.OTRA, "x".repeat(500)))
                .isEmpty());
        assertError("tipo", CodigoError.CAMPO_OBLIGATORIO, ValidadorPrueba.validarIncidencia(null));
        assertError("tipo", CodigoError.CAMPO_OBLIGATORIO,
                ValidadorPrueba.validarIncidencia(new NuevaIncidencia(null, "x")));
        assertError("descripcion", CodigoError.CAMPO_OBLIGATORIO,
                ValidadorPrueba.validarIncidencia(new NuevaIncidencia(TipoIncidencia.OTRA, " ")));
        assertError("descripcion", CodigoError.CAMPO_NO_VALIDO,
                ValidadorPrueba.validarIncidencia(new NuevaIncidencia(TipoIncidencia.OTRA, "x".repeat(501))));
    }

    @Test
    void filtro() {
        LocalDate dia = LocalDate.of(2026, 11, 26);
        assertTrue(ValidadorPrueba.validarFiltro(FiltroPruebas.SIN_FILTROS).isEmpty());
        assertTrue(ValidadorPrueba.validarFiltro(new FiltroPruebas(null, null, dia, dia, null)).isEmpty());
        assertError("hasta", CodigoError.CAMPO_NO_VALIDO,
                ValidadorPrueba.validarFiltro(new FiltroPruebas(null, null, dia, dia.minusDays(1), null)));
    }
}
