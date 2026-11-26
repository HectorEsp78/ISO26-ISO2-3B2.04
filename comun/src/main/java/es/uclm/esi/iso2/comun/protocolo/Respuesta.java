package es.uclm.esi.iso2.comun.protocolo;

import es.uclm.esi.iso2.comun.dominio.CampoPaciente;

import java.util.List;

/**
 * Respuesta del servidor ya interpretada (DEC-039 y DEC-057).
 *
 * @param correcta {@code true} si la respuesta empieza por {@code OK}
 * @param datos    campos que siguen a {@code OK} (vacío si es un error)
 * @param codigo   código de error ({@code null} si es correcta)
 * @param campo    campo de paciente al que se refiere el error ({@code null} si no lo indica o si
 *                 el dato no es un campo de paciente)
 * @param detalle  tercer dato del error tal como llega: el nombre de un campo, un identificador u
 *                 otro dato de apoyo ({@code null} si el error no lo incluye). Lo usan los casos
 *                 de uso cuyo error no se refiere a un campo de paciente
 */
public record Respuesta(boolean correcta, List<String> datos, CodigoError codigo, CampoPaciente campo,
                        String detalle) {

    public Respuesta {
        datos = List.copyOf(datos);
    }

    /**
     * Interpreta una línea recibida del servidor.
     *
     * @throws IllegalArgumentException si la línea no sigue el protocolo
     */
    public static Respuesta leer(String linea) {
        List<String> campos = Protocolo.descomponer(linea);
        String tipo = campos.get(0);
        if (Protocolo.OK.equals(tipo)) {
            return new Respuesta(true, campos.subList(1, campos.size()), null, null, null);
        }
        if (!Protocolo.ERROR.equals(tipo) || campos.size() < 2 || campos.size() > 3) {
            throw new IllegalArgumentException("Respuesta no válida: " + linea);
        }
        CodigoError codigo = CodigoError.valueOf(campos.get(1));
        String detalle = campos.size() == 3 ? campos.get(2) : null;
        CampoPaciente campo = detalle == null ? null : CampoPaciente.buscarPorNombreProtocolo(detalle);
        return new Respuesta(false, List.of(), codigo, campo, detalle);
    }
}
