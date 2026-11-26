package es.uclm.esi.iso2.comun.dominio;

import es.uclm.esi.iso2.comun.protocolo.CodigoError;

/**
 * Error de validación de un campo: falta ({@link CodigoError#CAMPO_OBLIGATORIO}) o no tiene un
 * formato válido ({@link CodigoError#CAMPO_NO_VALIDO}).
 */
public record ErrorValidacion(CampoPaciente campo, CodigoError codigo) {
}
