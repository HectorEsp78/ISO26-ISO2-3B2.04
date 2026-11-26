package es.uclm.esi.iso2.cliente.presentacion;

import es.uclm.esi.iso2.cliente.logica.Formatos;
import es.uclm.esi.iso2.cliente.logica.GestorPacientesCliente;
import es.uclm.esi.iso2.cliente.logica.Resultado;
import es.uclm.esi.iso2.comun.dominio.CampoPaciente;
import es.uclm.esi.iso2.comun.dominio.Paciente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Control;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador del formulario de alta y modificación (frontera FormularioPaciente). Si la
 * operación falla, el formulario sigue abierto con lo introducido (FA-01 a FA-05 y FA-08).
 */
public class FormularioPaciente {

    private final GestorPacientesCliente gestor;
    private final Paciente original;
    private Resultado resultado;

    @FXML private Label titulo;
    @FXML private TextField dni;
    @FXML private TextField tarjeta;
    @FXML private TextField nombre;
    @FXML private TextField apellidos;
    @FXML private TextField fechaNacimiento;
    @FXML private ComboBox<String> sexo;
    @FXML private TextField telefono;
    @FXML private TextField correo;
    @FXML private TextArea direccion;
    @FXML private Label mensaje;
    @FXML private Button botonGuardar;
    @FXML private Button botonCancelar;

    private final Map<CampoPaciente, Control> controles = new EnumMap<>(CampoPaciente.class);

    /**
     * @param original paciente que se modifica, o {@code null} para un alta
     */
    public FormularioPaciente(GestorPacientesCliente gestor, Paciente original) {
        this.gestor = gestor;
        this.original = original;
    }

    /**
     * Abre el formulario como ventana modal y espera a que se cierre.
     *
     * @return el resultado si se ha guardado, o vacío si se ha cancelado
     */
    public static Optional<Resultado> abrir(Window propietaria, GestorPacientesCliente gestor, Paciente original) {
        FormularioPaciente controlador = new FormularioPaciente(gestor, original);
        FXMLLoader cargador = new FXMLLoader(FormularioPaciente.class.getResource("FormularioPaciente.fxml"));
        cargador.setControllerFactory(tipo -> controlador);
        Scene escena;
        try {
            escena = new Scene(cargador.load());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        escena.getStylesheets().addAll(propietaria.getScene().getStylesheets());
        Stage ventana = new Stage();
        ventana.initOwner(propietaria);
        ventana.initModality(Modality.WINDOW_MODAL);
        ventana.setTitle(original == null ? "Nuevo paciente" : "Modificar paciente");
        ventana.setScene(escena);
        ventana.showAndWait();
        return Optional.ofNullable(controlador.resultado);
    }

    @FXML
    private void initialize() {
        controles.put(CampoPaciente.DNI, dni);
        controles.put(CampoPaciente.TARJETA_SANITARIA, tarjeta);
        controles.put(CampoPaciente.NOMBRE, nombre);
        controles.put(CampoPaciente.APELLIDOS, apellidos);
        controles.put(CampoPaciente.FECHA_NACIMIENTO, fechaNacimiento);
        controles.put(CampoPaciente.SEXO, sexo);
        controles.put(CampoPaciente.TELEFONO, telefono);
        controles.put(CampoPaciente.CORREO, correo);
        controles.put(CampoPaciente.DIRECCION, direccion);

        sexo.getItems().setAll(Formatos.OPCIONES_SEXO);
        sexo.setValue(Formatos.SEXO_SIN_INDICAR);
        mensaje.setManaged(false);

        if (original == null) {
            titulo.setText("Nuevo paciente");
        } else {
            titulo.setText("Modificar paciente");
            rellenar(original);
            // RN-03: una vez registrado, el DNI no se puede modificar
            dni.setDisable(true);
        }
        controles.values().forEach(c -> c.focusedProperty().addListener((obs, antes, ahora) -> {
            if (Boolean.TRUE.equals(ahora)) {
                c.getStyleClass().remove("campo-error");
            }
        }));
    }

    @FXML
    private void guardar() {
        Paciente datos = leer();
        TareaSegundoPlano.ejecutar(
                () -> original == null ? gestor.alta(datos) : gestor.modificar(datos),
                this::ocupado,
                r -> {
                    if (r.exito()) {
                        resultado = r;
                        cerrar();
                    } else {
                        marcarErrores(r);
                    }
                });
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    private Paciente leer() {
        return new Paciente(
                original == null ? dni.getText() : original.dni(),
                tarjeta.getText(),
                nombre.getText(),
                apellidos.getText(),
                Formatos.fechaAIso(fechaNacimiento.getText()),
                Formatos.sexoAProtocolo(sexo.getValue()),
                telefono.getText(),
                correo.getText(),
                direccion.getText(),
                original == null ? null : original.estado());
    }

    private void rellenar(Paciente p) {
        dni.setText(p.dni());
        tarjeta.setText(p.tarjetaSanitaria());
        nombre.setText(p.nombre());
        apellidos.setText(p.apellidos());
        fechaNacimiento.setText(Formatos.fechaAPantalla(p.fechaNacimiento()));
        sexo.setValue(Formatos.sexoAPantalla(p.sexo()));
        telefono.setText(p.telefono());
        correo.setText(p.correo());
        direccion.setText(p.direccion());
    }

    private void marcarErrores(Resultado r) {
        controles.values().forEach(c -> c.getStyleClass().remove("campo-error"));
        for (CampoPaciente campo : r.camposConError()) {
            Control control = controles.get(campo);
            control.getStyleClass().add("campo-error");
        }
        if (!r.camposConError().isEmpty()) {
            controles.get(r.camposConError().get(0)).requestFocus();
            // requestFocus quita la marca del primer campo; se vuelve a poner
            controles.get(r.camposConError().get(0)).getStyleClass().add("campo-error");
        }
        String texto = r.mensaje();
        if (r.sinConexion()) {
            texto += " Los datos introducidos se conservan.";
        }
        mensaje.setText(texto);
        mensaje.setManaged(true);
        mensaje.getScene().getWindow().sizeToScene();
    }

    private void ocupado(boolean ocupado) {
        botonGuardar.setDisable(ocupado);
        botonCancelar.setDisable(ocupado);
        botonGuardar.setText(ocupado ? "Guardando…" : "Guardar");
    }

    private void cerrar() {
        ((Stage) botonCancelar.getScene().getWindow()).close();
    }
}
