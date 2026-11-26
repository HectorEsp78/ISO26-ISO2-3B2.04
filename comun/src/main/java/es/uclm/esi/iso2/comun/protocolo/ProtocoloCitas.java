package es.uclm.esi.iso2.comun.protocolo;

import es.uclm.esi.iso2.comun.dominio.Cita;
import es.uclm.esi.iso2.comun.dominio.DatosCita;
import es.uclm.esi.iso2.comun.dominio.EstadoCita;
import es.uclm.esi.iso2.comun.dominio.Recurso;
import es.uclm.esi.iso2.comun.dominio.TipoPrueba;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Operaciones del protocolo de CU13 (DEC-065). Usa el mismo formato y escape que
 * {@link Protocolo} (DEC-057).
 *
 * <ul>
 *   <li>{@code <datos>}: 5 campos {@code dni;idTipoPrueba;idRecurso;inicio;duracion}.</li>
 *   <li>{@code <cita>}: 9 campos {@code id;dni;idTipoPrueba;idRecurso;inicio;duracion;fin;estado;motivo}.</li>
 *   <li>Fecha y hora en ISO ({@code aaaa-mm-ddThh:mm}); fecha sola en ISO ({@code aaaa-mm-dd}).</li>
 *   <li>Un campo vacío en una petición significa «no indicado».</li>
 * </ul>
 */
public final class ProtocoloCitas {

    public static final String ASIGNAR_CITA = "ASIGNAR_CITA";
    public static final String BUSCAR_CITAS = "BUSCAR_CITAS";
    public static final String REPROGRAMAR_CITA = "REPROGRAMAR_CITA";
    public static final String CANCELAR_CITA = "CANCELAR_CITA";
    public static final String LISTAR_TIPOS_PRUEBA = "LISTAR_TIPOS_PRUEBA";
    public static final String LISTAR_RECURSOS = "LISTAR_RECURSOS";

    /** Número de campos de {@code <datos>}. */
    public static final int CAMPOS_DATOS = 5;
    /** Número de campos de {@code <cita>}. */
    public static final int CAMPOS_CITA = 9;

    /** Nombres de los campos que se indican en los errores {@code CAMPO_OBLIGATORIO} y {@code CAMPO_NO_VALIDO}. */
    public static final String CAMPO_ID = "id";
    public static final String CAMPO_DNI = "dni";
    public static final String CAMPO_TIPO_PRUEBA = "idTipoPrueba";
    public static final String CAMPO_RECURSO = "idRecurso";
    public static final String CAMPO_INICIO = "inicio";
    public static final String CAMPO_DURACION = "duracion";
    public static final String CAMPO_MOTIVO = "motivo";
    public static final String CAMPO_DESDE = "desde";
    public static final String CAMPO_HASTA = "hasta";
    public static final String CAMPO_ESTADO = "estado";

    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private ProtocoloCitas() {
    }

    // ---- Fechas ----

    /** Fecha y hora en el formato del protocolo, o cadena vacía si es {@code null}. */
    public static String texto(LocalDateTime fechaHora) {
        return fechaHora == null ? "" : fechaHora.format(FECHA_HORA);
    }

    /** Fecha en el formato del protocolo, o cadena vacía si es {@code null}. */
    public static String texto(LocalDate fecha) {
        return fecha == null ? "" : fecha.toString();
    }

    // ---- Citas ----

    /** Los 9 campos de una cita. */
    public static List<String> camposCita(Cita cita) {
        return List.of(
                String.valueOf(cita.id()),
                cita.dniPaciente(),
                String.valueOf(cita.idTipoPrueba()),
                String.valueOf(cita.idRecurso()),
                texto(cita.inicio()),
                String.valueOf(cita.duracionMinutos()),
                texto(cita.fin()),
                cita.estado().name(),
                cita.motivoCancelacion());
    }

    /**
     * Construye una cita a partir de los 9 campos que empiezan en la posición indicada. El campo
     * {@code fin} se ignora, porque se calcula.
     *
     * @throws IllegalArgumentException si faltan campos o alguno no tiene el formato correcto
     */
    public static Cita citaDesdeCampos(List<String> c, int desde) {
        if (c.size() - desde < CAMPOS_CITA) {
            throw new IllegalArgumentException("Faltan campos de la cita");
        }
        try {
            return new Cita(Long.parseLong(c.get(desde)), c.get(desde + 1),
                    Integer.parseInt(c.get(desde + 2)), Integer.parseInt(c.get(desde + 3)),
                    LocalDateTime.parse(c.get(desde + 4)), Integer.parseInt(c.get(desde + 5)),
                    EstadoCita.valueOf(c.get(desde + 7)), c.get(desde + 8));
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Cita no válida", e);
        }
    }

    // ---- Peticiones (las usa el cliente) ----

    public static String peticionAsignar(DatosCita d) {
        return Protocolo.componer(ASIGNAR_CITA, texto(d.dniPaciente()), texto(d.idTipoPrueba()), texto(d.idRecurso()),
                texto(d.inicio()), texto(d.duracionMinutos()));
    }

    public static String peticionBuscar(String dni, LocalDate desde, LocalDate hasta, EstadoCita estado) {
        return Protocolo.componer(BUSCAR_CITAS, texto(dni), texto(desde), texto(hasta),
                estado == null ? "" : estado.name());
    }

    public static String peticionReprogramar(long id, Integer idRecurso, LocalDateTime inicio, Integer duracion) {
        return Protocolo.componer(REPROGRAMAR_CITA, String.valueOf(id), texto(idRecurso), texto(inicio),
                texto(duracion));
    }

    public static String peticionCancelar(long id, String motivo) {
        return Protocolo.componer(CANCELAR_CITA, String.valueOf(id), motivo);
    }

    // ---- Respuestas (las usa el servidor) ----

    /** {@code OK;<cita>}. */
    public static String respuestaOk(Cita cita) {
        List<String> campos = new ArrayList<>();
        campos.add(Protocolo.OK);
        campos.addAll(camposCita(cita));
        return Protocolo.componer(campos);
    }

    /** {@code OK;<n>;<cita1>;<cita2>…}. */
    public static String respuestaCitas(List<Cita> citas) {
        List<String> campos = new ArrayList<>();
        campos.add(Protocolo.OK);
        campos.add(String.valueOf(citas.size()));
        citas.forEach(cita -> campos.addAll(camposCita(cita)));
        return Protocolo.componer(campos);
    }

    /** {@code OK;<n>;<id>;<nombre>;<duracion>…}. */
    public static String respuestaTipos(List<TipoPrueba> tipos) {
        List<String> campos = new ArrayList<>();
        campos.add(Protocolo.OK);
        campos.add(String.valueOf(tipos.size()));
        for (TipoPrueba t : tipos) {
            campos.add(String.valueOf(t.id()));
            campos.add(t.nombre());
            campos.add(String.valueOf(t.duracionMinutos()));
        }
        return Protocolo.componer(campos);
    }

    /** {@code OK;<n>;<id>;<nombre>…}. */
    public static String respuestaRecursos(List<Recurso> recursos) {
        List<String> campos = new ArrayList<>();
        campos.add(Protocolo.OK);
        campos.add(String.valueOf(recursos.size()));
        for (Recurso r : recursos) {
            campos.add(String.valueOf(r.id()));
            campos.add(r.nombre());
        }
        return Protocolo.componer(campos);
    }

    /**
     * {@code ERROR;<código>} o {@code ERROR;<código>;<detalle>}. El detalle es el nombre de un
     * campo (por ejemplo {@code inicio}) o, en un solape, el id de la cita con la que choca.
     */
    public static String respuestaError(CodigoError codigo, String detalle) {
        return detalle == null || detalle.isEmpty()
                ? Protocolo.componer(Protocolo.ERROR, codigo.name())
                : Protocolo.componer(Protocolo.ERROR, codigo.name(), detalle);
    }

    // ---- Lectura de listas (las usa el cliente) ----

    /**
     * Citas de una respuesta {@code OK;<n>;<cita>…} (los campos que siguen a {@code OK}).
     *
     * @throws IllegalArgumentException si el número de campos no cuadra con {@code n}
     */
    public static List<Cita> citasDesdeCampos(List<String> campos) {
        int n = cantidad(campos, CAMPOS_CITA);
        List<Cita> citas = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            citas.add(citaDesdeCampos(campos, 1 + i * CAMPOS_CITA));
        }
        return citas;
    }

    /** Tipos de prueba de una respuesta {@code OK;<n>;…}. */
    public static List<TipoPrueba> tiposDesdeCampos(List<String> campos) {
        int n = cantidad(campos, 3);
        List<TipoPrueba> tipos = new ArrayList<>(n);
        for (int i = 0, p = 1; i < n; i++, p += 3) {
            tipos.add(new TipoPrueba(Integer.parseInt(campos.get(p)), campos.get(p + 1),
                    Integer.parseInt(campos.get(p + 2))));
        }
        return tipos;
    }

    /** Recursos de una respuesta {@code OK;<n>;…}. */
    public static List<Recurso> recursosDesdeCampos(List<String> campos) {
        int n = cantidad(campos, 2);
        List<Recurso> recursos = new ArrayList<>(n);
        for (int i = 0, p = 1; i < n; i++, p += 2) {
            recursos.add(new Recurso(Integer.parseInt(campos.get(p)), campos.get(p + 1)));
        }
        return recursos;
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor;
    }

    private static String texto(Integer numero) {
        return numero == null ? "" : numero.toString();
    }

    /** Lee {@code n} y comprueba que detrás vienen exactamente {@code n} elementos del tamaño indicado. */
    private static int cantidad(List<String> campos, int tamano) {
        if (campos.isEmpty()) {
            throw new IllegalArgumentException("Falta el número de elementos");
        }
        int n = Integer.parseInt(campos.get(0));
        if (n < 0 || campos.size() != 1 + n * tamano) {
            throw new IllegalArgumentException("El número de campos no cuadra con " + n + " elementos");
        }
        return n;
    }
}
