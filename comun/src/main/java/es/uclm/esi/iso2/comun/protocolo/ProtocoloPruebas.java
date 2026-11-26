package es.uclm.esi.iso2.comun.protocolo;

import es.uclm.esi.iso2.comun.dominio.EstadoPrueba;
import es.uclm.esi.iso2.comun.dominio.FiltroPruebas;
import es.uclm.esi.iso2.comun.dominio.Incidencia;
import es.uclm.esi.iso2.comun.dominio.NuevaIncidencia;
import es.uclm.esi.iso2.comun.dominio.Prueba;
import es.uclm.esi.iso2.comun.dominio.ResumenPrueba;
import es.uclm.esi.iso2.comun.dominio.SolicitudPendiente;
import es.uclm.esi.iso2.comun.dominio.TipoIncidencia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Operaciones del protocolo de CU10 (DEC-078). Usa el mismo formato y escape que
 * {@link Protocolo} (DEC-057). Todas las peticiones llevan el identificador del médico como
 * primer campo.
 *
 * <ul>
 *   <li>{@code <pendiente>}: 7 campos {@code idSolicitud;dni;nombrePaciente;tipoPrueba;prioridad;idCita;inicioCita}.</li>
 *   <li>{@code <incidenciaNueva>}: 2 campos {@code tipo;descripcion}.</li>
 *   <li>{@code <incidencia>}: 4 campos {@code id;tipo;descripcion;fechaHora}.</li>
 *   <li>{@code <resumen>}: 9 campos de la prueba más {@code numIncidencias}.</li>
 *   <li>{@code <prueba>}: 9 campos {@code id;idSolicitud;dni;idMedico;estado;fechaHoraRealizacion;observaciones;
 *       motivoCancelacion;fechaRegistro}, luego {@code n} y las {@code n} incidencias.</li>
 *   <li>Un campo opcional vacío se envía como cadena vacía.</li>
 * </ul>
 */
public final class ProtocoloPruebas {

    public static final String LISTAR_SOLICITUDES_PENDIENTES = "LISTAR_SOLICITUDES_PENDIENTES";
    public static final String REGISTRAR_PRUEBA = "REGISTRAR_PRUEBA";
    public static final String CANCELAR_PRUEBA = "CANCELAR_PRUEBA";
    public static final String ANADIR_INCIDENCIA = "ANADIR_INCIDENCIA";
    public static final String BUSCAR_PRUEBAS = "BUSCAR_PRUEBAS";
    public static final String OBTENER_PRUEBA = "OBTENER_PRUEBA";

    public static final int CAMPOS_PENDIENTE = 7;
    public static final int CAMPOS_INCIDENCIA_NUEVA = 2;
    public static final int CAMPOS_INCIDENCIA = 4;
    /** Campos de la prueba sin contar el número de incidencias ni las incidencias. */
    public static final int CAMPOS_BASE_PRUEBA = 9;
    public static final int CAMPOS_RESUMEN = 10;

    /** Nombres de los campos que se indican en los errores de campo. */
    public static final String CAMPO_MEDICO = "idMedico";
    public static final String CAMPO_DNI = "dni";
    public static final String CAMPO_ID_SOLICITUD = "idSolicitud";
    public static final String CAMPO_ID_PRUEBA = "idPrueba";
    public static final String CAMPO_FECHA_HORA = "fechaHora";
    public static final String CAMPO_OBSERVACIONES = "observaciones";
    public static final String CAMPO_MOTIVO = "motivo";
    public static final String CAMPO_TIPO = "tipo";
    public static final String CAMPO_DESCRIPCION = "descripcion";
    public static final String CAMPO_DESDE = "desde";
    public static final String CAMPO_HASTA = "hasta";
    public static final String CAMPO_ESTADO = "estado";

    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final DateTimeFormatter FECHA_HORA_SEGUNDOS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private ProtocoloPruebas() {
    }

    // ---- Fechas ----

    /** Fecha y hora sin segundos ({@code aaaa-mm-ddThh:mm}), o cadena vacía si es {@code null}. */
    public static String texto(LocalDateTime fechaHora) {
        return fechaHora == null ? "" : fechaHora.format(FECHA_HORA);
    }

    /** Fecha y hora con segundos, para las fechas de registro que pone el servidor. */
    public static String textoConSegundos(LocalDateTime fechaHora) {
        return fechaHora == null ? "" : fechaHora.format(FECHA_HORA_SEGUNDOS);
    }

    /** Fecha ({@code aaaa-mm-dd}), o cadena vacía si es {@code null}. */
    public static String texto(LocalDate fecha) {
        return fecha == null ? "" : fecha.toString();
    }

    private static String texto(String valor) {
        return valor == null ? "" : valor;
    }

    private static LocalDateTime fechaHoraONulo(String valor) {
        return valor.isEmpty() ? null : LocalDateTime.parse(valor);
    }

    // ---- Elementos ----

    public static List<String> camposPendiente(SolicitudPendiente p) {
        return List.of(String.valueOf(p.idSolicitud()), p.dniPaciente(), p.nombrePaciente(), p.tipoPrueba(),
                p.prioridad(), String.valueOf(p.idCita()), texto(p.inicioCita()));
    }

    public static SolicitudPendiente pendienteDesdeCampos(List<String> c, int desde) {
        try {
            return new SolicitudPendiente(Long.parseLong(c.get(desde)), c.get(desde + 1), c.get(desde + 2),
                    c.get(desde + 3), c.get(desde + 4), Long.parseLong(c.get(desde + 5)),
                    LocalDateTime.parse(c.get(desde + 6)));
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Solicitud pendiente no válida", e);
        }
    }

    public static List<String> camposIncidencia(Incidencia i) {
        return List.of(String.valueOf(i.id()), i.tipo().name(), i.descripcion(), textoConSegundos(i.fechaHora()));
    }

    /** Incidencia de 4 campos; el id de la prueba lo pone quien llama. */
    public static Incidencia incidenciaDesdeCampos(List<String> c, int desde, long idPrueba) {
        try {
            return new Incidencia(Long.parseLong(c.get(desde)), idPrueba, TipoIncidencia.valueOf(c.get(desde + 1)),
                    c.get(desde + 2), LocalDateTime.parse(c.get(desde + 3)));
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Incidencia no válida", e);
        }
    }

    private static List<String> camposBase(long id, long idSolicitud, String dni, String medico, EstadoPrueba estado,
            LocalDateTime realizacion, String observaciones, String motivo, LocalDateTime registro) {
        return List.of(String.valueOf(id), String.valueOf(idSolicitud), dni, medico, estado.name(),
                texto(realizacion), observaciones, motivo, textoConSegundos(registro));
    }

    public static List<String> camposResumen(ResumenPrueba r) {
        List<String> campos = new ArrayList<>(camposBase(r.id(), r.idSolicitud(), r.dniPaciente(), r.idMedico(),
                r.estado(), r.fechaHoraRealizacion(), r.observaciones(), r.motivoCancelacion(), r.fechaRegistro()));
        campos.add(String.valueOf(r.numIncidencias()));
        return campos;
    }

    public static ResumenPrueba resumenDesdeCampos(List<String> c, int desde) {
        try {
            return new ResumenPrueba(Long.parseLong(c.get(desde)), Long.parseLong(c.get(desde + 1)),
                    c.get(desde + 2), c.get(desde + 3), EstadoPrueba.valueOf(c.get(desde + 4)),
                    fechaHoraONulo(c.get(desde + 5)), c.get(desde + 6), c.get(desde + 7),
                    LocalDateTime.parse(c.get(desde + 8)), Integer.parseInt(c.get(desde + 9)));
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Resumen de prueba no válido", e);
        }
    }

    /** Los campos de una prueba: 9, {@code n} y las incidencias. */
    public static List<String> camposPrueba(Prueba p) {
        List<String> campos = new ArrayList<>(camposBase(p.id(), p.idSolicitud(), p.dniPaciente(), p.idMedico(),
                p.estado(), p.fechaHoraRealizacion(), p.observaciones(), p.motivoCancelacion(), p.fechaRegistro()));
        campos.add(String.valueOf(p.incidencias().size()));
        p.incidencias().forEach(i -> campos.addAll(camposIncidencia(i)));
        return campos;
    }

    /**
     * Prueba a partir de los campos que siguen a {@code OK}.
     *
     * @throws IllegalArgumentException si el número de campos no cuadra o algún valor no es válido
     */
    public static Prueba pruebaDesdeCampos(List<String> c) {
        try {
            if (c.size() < CAMPOS_BASE_PRUEBA + 1) {
                throw new IllegalArgumentException("Faltan campos de la prueba");
            }
            long id = Long.parseLong(c.get(0));
            int n = Integer.parseInt(c.get(CAMPOS_BASE_PRUEBA));
            if (n < 0 || c.size() != CAMPOS_BASE_PRUEBA + 1 + n * CAMPOS_INCIDENCIA) {
                throw new IllegalArgumentException("El número de campos no cuadra con " + n + " incidencias");
            }
            List<Incidencia> incidencias = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                incidencias.add(incidenciaDesdeCampos(c, CAMPOS_BASE_PRUEBA + 1 + i * CAMPOS_INCIDENCIA, id));
            }
            return new Prueba(id, Long.parseLong(c.get(1)), c.get(2), c.get(3), EstadoPrueba.valueOf(c.get(4)),
                    fechaHoraONulo(c.get(5)), c.get(6), c.get(7), LocalDateTime.parse(c.get(8)), incidencias);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Prueba no válida", e);
        }
    }

    // ---- Peticiones (las usa el cliente) ----

    public static String peticionListarPendientes(String idMedico, String dni) {
        return Protocolo.componer(LISTAR_SOLICITUDES_PENDIENTES, texto(idMedico), texto(dni));
    }

    public static String peticionRegistrar(String idMedico, long idSolicitud, LocalDateTime fechaHora,
            String observaciones, List<NuevaIncidencia> incidencias) {
        List<String> campos = new ArrayList<>(List.of(REGISTRAR_PRUEBA, texto(idMedico), String.valueOf(idSolicitud),
                texto(fechaHora), texto(observaciones), String.valueOf(incidencias.size())));
        for (NuevaIncidencia i : incidencias) {
            campos.add(i.tipo() == null ? "" : i.tipo().name());
            campos.add(texto(i.descripcion()));
        }
        return Protocolo.componer(campos);
    }

    public static String peticionCancelar(String idMedico, long idSolicitud, String motivo) {
        return Protocolo.componer(CANCELAR_PRUEBA, texto(idMedico), String.valueOf(idSolicitud), texto(motivo));
    }

    public static String peticionAnadirIncidencia(String idMedico, long idPrueba, NuevaIncidencia incidencia) {
        return Protocolo.componer(ANADIR_INCIDENCIA, texto(idMedico), String.valueOf(idPrueba),
                incidencia.tipo() == null ? "" : incidencia.tipo().name(), texto(incidencia.descripcion()));
    }

    public static String peticionBuscar(String idMedico, FiltroPruebas f) {
        return Protocolo.componer(BUSCAR_PRUEBAS, texto(idMedico), texto(f.dniPaciente()),
                f.idSolicitud() == null ? "" : f.idSolicitud().toString(), texto(f.desde()), texto(f.hasta()),
                f.estado() == null ? "" : f.estado().name());
    }

    public static String peticionObtener(String idMedico, long idPrueba) {
        return Protocolo.componer(OBTENER_PRUEBA, texto(idMedico), String.valueOf(idPrueba));
    }

    // ---- Respuestas (las usa el servidor) ----

    /** {@code OK;<n>;<pendiente>…}. */
    public static String respuestaPendientes(List<SolicitudPendiente> pendientes) {
        List<String> campos = new ArrayList<>(List.of(Protocolo.OK, String.valueOf(pendientes.size())));
        pendientes.forEach(p -> campos.addAll(camposPendiente(p)));
        return Protocolo.componer(campos);
    }

    /** {@code OK;<prueba>}. */
    public static String respuestaOk(Prueba prueba) {
        List<String> campos = new ArrayList<>();
        campos.add(Protocolo.OK);
        campos.addAll(camposPrueba(prueba));
        return Protocolo.componer(campos);
    }

    /** {@code OK;<incidencia>}. */
    public static String respuestaOk(Incidencia incidencia) {
        List<String> campos = new ArrayList<>();
        campos.add(Protocolo.OK);
        campos.addAll(camposIncidencia(incidencia));
        return Protocolo.componer(campos);
    }

    /** {@code OK;<n>;<resumen>…}. */
    public static String respuestaResumenes(List<ResumenPrueba> resumenes) {
        List<String> campos = new ArrayList<>(List.of(Protocolo.OK, String.valueOf(resumenes.size())));
        resumenes.forEach(r -> campos.addAll(camposResumen(r)));
        return Protocolo.componer(campos);
    }

    // ---- Lectura de listas (las usa el cliente) ----

    /** Pendientes de una respuesta {@code OK;<n>;…} (los campos que siguen a {@code OK}). */
    public static List<SolicitudPendiente> pendientesDesdeCampos(List<String> campos) {
        int n = cantidad(campos, CAMPOS_PENDIENTE);
        List<SolicitudPendiente> lista = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            lista.add(pendienteDesdeCampos(campos, 1 + i * CAMPOS_PENDIENTE));
        }
        return lista;
    }

    /** Resúmenes de una respuesta {@code OK;<n>;…}. */
    public static List<ResumenPrueba> resumenesDesdeCampos(List<String> campos) {
        int n = cantidad(campos, CAMPOS_RESUMEN);
        List<ResumenPrueba> lista = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            lista.add(resumenDesdeCampos(campos, 1 + i * CAMPOS_RESUMEN));
        }
        return lista;
    }

    private static int cantidad(List<String> campos, int tamano) {
        if (campos.isEmpty()) {
            throw new IllegalArgumentException("Falta el número de elementos");
        }
        int n;
        try {
            n = Integer.parseInt(campos.get(0));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Número de elementos no válido", e);
        }
        if (n < 0 || campos.size() != 1 + n * tamano) {
            throw new IllegalArgumentException("El número de campos no cuadra con " + n + " elementos");
        }
        return n;
    }
}
