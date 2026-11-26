package es.uclm.esi.iso2.comun.dominio;

import java.time.LocalDate;

/**
 * Filtros de la búsqueda de pruebas (CU10, flujo 6.4). Un campo {@code null} significa «sin
 * filtrar por él». Las fechas se comparan con la fecha de registro e incluyen el día completo.
 */
public record FiltroPruebas(String dniPaciente, Long idSolicitud, LocalDate desde, LocalDate hasta,
        EstadoPrueba estado) {

    /** Filtro que no excluye ninguna prueba. */
    public static final FiltroPruebas SIN_FILTROS = new FiltroPruebas(null, null, null, null, null);
}
