package es.uclm.esi.iso2.servidor.pacientes;

import es.uclm.esi.iso2.comun.dominio.EstadoPaciente;
import es.uclm.esi.iso2.comun.dominio.Paciente;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;
import es.uclm.esi.iso2.servidor.persistencia.ErrorPersistencia;
import es.uclm.esi.iso2.servidor.persistencia.FabricaConexiones;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

/**
 * Acceso a la tabla {@code paciente} con JDBC (DEC-069). Los campos opcionales vacíos se
 * guardan como {@code NULL}.
 */
public class PacienteDAO {

    private static final String COLUMNAS =
            "dni, tarjeta_sanitaria, nombre, apellidos, fecha_nacimiento, sexo, telefono, correo, direccion, estado";

    /** Nombre de la restricción de unicidad de la tarjeta en {@code 01_esquema.sql}. */
    private static final String RESTRICCION_TARJETA = "uk_paciente_tarjeta";

    private final FabricaConexiones conexiones;

    public PacienteDAO(FabricaConexiones conexiones) {
        this.conexiones = conexiones;
    }

    public Optional<Paciente> buscar(String dni) {
        String sql = "SELECT " + COLUMNAS + " FROM paciente WHERE dni = ?";
        try (Connection c = conexiones.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(leer(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new ErrorPersistencia("Error al buscar el paciente", e);
        }
    }

    /** Indica si la tarjeta la tiene un paciente con un DNI distinto del indicado. */
    public boolean tarjetaUsadaPorOtro(String tarjeta, String dni) {
        String sql = "SELECT 1 FROM paciente WHERE tarjeta_sanitaria = ? AND dni <> ?";
        try (Connection c = conexiones.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tarjeta);
            ps.setString(2, dni);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new ErrorPersistencia("Error al comprobar la tarjeta sanitaria", e);
        }
    }

    /**
     * Inserta un paciente.
     *
     * @throws PacienteException con {@code DNI_DUPLICADO} o {@code TARJETA_DUPLICADA} si la base
     *                           de datos rechaza un duplicado (por ejemplo, dos altas a la vez)
     */
    public void insertar(Paciente p) {
        String sql = "INSERT INTO paciente (" + COLUMNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = conexiones.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, p.dni());
            asignarDatos(ps, p, 2);
            ps.setString(10, p.estado().name());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw traducir(e, "Error al insertar el paciente");
        }
    }

    /**
     * Guarda los datos de un paciente existente (todo salvo el DNI y el estado).
     *
     * @return {@code true} si existía el paciente
     */
    public boolean actualizar(Paciente p) {
        String sql = "UPDATE paciente SET tarjeta_sanitaria = ?, nombre = ?, apellidos = ?, fecha_nacimiento = ?, "
                + "sexo = ?, telefono = ?, correo = ?, direccion = ? WHERE dni = ?";
        try (Connection c = conexiones.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            asignarDatos(ps, p, 1);
            ps.setString(9, p.dni());
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw traducir(e, "Error al actualizar el paciente");
        }
    }

    /** @return {@code true} si existía el paciente */
    public boolean cambiarEstado(String dni, EstadoPaciente estado) {
        String sql = "UPDATE paciente SET estado = ? WHERE dni = ?";
        try (Connection c = conexiones.abrir(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, estado.name());
            ps.setString(2, dni);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new ErrorPersistencia("Error al cambiar el estado del paciente", e);
        }
    }

    /** Asigna los 8 campos que siguen al DNI a partir de la posición indicada. */
    private static void asignarDatos(PreparedStatement ps, Paciente p, int desde) throws SQLException {
        ps.setString(desde, p.tarjetaSanitaria());
        ps.setString(desde + 1, p.nombre());
        ps.setString(desde + 2, p.apellidos());
        if (p.fechaNacimiento().isEmpty()) {
            ps.setNull(desde + 3, Types.DATE);
        } else {
            ps.setDate(desde + 3, Date.valueOf(LocalDate.parse(p.fechaNacimiento())));
        }
        ps.setString(desde + 4, nuloSiVacio(p.sexo()));
        ps.setString(desde + 5, p.telefono());
        ps.setString(desde + 6, nuloSiVacio(p.correo()));
        ps.setString(desde + 7, nuloSiVacio(p.direccion()));
    }

    private static Paciente leer(ResultSet rs) throws SQLException {
        Date fecha = rs.getDate("fecha_nacimiento");
        return new Paciente(
                rs.getString("dni"),
                rs.getString("tarjeta_sanitaria"),
                rs.getString("nombre"),
                rs.getString("apellidos"),
                fecha == null ? "" : fecha.toLocalDate().toString(),
                rs.getString("sexo"),
                rs.getString("telefono"),
                rs.getString("correo"),
                rs.getString("direccion"),
                EstadoPaciente.valueOf(rs.getString("estado")));
    }

    private static String nuloSiVacio(String valor) {
        return valor.isEmpty() ? null : valor;
    }

    /**
     * Un error de integridad (SQLState 23xxx) es un duplicado: de tarjeta si el mensaje nombra la
     * restricción {@code uk_paciente_tarjeta} y de DNI (clave primaria) en otro caso. Se busca el
     * nombre de la restricción y no la palabra «tarjeta», porque algunos drivers incluyen en el
     * mensaje la sentencia SQL completa, que siempre nombra la columna.
     */
    private static RuntimeException traducir(SQLException e, String mensaje) {
        String estado = e.getSQLState();
        if (estado != null && estado.startsWith("23")) {
            String texto = String.valueOf(e.getMessage()).toLowerCase(Locale.ROOT);
            return new PacienteException(texto.contains(RESTRICCION_TARJETA)
                    ? CodigoError.TARJETA_DUPLICADA
                    : CodigoError.DNI_DUPLICADO);
        }
        return new ErrorPersistencia(mensaje, e);
    }
}
