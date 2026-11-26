package es.uclm.esi.iso2.comun.dominio;

import java.util.Locale;

/**
 * Datos de un paciente (CU12, apartado 8, y DEC-055).
 *
 * <p>Los campos se guardan como texto, igual que viajan por el protocolo: la fecha de
 * nacimiento en formato ISO ({@code aaaa-mm-dd}) y el sexo como {@code HOMBRE} o
 * {@code MUJER}. Un campo opcional vacío es la cadena vacía, nunca {@code null}.</p>
 */
public record Paciente(
        String dni,
        String tarjetaSanitaria,
        String nombre,
        String apellidos,
        String fechaNacimiento,
        String sexo,
        String telefono,
        String correo,
        String direccion,
        EstadoPaciente estado) {

    public static final String HOMBRE = "HOMBRE";
    public static final String MUJER = "MUJER";

    public Paciente {
        dni = vacioSiNulo(dni);
        tarjetaSanitaria = vacioSiNulo(tarjetaSanitaria);
        nombre = vacioSiNulo(nombre);
        apellidos = vacioSiNulo(apellidos);
        fechaNacimiento = vacioSiNulo(fechaNacimiento);
        sexo = vacioSiNulo(sexo);
        telefono = vacioSiNulo(telefono);
        correo = vacioSiNulo(correo);
        direccion = vacioSiNulo(direccion);
        if (estado == null) {
            estado = EstadoPaciente.ACTIVO;
        }
    }

    /**
     * Copia del paciente sin espacios al principio ni al final de cada campo y con el DNI/NIE,
     * la tarjeta sanitaria y el sexo en mayúsculas (DEC-055).
     */
    public Paciente normalizado() {
        return new Paciente(
                mayusculas(dni),
                mayusculas(tarjetaSanitaria),
                nombre.strip(),
                apellidos.strip(),
                fechaNacimiento.strip(),
                mayusculas(sexo),
                telefono.strip(),
                correo.strip(),
                direccion.strip(),
                estado);
    }

    /** Copia del paciente con otro estado. */
    public Paciente conEstado(EstadoPaciente nuevoEstado) {
        return new Paciente(dni, tarjetaSanitaria, nombre, apellidos, fechaNacimiento, sexo,
                telefono, correo, direccion, nuevoEstado);
    }

    public boolean activo() {
        return estado == EstadoPaciente.ACTIVO;
    }

    /** Valor del campo indicado. */
    public String valor(CampoPaciente campo) {
        return switch (campo) {
            case DNI -> dni;
            case TARJETA_SANITARIA -> tarjetaSanitaria;
            case NOMBRE -> nombre;
            case APELLIDOS -> apellidos;
            case FECHA_NACIMIENTO -> fechaNacimiento;
            case SEXO -> sexo;
            case TELEFONO -> telefono;
            case CORREO -> correo;
            case DIRECCION -> direccion;
        };
    }

    /** Nombre y apellidos, para mostrar al usuario. */
    public String nombreCompleto() {
        return (nombre + " " + apellidos).strip();
    }

    /** Normaliza un DNI/NIE como lo hace {@link #normalizado()}. */
    public static String normalizarDni(String dni) {
        return mayusculas(vacioSiNulo(dni));
    }

    private static String mayusculas(String texto) {
        return texto.strip().toUpperCase(Locale.ROOT);
    }

    private static String vacioSiNulo(String texto) {
        return texto == null ? "" : texto;
    }
}
