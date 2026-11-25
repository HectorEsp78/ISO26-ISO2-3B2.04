package es.uclm.esi.iso2.cliente.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uclm.esi.iso2.comun.dominio.CampoPaciente;
import es.uclm.esi.iso2.comun.dominio.EstadoPaciente;
import es.uclm.esi.iso2.comun.dominio.Paciente;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ServicioPacientesEnMemoriaTest {

    static final Paciente LUCIA = new Paciente("12345678Z", "CMCM000000000001", "Lucía", "García", "1985-04-12",
            Paciente.MUJER, "600111222", "", "", null);
    static final Paciente AHMED = new Paciente("X1234567L", "CMCM000000000002", "Ahmed", "El Idrissi", "",
            "", "699333444", "", "", null);

    private ServicioPacientesEnMemoria servicio;

    @BeforeEach
    void preparar() {
        servicio = new ServicioPacientesEnMemoria();
    }

    private static void assertError(CodigoError codigo, CampoPaciente campo, Executable operacion) {
        ExcepcionServicio e = assertThrows(ExcepcionServicio.class, operacion);
        assertEquals(codigo, e.codigo());
        assertEquals(campo, e.campo());
    }

    // ---- Alta (CA-01 a CA-04) ----

    @Test
    void altaDeUnPacienteValidoYBusquedaPosterior() {
        Paciente guardado = servicio.alta(LUCIA);
        assertEquals(LUCIA, guardado);
        assertEquals(LUCIA, servicio.buscar("12345678Z"));
    }

    @Test
    void altaNormalizaYSiempreCreaElPacienteActivo() {
        Paciente guardado = servicio.alta(new Paciente(" 12345678z ", "cmcm000000000001", "Lucía", "García", "",
                "", "600111222", "", "", EstadoPaciente.BAJA));
        assertEquals("12345678Z", guardado.dni());
        assertEquals("CMCM000000000001", guardado.tarjetaSanitaria());
        assertTrue(guardado.activo());
    }

    @Test
    void altaConDatoObligatorioVacio() {
        Paciente sinTelefono = new Paciente(LUCIA.dni(), LUCIA.tarjetaSanitaria(), LUCIA.nombre(),
                LUCIA.apellidos(), "", "", "", "", "", null);
        assertError(CodigoError.CAMPO_OBLIGATORIO, CampoPaciente.TELEFONO, () -> servicio.alta(sinTelefono));
        assertTrue(servicio.todos().isEmpty());
    }

    @Test
    void altaConDniNoValido() {
        Paciente letraMal = new Paciente("12345678A", LUCIA.tarjetaSanitaria(), "Lucía", "García", "", "",
                "600111222", "", "", null);
        assertError(CodigoError.CAMPO_NO_VALIDO, CampoPaciente.DNI, () -> servicio.alta(letraMal));
    }

    @Test
    void altaConDniDuplicado() {
        servicio.alta(LUCIA);
        Paciente mismoDni = new Paciente(LUCIA.dni(), "CMCM000000000009", "Otra", "Persona", "", "",
                "600000000", "", "", null);
        assertError(CodigoError.DNI_DUPLICADO, null, () -> servicio.alta(mismoDni));
    }

    @Test
    void altaConTarjetaDuplicada() {
        servicio.alta(LUCIA);
        Paciente mismaTarjeta = new Paciente(AHMED.dni(), LUCIA.tarjetaSanitaria(), "Ahmed", "El Idrissi", "",
                "", "699333444", "", "", null);
        assertError(CodigoError.TARJETA_DUPLICADA, null, () -> servicio.alta(mismaTarjeta));
    }

    // ---- Búsqueda (CA-05) ----

    @Test
    void busquedaNormalizaElDni() {
        servicio.alta(AHMED);
        assertEquals(AHMED, servicio.buscar(" x1234567l "));
    }

    @Test
    void busquedaDeUnDniNoRegistrado() {
        assertError(CodigoError.PACIENTE_NO_ENCONTRADO, null, () -> servicio.buscar("12345678Z"));
    }

    @Test
    void busquedaConDniVacioONoValido() {
        assertError(CodigoError.CAMPO_OBLIGATORIO, CampoPaciente.DNI, () -> servicio.buscar(""));
        assertError(CodigoError.CAMPO_NO_VALIDO, CampoPaciente.DNI, () -> servicio.buscar("1234"));
    }

    // ---- Modificación (CA-06 y CA-07) ----

    @Test
    void modificacionGuardaLosCambios() {
        servicio.alta(LUCIA);
        Paciente cambiado = new Paciente(LUCIA.dni(), LUCIA.tarjetaSanitaria(), "Lucía", "García Pérez",
                "1985-04-12", Paciente.MUJER, "699999999", "lucia@example.com", "Nueva dirección",
                EstadoPaciente.BAJA);
        Paciente guardado = servicio.modificar(cambiado);
        assertEquals(cambiado.conEstado(EstadoPaciente.ACTIVO), guardado);
        assertEquals(guardado, servicio.buscar(LUCIA.dni()));
    }

    @Test
    void modificacionAplicaLasValidacionesDelAlta() {
        servicio.alta(LUCIA);
        Paciente correoMal = new Paciente(LUCIA.dni(), LUCIA.tarjetaSanitaria(), "Lucía", "García", "", "",
                "600111222", "no-es-un-correo", "", null);
        assertError(CodigoError.CAMPO_NO_VALIDO, CampoPaciente.CORREO, () -> servicio.modificar(correoMal));
        assertEquals(LUCIA, servicio.buscar(LUCIA.dni()));
    }

    @Test
    void modificacionConLaTarjetaDeOtroPaciente() {
        servicio.alta(LUCIA);
        servicio.alta(AHMED);
        Paciente robaTarjeta = new Paciente(AHMED.dni(), LUCIA.tarjetaSanitaria(), "Ahmed", "El Idrissi", "",
                "", "699333444", "", "", null);
        assertError(CodigoError.TARJETA_DUPLICADA, null, () -> servicio.modificar(robaTarjeta));
    }

    @Test
    void modificacionDeUnPacienteInexistente() {
        assertError(CodigoError.PACIENTE_NO_ENCONTRADO, null, () -> servicio.modificar(LUCIA));
    }

    @Test
    void unPacienteDeBajaNoSePuedeModificar() {
        servicio.alta(LUCIA);
        servicio.baja(LUCIA.dni());
        assertError(CodigoError.PACIENTE_DE_BAJA, null, () -> servicio.modificar(LUCIA));
    }

    // ---- Baja (CA-07) ----

    @Test
    void bajaLogica() {
        servicio.alta(LUCIA);
        Paciente deBaja = servicio.baja(LUCIA.dni());
        assertFalse(deBaja.activo());
        assertEquals(EstadoPaciente.BAJA, servicio.buscar(LUCIA.dni()).estado());
        assertEquals(1, servicio.todos().size());
    }

    @Test
    void bajaDeUnPacienteYaDadoDeBajaOInexistente() {
        servicio.alta(LUCIA);
        servicio.baja(LUCIA.dni());
        assertError(CodigoError.PACIENTE_DE_BAJA, null, () -> servicio.baja(LUCIA.dni()));
        assertError(CodigoError.PACIENTE_NO_ENCONTRADO, null, () -> servicio.baja(AHMED.dni()));
    }

    // ---- Comprobación para otros casos de uso (DEC-061) ----

    @Test
    void comprobarDevuelveElEstado() {
        servicio.alta(LUCIA);
        servicio.alta(AHMED);
        servicio.baja(AHMED.dni());
        assertEquals(EstadoPaciente.ACTIVO, servicio.comprobar(" 12345678z "));
        assertEquals(EstadoPaciente.BAJA, servicio.comprobar(AHMED.dni()));
        assertError(CodigoError.PACIENTE_NO_ENCONTRADO, null, () -> servicio.comprobar("87654321X"));
        assertError(CodigoError.CAMPO_NO_VALIDO, CampoPaciente.DNI, () -> servicio.comprobar("1234"));
    }

    @Test
    void estaActivo() {
        servicio.alta(LUCIA);
        servicio.alta(AHMED);
        servicio.baja(AHMED.dni());
        assertTrue(servicio.estaActivo(LUCIA.dni()));
        assertFalse(servicio.estaActivo(AHMED.dni()));
        assertFalse(servicio.estaActivo("87654321X"));
        // Los demás errores no se ocultan: un DNI no válido no es "no activo"
        assertError(CodigoError.CAMPO_OBLIGATORIO, CampoPaciente.DNI, () -> servicio.estaActivo(""));
    }

    // ---- Datos de ejemplo ----

    @Test
    void datosDeEjemplo() {
        ServicioPacientesEnMemoria ejemplo = ServicioPacientesEnMemoria.conDatosDeEjemplo();
        assertEquals(3, ejemplo.todos().size());
        assertTrue(ejemplo.buscar("12345678Z").activo());
        assertFalse(ejemplo.buscar("87654321X").activo());
    }

    @Test
    void mensajeDeLaExcepcion() {
        assertEquals("CAMPO_OBLIGATORIO (telefono)",
                new ExcepcionServicio(CodigoError.CAMPO_OBLIGATORIO, CampoPaciente.TELEFONO).getMessage());
        ExcepcionServicio sinCampo = new ExcepcionServicio(CodigoError.ERROR_INTERNO);
        assertEquals("ERROR_INTERNO", sinCampo.getMessage());
        assertNull(sinCampo.campo());
    }
}
