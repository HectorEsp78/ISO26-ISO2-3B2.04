package es.uclm.esi.iso2.cliente.presentacion;

import es.uclm.esi.iso2.cliente.logica.Formatos;
import es.uclm.esi.iso2.cliente.logica.GestorPacientesCliente;
import es.uclm.esi.iso2.cliente.logica.Resultado;
import es.uclm.esi.iso2.comun.dominio.Paciente;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * Controlador de la pantalla de pacientes (frontera PantallaBusquedaPacientes). Solo muestra lo
 * que devuelve {@link GestorPacientesCliente}; no contiene lógica (DEC-059 y DEC-060).
 */
public class PantallaBusquedaPacientes {

    private final GestorPacientesCliente gestor;
    private Paciente pacienteActual;

    @FXML private TextField campoDni;
    @FXML private Button botonBuscar;
    @FXML private Button botonNuevo;
    @FXML private Label marcador;
    @FXML private VBox ficha;
    @FXML private Label nombreCompleto;
    @FXML private Label estado;
    @FXML private Label valorDni;
    @FXML private Label valorTarjeta;
    @FXML private Label valorFecha;
    @FXML private Label valorSexo;
    @FXML private Label valorTelefono;
    @FXML private Label valorCorreo;
    @FXML private Label valorDireccion;
    @FXML private Button botonModificar;
    @FXML private Button botonBaja;
    @FXML private Label mensaje;

    public PantallaBusquedaPacientes(GestorPacientesCliente gestor) {
        this.gestor = gestor;
    }

    @FXML
    private void buscar() {
        String dni = campoDni.getText();
        TareaSegundoPlano.ejecutar(() -> gestor.buscar(dni), this::ocupado, resultado -> {
            if (resultado.exito()) {
                mostrarPaciente(resultado.paciente());
                limpiarMensaje();
            } else {
                mostrarPaciente(null);
                mostrarMensaje(resultado);
            }
        });
    }

    @FXML
    private void nuevoPaciente() {
        FormularioPaciente.abrir(ventana(), gestor, null).ifPresent(this::tratarGuardado);
    }

    @FXML
    private void modificar() {
        FormularioPaciente.abrir(ventana(), gestor, pacienteActual).ifPresent(this::tratarGuardado);
    }

    @FXML
    private void darDeBaja() {
        Paciente paciente = pacienteActual;
        ButtonType confirmar = new ButtonType("Dar de baja", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert pregunta = new Alert(Alert.AlertType.CONFIRMATION, gestor.preguntaConfirmacionBaja(paciente),
                confirmar, cancelar);
        pregunta.initOwner(ventana());
        pregunta.setTitle("Confirmar baja");
        pregunta.setHeaderText("Dar de baja al paciente");
        if (pregunta.showAndWait().orElse(cancelar) != confirmar) {
            mostrarInfo("No se ha dado de baja al paciente.");
            return;
        }
        TareaSegundoPlano.ejecutar(() -> gestor.baja(paciente), this::ocupado, resultado -> {
            if (resultado.exito()) {
                mostrarPaciente(resultado.paciente());
            }
            mostrarMensaje(resultado);
        });
    }

    private void tratarGuardado(Resultado resultado) {
        mostrarPaciente(resultado.paciente());
        campoDni.setText(resultado.paciente().dni());
        mostrarMensaje(resultado);
    }

    private void mostrarPaciente(Paciente paciente) {
        pacienteActual = paciente;
        boolean hay = paciente != null;
        ficha.setVisible(hay);
        ficha.setManaged(hay);
        marcador.setVisible(!hay);
        marcador.setManaged(!hay);
        if (!hay) {
            return;
        }
        nombreCompleto.setText(paciente.nombreCompleto());
        estado.setText(paciente.activo() ? "Activo" : "De baja");
        estado.getStyleClass().removeAll("estado-activo", "estado-baja");
        estado.getStyleClass().add(paciente.activo() ? "estado-activo" : "estado-baja");
        valorDni.setText(paciente.dni());
        valorTarjeta.setText(paciente.tarjetaSanitaria());
        valorFecha.setText(Formatos.oGuion(Formatos.fechaAPantalla(paciente.fechaNacimiento())));
        valorSexo.setText(paciente.sexo().isEmpty() ? "—" : Formatos.sexoAPantalla(paciente.sexo()));
        valorTelefono.setText(paciente.telefono());
        valorCorreo.setText(Formatos.oGuion(paciente.correo()));
        valorDireccion.setText(Formatos.oGuion(paciente.direccion()));
        boolean editable = gestor.puedeModificar(paciente);
        botonModificar.setDisable(!editable);
        botonBaja.setDisable(!editable);
    }

    private void ocupado(boolean ocupado) {
        campoDni.setDisable(ocupado);
        botonBuscar.setDisable(ocupado);
        botonNuevo.setDisable(ocupado);
        boolean editable = !ocupado && gestor.puedeModificar(pacienteActual);
        botonModificar.setDisable(!editable);
        botonBaja.setDisable(!editable);
        if (ocupado) {
            mostrarInfo("Conectando con el servidor…");
        }
    }

    private void mostrarMensaje(Resultado resultado) {
        mensaje.setText(resultado.mensaje() == null ? "" : resultado.mensaje());
        estilo(resultado.exito() ? "mensaje-ok" : "mensaje-error");
    }

    private void mostrarInfo(String texto) {
        mensaje.setText(texto);
        estilo("mensaje-info");
    }

    private void limpiarMensaje() {
        mensaje.setText("");
        estilo("mensaje-info");
    }

    private void estilo(String clase) {
        mensaje.getStyleClass().removeAll("mensaje-ok", "mensaje-error", "mensaje-info");
        mensaje.getStyleClass().add(clase);
    }

    private javafx.stage.Window ventana() {
        return campoDni.getScene().getWindow();
    }
}
