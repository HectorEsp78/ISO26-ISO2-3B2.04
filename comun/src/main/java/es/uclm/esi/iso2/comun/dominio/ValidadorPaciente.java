package es.uclm.esi.iso2.comun.dominio;

import es.uclm.esi.iso2.comun.protocolo.CodigoError;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Validaciones de los datos de un paciente (CU12, apartado 8, y DEC-055).
 *
 * <p>La usan el cliente y el servidor, de modo que los dos aplican exactamente las mismas
 * reglas. El servidor valida siempre, aunque el cliente ya lo haya hecho.</p>
 */
public final class ValidadorPaciente {

    public static final int MAX_NOMBRE = 50;
    public static final int MAX_APELLIDOS = 100;
    public static final int MAX_CORREO = 100;
    public static final int MAX_DIRECCION = 200;
    public static final LocalDate FECHA_MINIMA = LocalDate.of(1900, 1, 1);

    private static final String LETRAS_CONTROL = "TRWAGMYFPDXBNJZSQVHLCKE";
    private static final Pattern DNI = Pattern.compile("\\d{8}[A-Z]");
    private static final Pattern NIE = Pattern.compile("[XYZ]\\d{7}[A-Z]");
    private static final Pattern TARJETA = Pattern.compile("[A-Z0-9]{16}");
    private static final Pattern NOMBRE = Pattern.compile("(?=.*\\p{L})[\\p{L} '\\-]+");
    private static final Pattern TELEFONO = Pattern.compile("[6789]\\d{8}");
    private static final Pattern CORREO =
            Pattern.compile("[A-Za-z0-9._%+\\-]+@[A-Za-z0-9\\-]+(\\.[A-Za-z0-9\\-]+)*\\.[A-Za-z]{2,}");

    private final Clock reloj;

    public ValidadorPaciente() {
        this(Clock.systemDefaultZone());
    }

    /** Permite fijar el día de hoy en las pruebas. */
    public ValidadorPaciente(Clock reloj) {
        this.reloj = reloj;
    }

    /**
     * Valida todos los campos del paciente, una vez normalizado, y devuelve sus errores en el
     * orden de {@link CampoPaciente}. La lista está vacía si el paciente es válido.
     */
    public List<ErrorValidacion> validar(Paciente paciente) {
        Paciente p = paciente.normalizado();
        List<ErrorValidacion> errores = new ArrayList<>();
        validarDni(p.dni()).ifPresent(errores::add);
        obligatorio(errores, CampoPaciente.TARJETA_SANITARIA, p.tarjetaSanitaria(),
                TARJETA.matcher(p.tarjetaSanitaria()).matches());
        obligatorio(errores, CampoPaciente.NOMBRE, p.nombre(), textoPersona(p.nombre(), MAX_NOMBRE));
        obligatorio(errores, CampoPaciente.APELLIDOS, p.apellidos(),
                textoPersona(p.apellidos(), MAX_APELLIDOS));
        opcional(errores, CampoPaciente.FECHA_NACIMIENTO, p.fechaNacimiento(),
                fechaValida(p.fechaNacimiento()));
        opcional(errores, CampoPaciente.SEXO, p.sexo(),
                Paciente.HOMBRE.equals(p.sexo()) || Paciente.MUJER.equals(p.sexo()));
        obligatorio(errores, CampoPaciente.TELEFONO, p.telefono(), TELEFONO.matcher(p.telefono()).matches());
        opcional(errores, CampoPaciente.CORREO, p.correo(),
                p.correo().length() <= MAX_CORREO && CORREO.matcher(p.correo()).matches());
        opcional(errores, CampoPaciente.DIRECCION, p.direccion(), p.direccion().length() <= MAX_DIRECCION);
        return errores;
    }

    /** Primer error del paciente, que es el que se devuelve por el protocolo. */
    public Optional<ErrorValidacion> primerError(Paciente paciente) {
        return validar(paciente).stream().findFirst();
    }

    /** Valida solo el DNI/NIE (por ejemplo, antes de buscar o de dar de baja). */
    public Optional<ErrorValidacion> validarDni(String dni) {
        String normalizado = Paciente.normalizarDni(dni);
        if (normalizado.isEmpty()) {
            return Optional.of(new ErrorValidacion(CampoPaciente.DNI, CodigoError.CAMPO_OBLIGATORIO));
        }
        if (!dniValido(normalizado)) {
            return Optional.of(new ErrorValidacion(CampoPaciente.DNI, CodigoError.CAMPO_NO_VALIDO));
        }
        return Optional.empty();
    }

    /**
     * Comprueba el formato y la letra de control de un DNI o NIE ya normalizado
     * (sin espacios y en mayúsculas).
     */
    public static boolean dniValido(String dni) {
        String numero;
        if (DNI.matcher(dni).matches()) {
            numero = dni.substring(0, 8);
        } else if (NIE.matcher(dni).matches()) {
            numero = "XYZ".indexOf(dni.charAt(0)) + dni.substring(1, 8);
        } else {
            return false;
        }
        char esperada = LETRAS_CONTROL.charAt(Integer.parseInt(numero) % 23);
        return dni.charAt(dni.length() - 1) == esperada;
    }

    private boolean fechaValida(String texto) {
        try {
            LocalDate fecha = LocalDate.parse(texto);
            return !fecha.isBefore(FECHA_MINIMA) && !fecha.isAfter(LocalDate.now(reloj));
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static boolean textoPersona(String texto, int maximo) {
        return texto.length() <= maximo && NOMBRE.matcher(texto).matches();
    }

    private static void obligatorio(List<ErrorValidacion> errores, CampoPaciente campo, String valor,
            boolean valido) {
        if (valor.isEmpty()) {
            errores.add(new ErrorValidacion(campo, CodigoError.CAMPO_OBLIGATORIO));
        } else if (!valido) {
            errores.add(new ErrorValidacion(campo, CodigoError.CAMPO_NO_VALIDO));
        }
    }

    private static void opcional(List<ErrorValidacion> errores, CampoPaciente campo, String valor,
            boolean valido) {
        if (!valor.isEmpty() && !valido) {
            errores.add(new ErrorValidacion(campo, CodigoError.CAMPO_NO_VALIDO));
        }
    }
}
