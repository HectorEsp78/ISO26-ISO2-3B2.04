package es.uclm.esi.iso2.servidor.pruebas;

import es.uclm.esi.iso2.comun.dominio.EstadoPrueba;
import es.uclm.esi.iso2.comun.dominio.FiltroPruebas;
import es.uclm.esi.iso2.comun.dominio.Incidencia;
import es.uclm.esi.iso2.comun.dominio.Prueba;
import es.uclm.esi.iso2.comun.dominio.ResumenPrueba;
import es.uclm.esi.iso2.comun.dominio.TipoIncidencia;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.servidor.persistencia.ErrorPersistencia;
import es.uclm.esi.iso2.servidor.persistencia.FabricaConexiones;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Acceso a las tablas {@code prueba} e {@code incidencia} con JDBC (DEC-079).
 */
public class PruebaDAO {

    private static final String COLUMNAS = "id, id_solicitud, dni_paciente, id_medico, estado, "
            + "fecha_hora_realizacion, observaciones, motivo_cancelacion, fecha_registro";

    private final FabricaConexiones conexiones;

    public PruebaDAO(FabricaConexiones conexiones) {
        this.conexiones = conexiones;
    }

    // ---- Lectura ----

    /** Prueba con sus incidencias. */
    public Optional<Prueba> buscarPorId(long id) {
        return buscarUna("id = ?", id);
    }

    /** Prueba de una solicitud, si ya tiene (CA-05). */
    public Optional<Prueba> buscarPorSolicitud(long idSolicitud) {
        return buscarUna("id_solicitud = ?", idSolicitud);
    }

    /** Solicitudes de la lista que ya tienen prueba. */
    public List<Long> solicitudesConPrueba(List<Long> idsSolicitud) {
        List<Long> resultado = new ArrayList<>();
        if (idsSolicitud.isEmpty()) {
            return resultado;
        }
        String marcas = String.join(", ", java.util.Collections.nCopies(idsSolicitud.size(), "?"));
        try (Connection c = conexiones.abrir();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id_solicitud FROM prueba WHERE id_solicitud IN (" + marcas + ")")) {
            for (int i = 0; i < idsSolicitud.size(); i++) {
                ps.setLong(i + 1, idsSolicitud.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resultado.add(rs.getLong(1));
                }
            }
            return resultado;
        } catch (SQLException e) {
            throw new ErrorPersistencia("Error al comprobar las pruebas de las solicitudes", e);
        }
    }

    /**
     * Pruebas del médico que cumplen el filtro, de la más reciente a la más antigua. Un filtro
     * {@code null} no se aplica; {@code desde} y {@code hasta} incluyen el día completo y se
     * comparan con la fecha de registro.
     */
    public List<ResumenPrueba> buscar(String idMedico, FiltroPruebas filtro) {
        StringBuilder sql = new StringBuilder("SELECT p.id, p.id_solicitud, p.dni_paciente, p.id_medico, p.estado, "
                + "p.fecha_hora_realizacion, p.observaciones, p.motivo_cancelacion, p.fecha_registro, "
                + "(SELECT COUNT(*) FROM incidencia i WHERE i.id_prueba = p.id) FROM prueba p WHERE p.id_medico = ?");
        List<Object> valores = new ArrayList<>();
        valores.add(idMedico);
        if (filtro.dniPaciente() != null) {
            sql.append(" AND p.dni_paciente = ?");
            valores.add(filtro.dniPaciente());
        }
        if (filtro.idSolicitud() != null) {
            sql.append(" AND p.id_solicitud = ?");
            valores.add(filtro.idSolicitud());
        }
        if (filtro.desde() != null) {
            sql.append(" AND p.fecha_registro >= ?");
            valores.add(Timestamp.valueOf(filtro.desde().atStartOfDay()));
        }
        if (filtro.hasta() != null) {
            sql.append(" AND p.fecha_registro < ?");
            valores.add(Timestamp.valueOf(filtro.hasta().plusDays(1).atStartOfDay()));
        }
        if (filtro.estado() != null) {
            sql.append(" AND p.estado = ?");
            valores.add(filtro.estado().name());
        }
        sql.append(" ORDER BY p.fecha_registro DESC, p.id DESC");
        try (Connection c = conexiones.abrir(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < valores.size(); i++) {
                ps.setObject(i + 1, valores.get(i));
            }
            List<ResumenPrueba> resumenes = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    resumenes.add(new ResumenPrueba(rs.getLong(1), rs.getLong(2), rs.getString(3), rs.getString(4),
                            EstadoPrueba.valueOf(rs.getString(5)), fecha(rs, 6), texto(rs, 7), texto(rs, 8),
                            fecha(rs, 9), rs.getInt(10)));
                }
            }
            return resumenes;
        } catch (SQLException e) {
            throw new ErrorPersistencia("Error al buscar pruebas", e);
        }
    }

    // ---- Escritura ----

    /**
     * Guarda una prueba nueva con sus incidencias, en una sola transacción.
     *
     * @return la prueba con su id y los de sus incidencias
     * @throws PruebaException con {@code PRUEBA_YA_EXISTE} si la solicitud ya tiene prueba; la
     *                         restricción {@code UNIQUE} lo garantiza aunque dos peticiones lleguen
     *                         a la vez (CA-05)
     */
    public Prueba guardar(Prueba prueba) {
        String sql = "INSERT INTO prueba (id_solicitud, dni_paciente, id_medico, estado, fecha_hora_realizacion, "
                + "observaciones, motivo_cancelacion, fecha_registro) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = conexiones.abrir()) {
            c.setAutoCommit(false);
            try {
                long id;
                try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, prueba.idSolicitud());
                    ps.setString(2, prueba.dniPaciente());
                    ps.setString(3, prueba.idMedico());
                    ps.setString(4, prueba.estado().name());
                    ps.setTimestamp(5, prueba.fechaHoraRealizacion() == null ? null
                            : Timestamp.valueOf(prueba.fechaHoraRealizacion()));
                    ps.setString(6, vacioANulo(prueba.observaciones()));
                    ps.setString(7, vacioANulo(prueba.motivoCancelacion()));
                    ps.setTimestamp(8, Timestamp.valueOf(prueba.fechaRegistro()));
                    ps.executeUpdate();
                    try (ResultSet claves = ps.getGeneratedKeys()) {
                        claves.next();
                        id = claves.getLong(1);
                    }
                }
                List<Incidencia> guardadas = new ArrayList<>();
                for (Incidencia i : prueba.incidencias()) {
                    guardadas.add(insertarIncidencia(c, i.conIds(0, id)));
                }
                c.commit();
                return prueba.conId(id).conIncidencias(guardadas);
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            if (esDuplicado(e)) {
                String existente = buscarPorSolicitud(prueba.idSolicitud()).map(p -> String.valueOf(p.id()))
                        .orElse(null);
                throw new PruebaException(CodigoError.PRUEBA_YA_EXISTE, existente);
            }
            throw new ErrorPersistencia("Error al guardar la prueba", e);
        }
    }

    /** Añade una incidencia a una prueba existente. */
    public Incidencia guardarIncidencia(Incidencia incidencia) {
        try (Connection c = conexiones.abrir()) {
            return insertarIncidencia(c, incidencia);
        } catch (SQLException e) {
            throw new ErrorPersistencia("Error al guardar la incidencia", e);
        }
    }

    /** Borra una prueba y sus incidencias (compensación de DEC-076). */
    public void borrar(long idPrueba) {
        try (Connection c = conexiones.abrir()) {
            c.setAutoCommit(false);
            try {
                ejecutar(c, "DELETE FROM incidencia WHERE id_prueba = ?", idPrueba);
                ejecutar(c, "DELETE FROM prueba WHERE id = ?", idPrueba);
                c.commit();
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new ErrorPersistencia("Error al borrar la prueba", e);
        }
    }

    // ---- Auxiliares ----

    private Optional<Prueba> buscarUna(String condicion, long valor) {
        try (Connection c = conexiones.abrir();
             PreparedStatement ps = c.prepareStatement("SELECT " + COLUMNAS + " FROM prueba WHERE " + condicion)) {
            ps.setLong(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                long id = rs.getLong(1);
                Prueba base = new Prueba(id, rs.getLong(2), rs.getString(3), rs.getString(4),
                        EstadoPrueba.valueOf(rs.getString(5)), fecha(rs, 6), texto(rs, 7), texto(rs, 8),
                        fecha(rs, 9), List.of());
                return Optional.of(base.conIncidencias(incidenciasDe(c, id)));
            }
        } catch (SQLException e) {
            throw new ErrorPersistencia("Error al buscar la prueba", e);
        }
    }

    private static List<Incidencia> incidenciasDe(Connection c, long idPrueba) throws SQLException {
        List<Incidencia> lista = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT id, tipo, descripcion, fecha_hora FROM incidencia WHERE id_prueba = ? ORDER BY fecha_hora, id")) {
            ps.setLong(1, idPrueba);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Incidencia(rs.getLong(1), idPrueba, TipoIncidencia.valueOf(rs.getString(2)),
                            rs.getString(3), rs.getTimestamp(4).toLocalDateTime()));
                }
            }
        }
        return lista;
    }

    private static Incidencia insertarIncidencia(Connection c, Incidencia i) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO incidencia (id_prueba, tipo, descripcion, fecha_hora) VALUES (?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, i.idPrueba());
            ps.setString(2, i.tipo().name());
            ps.setString(3, i.descripcion());
            ps.setTimestamp(4, Timestamp.valueOf(i.fechaHora()));
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                claves.next();
                return i.conIds(claves.getLong(1), i.idPrueba());
            }
        }
    }

    private static void ejecutar(Connection c, String sql, long valor) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, valor);
            ps.executeUpdate();
        }
    }

    /** MySQL usa el código 1062 y el SQLSTATE 23xxx; H2 usa 23505. Ambos son violación de restricción. */
    private static boolean esDuplicado(SQLException e) {
        String estado = e.getSQLState();
        return estado != null && estado.startsWith("23");
    }

    private static LocalDateTime fecha(ResultSet rs, int columna) throws SQLException {
        Timestamp t = rs.getTimestamp(columna);
        return t == null ? null : t.toLocalDateTime();
    }

    private static String texto(ResultSet rs, int columna) throws SQLException {
        String valor = rs.getString(columna);
        return valor == null ? "" : valor;
    }

    private static String vacioANulo(String texto) {
        return texto == null || texto.isEmpty() ? null : texto;
    }
}
