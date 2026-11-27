package es.uclm.esi.iso2.cliente.presentacion;

import es.uclm.esi.iso2.cliente.logica.Catalogo;
import es.uclm.esi.iso2.cliente.logica.EntradaCita;
import es.uclm.esi.iso2.cliente.logica.FormatosCitas;
import es.uclm.esi.iso2.cliente.logica.GestorCitasCliente;
import es.uclm.esi.iso2.cliente.logica.ResultadoCitas;
import es.uclm.esi.iso2.comun.dominio.Cita;
import es.uclm.esi.iso2.comun.dominio.Recurso;
import es.uclm.esi.iso2.comun.dominio.TipoPrueba;
import es.uclm.esi.iso2.comun.protocolo.ProtocoloCitas;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * Controlador del formulario de cita (frontera FormularioCita). Sirve para asignar una cita nueva
 * y para reprogramar una existente; al reprogramar, el paciente y el tipo de prueba no se pueden
 * cambiar (DEC-045). Si la operación falla, el formulario sigue abierto con lo escrito.
 */
public class FormularioCita {

    private final GestorCitasCliente gestor;
    private final Cita original;
    private ResultadoCitas<Cita> resultado;

    @FXML private Label titulo;
    @FXML private Label nota;
    @FXML private TextField dni;
    @FXML private ComboBox<TipoPrueba> tipo;
    @FXML private ComboBox<Recurso> recurso;
    @FXML private TextField fecha;
    @FXML private TextField hora;
    @FXML private TextField duracion;
    @FXML private Label mensaje;
    @FXML private Button botonGuardar;
    @FXML private Button botonCancelar;

    /** Control de cada campo, por el nombre que usa {@link GestorCitasCliente}. */
    private final Map<String, Control> controles = new LinkedHashMap<>();

    /**
     * @param original cita que se reprograma, o {@code null} para una cita nueva
     */
    public FormularioCita(GestorCitasCliente gestor, Cita original) {
        this.gestor = gestor;
        this.original = original;
    }

    /**
     * Abre el formulario como ventana modal y espera a que se cierre.
     *
     * @return el resultado si se ha guardado, o vacío si se ha cerrado sin guardar
     */
    public static Optional<ResultadoCitas<Cita>> abrir(Window propietaria, GestorCitasCliente gestor, Cita original) {
        FormularioCita controlador = new FormularioCita(gestor, original);
        FXMLLoader cargador = new FXMLLoader(FormularioCita.class.getResource("FormularioCita.fxml"));
        cargador.setControllerFactory(t -> controlador);
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
        ventana.setTitle(original == null ? "Nueva cita" : "Reprogramar cita");
        ventana.setScene(escena);
        ventana.showAndWait();
        return Optional.ofNullable(controlador.resultado);
    }

    @FXML
    private void initialize() {
        controles.put(ProtocoloCitas.CAMPO_DNI, dni);
        controles.put(ProtocoloCitas.CAMPO_TIPO_PRUEBA, tipo);
        controles.put(ProtocoloCitas.CAMPO_RECURSO, recurso);
        controles.put(GestorCitasCliente.CAMPO_FECHA, fecha);
        controles.put(GestorCitasCliente.CAMPO_HORA, hora);
        controles.put(ProtocoloCitas.CAMPO_DURACION, duracion);

        Catalogo catalogo = gestor.catalogo();
        tipo.getItems().setAll(catalogo.tipos());
        tipo.setConverter(conversor(t -> t.nombre() + " (" + t.duracionMinutos() + " min)"));
        recurso.getItems().setAll(catalogo.recursos());
        recurso.setConverter(conversor(Recurso::nombre));
        mensaje.setManaged(false);

        if (original == null) {
            titulo.setText("Nueva cita");
            nota.setText("Los campos marcados con * son obligatorios. Horario: de lunes a viernes, de 08:00 a 20:00.");
        } else {
            titulo.setText("Reprogramar la cita nº " + original.id());
            nota.setText("Puedes cambiar el recurso, la fecha, la hora y la duración. El paciente y el tipo de prueba"
                    + " no cambian; si son otros, cancela esta cita y asigna una nueva.");
            rellenar(original, catalogo);
            dni.setDisable(true);
            tipo.setDisable(true);
        }
        controles.values().forEach(c -> c.focusedProperty().addListener((obs, antes, ahora) -> {
            if (Boolean.TRUE.equals(ahora)) {
                c.getStyleClass().remove("campo-error");
            }
        }));
    }

    @FXML
    private void guardar() {
        EntradaCita entrada = new EntradaCita(dni.getText(), tipo.getValue(), recurso.getValue(), fecha.getText(),
                hora.getText(), duracion.getText());
        TareaCitas.ejecutar(
                () -> original == null ? gestor.asignar(entrada) : gestor.reprogramar(original, entrada),
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

    private void rellenar(Cita c, Catalogo catalogo) {
        dni.setText(c.dniPaciente());
        catalogo.tipo(c.idTipoPrueba()).ifPresent(tipo::setValue);
        catalogo.recurso(c.idRecurso()).ifPresent(recurso::setValue);
        fecha.setText(FormatosCitas.fecha(c.inicio().toLocalDate()));
        hora.setText(FormatosCitas.hora(c.inicio().toLocalTime()));
        duracion.setText(String.valueOf(c.duracionMinutos()));
    }

    private void marcarErrores(ResultadoCitas<Cita> r) {
        controles.values().forEach(c -> c.getStyleClass().remove("campo-error"));
        Control primero = null;
        for (String campo : r.camposConError()) {
            Control control = controles.get(campo);
            if (control != null) {
                control.getStyleClass().add("campo-error");
                if (primero == null && !control.isDisabled()) {
                    primero = control;
                }
            }
        }
        if (primero != null) {
            primero.requestFocus();
            // requestFocus quita la marca del campo; se vuelve a poner
            primero.getStyleClass().add("campo-error");
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

    /** Muestra en el desplegable el texto indicado en lugar del {@code toString} del objeto. */
    private static <T> StringConverter<T> conversor(Function<T, String> texto) {
        return new StringConverter<>() {
            @Override
            public String toString(T objeto) {
                return objeto == null ? "" : texto.apply(objeto);
            }

            @Override
            public T fromString(String cadena) {
                return null;
            }
        };
    }
}
