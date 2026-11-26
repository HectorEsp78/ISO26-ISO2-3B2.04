package es.uclm.esi.iso2.comun.dominio;

import es.uclm.esi.iso2.comun.protocolo.CodigoError;

/**
 * Error de validación de un campo que no es de paciente (por ejemplo, de CU10). El campo es el
 * nombre que aparece en el protocolo y el código indica si falta o no es válido.
 */
public record ErrorCampo(String campo, CodigoError codigo) {
}
