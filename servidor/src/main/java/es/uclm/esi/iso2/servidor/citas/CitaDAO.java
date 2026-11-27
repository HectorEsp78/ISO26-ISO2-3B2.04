package es.uclm.esi.iso2.servidor.citas;

import es.uclm.esi.iso2.comun.dominio.Cita;
import es.uclm.esi.iso2.comun.dominio.EstadoCita;
import es.uclm.esi.iso2.comun.dominio.Recurso;
import es.uclm.esi.iso2.comun.dominio.TipoPrueba;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.servidor.persistencia.ErrorPersistencia;
import es.uclm.esi.iso2.servidor.persistencia.FabricaConexiones;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Acceso a las tablas {@code cita}, {@code tipo_prueba} y {@code recurso} con JDBC (DEC-066).
 */
public class CitaDAO {

    private static final String COLUMNAS =
            "id, dni_paciente, id_tipo_prueba, id_recurso, inicio, duracion_minutos, estado, motivo_cancelacion";

    /** Citas PROGRAMADA que se solapan con [inicio, fin), sin contar la cita indicada (RN-06, RN-07). */
    private static final String SOLAPE = "SELECT id FROM cita WHERE estado = 'PROGRAMADA' AND %s = ? "
            + "AND inicio < ? AND fin > ? AND id <> ? ORDER BY inicio";

    private final FabricaConexiones conexiones;

    public CitaDAO(FabricaConexiones conexiones) {
        this.conexiones = conexiones;
    }

    // ---- Catálogo ----

    public List<TipoPrueba> listarTiposPrueba() {
        List<TipoPrueba> tipos = new ArrayList<>();
        try (Connection c = conexiones.abrir();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT id, nombre, duracion_minutos FROM tipo_prueba ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tipos.add(new TipoPrueba(rs.getInt(1), rs.getString(2), rs.getInt(3)));
            }
            return tipos;
        } catch (SQLException e) {
            throw new ErrorPersistencia("Error al leer los tipos de prueba", e);
        }
    }

    public List<Recurso> listarRecursos() {
        List<Recurso> recursos = new ArrayList<>();
        try (Connection c = conexiones.abrir();
             PreparedStatement ps = c.prepareStatement("SELECT id, nombre FROM recurso ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                recursos.add(new Recurso(rs.getInt(1), rs.getString(2)));
            }
            return recursos;
        } catch (SQLException e) {
            throw new ErrorPersistencia("Error al leer los recursos", e);
        }
    }

    public Optional<TipoPrueba> buscarTipoPrueba(int id) {
        return listarTiposPrueba().stream().filter(t -> t.id() == id).findFirst();
    }

    public Optional<Recurso> buscarRecurso(int id) {
        return listarRecursos().stream().filter(r -> r.id() == id).findFirst();
    }

    // ---- Consultas de citas ----

    public Optional<Cita> buscarPorId(long id) {
        String sql = "SELECT " + COLUMNAS + " FROM cita WHERE id = ?";
        try (Connection c = conexiones.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(leer(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorPersistencia("Error al buscar la cita", e);
        }
    }

    /**
     * Citas que cumplen los filtros, ordenadas por inicio. Un filtro {@code null} no se aplica.
     * {@code desde} y {@code hasta} incluyen el día completo.
     */
    public List<Cita> buscar(String dni, LocalDate desde, LocalDate hasta, EstadoCita estado) {
        StringBuilder sql = new StringBuilder("SELECT " + COLUMNAS + " FROM cita WHERE 1 = 1");
        List<Object> valores = new ArrayList<>();
        if (dni != null) {
            sql.append(" AND dni_paciente = ?");
            valores.add(dni);
        }
        if (desde != null) {
            sql.append(" AND inicio >= ?");
            valores.add(desde.atStartOfDay());
        }
        if (hasta != null) {
            sql.append(" AND inicio < ?");
            valores.add(hasta.plusDays(1).atStartOfDay());
        }
        if (estado != null) {
            sql.append(" AND estado = ?");
            valores.add(estado.name());
        }
        sql.append(" ORDER BY inicio, id");
        try (Connection c = conexiones.abrir(); PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < valores.size(); i++) {
                ps.setObject(i + 1, valores.get(i));
            }
            List<Cita> citas = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    citas.add(leer(rs));
                }
            }
            return citas;
        } catch (SQLException e) {
            throw new ErrorPersistencia("Error al buscar citas", e);
        }
    }

    // ---- Escritura ----

    /**
     * Guarda la cita (la inserta si su id es 0 y si no la actualiza) solo si no choca con otra
     * cita {@code PROGRAMADA} del mismo recurso o del mismo paciente (RN-04 a RN-07).
     *
     * <p>Todo se hace en una sola transacción (DEC-043 y DEC-066): primero se bloquean las filas
     * del recurso y del paciente con {@code SELECT ... FOR UPDATE}, de modo que si llegan dos
     * peticiones a la vez para el mismo recurso o paciente, la segunda espera a que termine la
     * primera y ya ve su cita al comprobar los solapes.</p>
     *
     * @return la cita guardada, con su id
     * @throws CitaException con {@code SOLAPE_RECURSO} o {@code SOLAPE_PACIENTE} y el id de la
     *                       cita con la que choca; en ese caso no se guarda nada
     */
    public Cita guardarSinSolapes(Cita cita) {
        try (Connection c = conexiones.abrir()) {
            c.setAutoCommit(false);
            try {
                bloquear(c, "SELECT id FROM recurso WHERE id = ? FOR UPDATE", cita.idRecurso());
                bloquear(c, "SELECT dni FROM paciente WHERE dni = ? FOR UPDATE", cita.dniPaciente());
                comprobarSolape(c, "id_recurso", cita.idRecurso(), cita, CodigoError.SOLAPE_RECURSO);
                comprobarSolape(c, "dni_paciente", cita.dniPaciente(), cita, CodigoError.SOLAPE_PACIENTE);
                Cita guardada = cita.id() == 0 ? insertar(c, cita) : actualizar(c, cita);
                c.commit();
                return guardada;
            } catch (SQLException | RuntimeException e) {
                c.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new ErrorPersistencia("Error al guardar la cita", e);
        }
    }

    /**
     * Cambia el estado de una cita solo si sigue {@code PROGRAMADA}. Comprobarlo en el propio
     * {@code UPDATE} evita que dos peticiones a la vez cambien la misma cita.
     *
     * @return {@code true} si se ha cambiado
     */
    public boolean cambiarEstadoSiProgramada(long id, EstadoCita nuevo, String motivo) {
        String sql = "UPDATE cita SET estado = ?, motivo_cancelacion = ? WHERE id = ? AND estado = 'PROGRAMADA'";
        try (Connection c = conexiones.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nuevo.name());
            ps.setString(2, motivo);
            ps.setLong(3, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new ErrorPersistencia("Error al cambiar el estado de la cita", e);
        }
    }

    /**
     * Pasa a {@code CANCELADA} todas las citas {@code PROGRAMADA} del paciente (DEC-048).
     *
     * @return cuántas se han cancelado
     */
    public int cancelarPendientes(String dni, String motivo) {
        String sql = "UPDATE cita SET estado = 'CANCELADA', motivo_cancelacion = ? "
                + "WHERE dni_paciente = ? AND estado = 'PROGRAMADA'";
        try (Connection c = conexiones.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, motivo);
            ps.setString(2, dni);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ErrorPersistencia("Error al cancelar las citas pendientes", e);
        }
    }

    // ---- Auxiliares ----

    private static void bloquear(Connection c, String sql, Object clave) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, clave);
            ps.executeQuery().close();
        }
    }

    private static void comprobarSolape(Connection c, String columna, Object valor, Cita cita,
            CodigoError codigo) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(String.format(SOLAPE, columna))) {
            ps.setObject(1, valor);
            ps.setObject(2, cita.fin());
            ps.setObject(3, cita.inicio());
            ps.setLong(4, cita.id());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    throw new CitaException(codigo, String.valueOf(rs.getLong(1)));
                }
            }
        }
    }

    private static Cita insertar(Connection c, Cita cita) throws SQLException {
        String sql = "INSERT INTO cita (dni_paciente, id_tipo_prueba, id_recurso, inicio, fin, duracion_minutos, "
                + "estado, motivo_cancelacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignar(ps, cita);
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                claves.next();
                return cita.conId(claves.getLong(1));
            }
        }
    }

    private static Cita actualizar(Connection c, Cita cita) throws SQLException {
        String sql = "UPDATE cita SET dni_paciente = ?, id_tipo_prueba = ?, id_recurso = ?, inicio = ?, fin = ?, "
                + "duracion_minutos = ?, estado = ?, motivo_cancelacion = ? WHERE id = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, cita);
            ps.setLong(9, cita.id());
            ps.executeUpdate();
            return cita;
        }
    }

    private static void asignar(PreparedStatement ps, Cita cita) throws SQLException {
        ps.setString(1, cita.dniPaciente());
        ps.setInt(2, cita.idTipoPrueba());
        ps.setInt(3, cita.idRecurso());
        ps.setObject(4, cita.inicio());
        ps.setObject(5, cita.fin());
        ps.setInt(6, cita.duracionMinutos());
        ps.setString(7, cita.estado().name());
        ps.setString(8, cita.motivoCancelacion().isEmpty() ? null : cita.motivoCancelacion());
    }

    private static Cita leer(ResultSet rs) throws SQLException {
        return new Cita(
                rs.getLong("id"),
                rs.getString("dni_paciente"),
                rs.getInt("id_tipo_prueba"),
                rs.getInt("id_recurso"),
                rs.getObject("inicio", LocalDateTime.class),
                rs.getInt("duracion_minutos"),
                EstadoCita.valueOf(rs.getString("estado")),
                rs.getString("motivo_cancelacion"));
    }
}
