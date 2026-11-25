package es.uclm.esi.iso2.cliente.logica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import es.uclm.esi.iso2.cliente.servicio.ExcepcionServicio;
import es.uclm.esi.iso2.cliente.servicio.ServicioPacientes;
import es.uclm.esi.iso2.cliente.servicio.ServicioPacientesEnMemoria;
import es.uclm.esi.iso2.comun.dominio.CampoPaciente;
import es.uclm.esi.iso2.comun.dominio.EstadoPaciente;
import es.uclm.esi.iso2.comun.dominio.Paciente;
import es.uclm.esi.iso2.comun.protocolo.CodigoError;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GestorPacientesClienteTest {

    private static final Paciente LUCIA = new Paciente("12345678Z", "CMCM000000000001", "Lucía", "García",
            "1985-04-12", Paciente.MUJER, "600111222", "", "", null);

    private ServicioPacientesEnMemoria servicio;
    private GestorPacientesCliente gestor;

    @BeforeEach
    void preparar() {
        servicio = new ServicioPacientesEnMemoria();
        gestor = new GestorPacientesCliente(servicio);
    }

    /** Servicio que siempre falla con el código indicado. */
    private static GestorPacientesCliente gestorQueFalla(CodigoError codigo) {
        ServicioPacientes falla = new ServicioPacientes() {
            @Override
            public Paciente alta(Paciente paciente) {
                throw new ExcepcionServicio(codigo);
            }

            @Override
            public Paciente buscar(String dni) {
                throw new ExcepcionServicio(codigo);
            }

            @Override
            public Paciente modificar(Paciente paciente) {
                throw new ExcepcionServicio(codigo);
            }

            @Override
            public Paciente baja(String dni) {
                throw new ExcepcionServicio(codigo);
            }
        };
        return new GestorPacientesCliente(falla);
    }

    // ---- Búsqueda ----

    @Test
    void buscarUnPacienteExistente() {
        servicio.alta(LUCIA);
        Resultado r = gestor.buscar(" 12345678z ");
        assertTrue(r.exito());
        assertEquals(LUCIA, r.paciente());
    }

    @Test
    void buscarConDniNoValidoNoLlamaAlServicio() {
        Resultado r = gestorQueFalla(CodigoError.ERROR_INTERNO).buscar("123");
        assertFalse(r.exito());
        assertEquals(List.of(CampoPaciente.DNI), r.camposConError());
        assertTrue(r.mensaje().contains("DNI/NIE"));
    }

    @Test
    void buscarUnDniNoRegistrado() {
        Resultado r = gestor.buscar("12345678Z");
        assertFalse(r.exito());
        assertNull(r.paciente());
        assertEquals(MensajesError.mensaje(CodigoError.PACIENTE_NO_ENCONTRADO, null), r.mensaje());
        assertEquals(List.of(), r.camposConError());
    }

    // ---- Alta ----

    @Test
    void altaCorrecta() {
        Resultado r = gestor.alta(LUCIA);
        assertTrue(r.exito());
        assertEquals("Paciente Lucía García dado de alta correctamente.", r.mensaje());
        assertEquals(1, servicio.todos().size());
    }

    @Test
    void altaConUnSoloErrorIndicaElCampo() {
        Paciente sinNombre = new Paciente(LUCIA.dni(), LUCIA.tarjetaSanitaria(), "", LUCIA.apellidos(), "", "",
                LUCIA.telefono(), "", "", null);
        Resultado r = gestor.alta(sinNombre);
        assertFalse(r.exito());
        assertEquals(List.of(CampoPaciente.NOMBRE), r.camposConError());
        assertEquals("Falta el campo obligatorio «Nombre».", r.mensaje());
        assertTrue(servicio.todos().isEmpty());
    }

    @Test
    void altaConVariosErroresLosMarcaTodos() {
        Resultado r = gestor.alta(new Paciente("", "", "Ana", "Ruiz", "", "", "", "", "", null));
        assertEquals(List.of(CampoPaciente.DNI, CampoPaciente.TARJETA_SANITARIA, CampoPaciente.TELEFONO),
                r.camposConError());
        assertTrue(r.mensaje().endsWith("Revisa también los demás campos marcados."));
    }

    @Test
    void altaConDniDuplicadoMarcaElDni() {
        servicio.alta(LUCIA);
        Resultado r = gestor.alta(LUCIA);
        assertFalse(r.exito());
        assertEquals(List.of(CampoPaciente.DNI), r.camposConError());
        assertEquals(MensajesError.mensaje(CodigoError.DNI_DUPLICADO, null), r.mensaje());
    }

    @Test
    void altaSinConexionConservaLosDatos() {
        Resultado r = gestorQueFalla(CodigoError.SIN_CONEXION).alta(LUCIA);
        assertFalse(r.exito());
        assertTrue(r.sinConexion());
        assertEquals(List.of(), r.camposConError());
    }

    @Test
    void errorDelServidorSinCampo() {
        Resultado r = gestorQueFalla(CodigoError.ERROR_INTERNO).alta(LUCIA);
        assertFalse(r.sinConexion());
        assertEquals(List.of(), r.camposConError());
        assertEquals(MensajesError.mensaje(CodigoError.ERROR_INTERNO, null), r.mensaje());
    }

    // ---- Modificación ----

    @Test
    void modificacionCorrecta() {
        servicio.alta(LUCIA);
        Paciente cambiado = new Paciente(LUCIA.dni(), LUCIA.tarjetaSanitaria(), "Lucía", "García Pérez", "", "",
                "699999999", "", "", null);
        Resultado r = gestor.modificar(cambiado);
        assertTrue(r.exito());
        assertEquals("Datos de Lucía García Pérez guardados correctamente.", r.mensaje());
        assertEquals("699999999", servicio.buscar(LUCIA.dni()).telefono());
    }

    @Test
    void modificacionConDatosNoValidos() {
        servicio.alta(LUCIA);
        Paciente telefonoMal = new Paciente(LUCIA.dni(), LUCIA.tarjetaSanitaria(), "Lucía", "García", "", "",
                "123", "", "", null);
        Resultado r = gestor.modificar(telefonoMal);
        assertFalse(r.exito());
        assertEquals(List.of(CampoPaciente.TELEFONO), r.camposConError());
    }

    @Test
    void unPacienteDeBajaNoSePuedeModificar() {
        Paciente deBaja = LUCIA.conEstado(EstadoPaciente.BAJA);
        assertFalse(gestor.puedeModificar(deBaja));
        assertFalse(gestor.puedeModificar(null));
        assertTrue(gestor.puedeModificar(LUCIA));
        Resultado r = gestorQueFalla(CodigoError.ERROR_INTERNO).modificar(deBaja);
        assertFalse(r.exito());
        assertEquals(MensajesError.mensaje(CodigoError.PACIENTE_DE_BAJA, null), r.mensaje());
    }

    // ---- Baja ----

    @Test
    void bajaCorrecta() {
        servicio.alta(LUCIA);
        Resultado r = gestor.baja(LUCIA);
        assertTrue(r.exito());
        assertFalse(r.paciente().activo());
        assertEquals("Paciente Lucía García dado de baja.", r.mensaje());
    }

    @Test
    void bajaDeUnPacienteYaDeBajaNoLlamaAlServicio() {
        Resultado r = gestorQueFalla(CodigoError.ERROR_INTERNO).baja(LUCIA.conEstado(EstadoPaciente.BAJA));
        assertFalse(r.exito());
        assertEquals(MensajesError.mensaje(CodigoError.PACIENTE_DE_BAJA, null), r.mensaje());
    }

    @Test
    void preguntaDeConfirmacion() {
        String pregunta = gestor.preguntaConfirmacionBaja(LUCIA);
        assertTrue(pregunta.contains("Lucía García"));
        assertTrue(pregunta.contains("12345678Z"));
    }
}
