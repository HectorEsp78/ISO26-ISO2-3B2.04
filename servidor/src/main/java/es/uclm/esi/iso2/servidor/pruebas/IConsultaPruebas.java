package es.uclm.esi.iso2.servidor.pruebas;

/**
 * Consulta de solo lectura de pruebas, para CU09 (DEC-075). No modifica nada.
 */
public interface IConsultaPruebas {

    /**
     * @throws PruebaException con {@code PRUEBA_NO_ENCONTRADA} si no existe
     */
    DatosPrueba obtenerPrueba(long idPrueba);
}
