/**
 * Solicitud de una prueba diagnóstica.
 *
 * <p>Ciclo de vida: se crea en estado {@link Estado#PENDIENTE}, pasa a
 * {@link Estado#REALIZADA} al registrar su realización y, una vez realizada,
 * se le puede asociar un único informe.</p>
 */
public class SolicitudPrueba {

    /** Estados posibles de una solicitud. */
    public enum Estado {
        PENDIENTE,
        REALIZADA
    }

    /** Prioridad máxima (urgente). */
    public static final int PRIORIDAD_MIN = 1;
    /** Prioridad mínima. */
    public static final int PRIORIDAD_MAX = 5;

    private final int prioridad;
    private Estado estado = Estado.PENDIENTE;
    private String informe; // null hasta que se registre el informe

    /**
     * Crea una solicitud pendiente.
     *
     * @param prioridad valor entre {@value #PRIORIDAD_MIN} (urgente) y {@value #PRIORIDAD_MAX}
     * @throws IllegalArgumentException si la prioridad está fuera de rango
     */
    public SolicitudPrueba(int prioridad) {
        if (prioridad < PRIORIDAD_MIN || prioridad > PRIORIDAD_MAX) {
            throw new IllegalArgumentException("Prioridad fuera de rango");
        }
        this.prioridad = prioridad;
    }

    /**
     * Marca la prueba como realizada.
     *
     * @throws IllegalStateException si la solicitud ya estaba realizada
     */
    public void registrarRealizacion() {
        if (estado != Estado.PENDIENTE) {
            throw new IllegalStateException("Ya realizada");
        }
        estado = Estado.REALIZADA;
    }

    /**
     * Asocia el informe a una prueba ya realizada.
     *
     * @param texto contenido del informe, no vacío
     * @throws IllegalArgumentException si el texto es nulo o está en blanco
     * @throws IllegalStateException si la prueba no está realizada o ya tiene informe
     */
    public void registrarInforme(String texto) {
        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException("El informe no puede estar vacío");
        }
        if (estado != Estado.REALIZADA) {
            throw new IllegalStateException("La prueba aún no se ha realizado");
        }
        if (informe != null) {
            throw new IllegalStateException("La prueba ya tiene informe");
        }
        informe = texto;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public Estado getEstado() {
        return estado;
    }

    /** @return el informe, o {@code null} si todavía no se ha registrado */
    public String getInforme() {
        return informe;
    }
}
