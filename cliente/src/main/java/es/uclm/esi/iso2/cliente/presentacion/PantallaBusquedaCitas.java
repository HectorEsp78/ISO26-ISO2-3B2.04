package es.uclm.esi.iso2.cliente.presentacion;

import es.uclm.esi.iso2.cliente.logica.Catalogo;
import es.uclm.esi.iso2.cliente.logica.FormatosCitas;
import es.uclm.esi.iso2.cliente.logica.GestorCitasCliente;
import es.uclm.esi.iso2.cliente.logica.ResultadoCitas;
import es.uclm.esi.iso2.comun.dominio.Cita;
import es.uclm.esi.iso2.comun.dominio.EstadoCita;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * Controlador de la pantalla de citas (frontera PantallaBusquedaCitas). Solo muestra lo que
 * devuelve {@link GestorCitasCliente}; no contiene lógica (DEC-059 y DEC-060).
 */
public class PantallaBusquedaCitas {

    private static final String TODOS = "Todos";

    private final GestorCitasCliente gestor;

    @FXML private TextField filtroDni;
    @FXML private TextField filtroDesde;
    @FXML private TextField filtroHasta;
    @FXML private ComboBox<String> filtroEstado;
    @FXML private Button botonBuscar;
    @FXML private Button botonNueva;
    @FXML private TableView<Cita> tabla;
    @FXML private TableColumn<Cita, String> columnaId;
    @FXML private TableColumn<Cita, String> columnaDni;
    @FXML private TableColumn<Cita, String> columnaTipo;
    @FXML private TableColumn<Cita, String> columnaRecurso;
    @FXML private TableColumn<Cita, String> columnaInicio;
    @FXML private TableColumn<Cita, String> columnaFin;
    @FXML private TableColumn<Cita, String> columnaEstado;
    @FXML private TableColumn<Cita, String> columnaMotivo;
    @FXML private Button botonReprogramar;
    @FXML private Button botonCancelarCita;
    @FXML private Label mensaje;

    public PantallaBusquedaCitas(GestorCitasCliente gestor) {
        this.gestor = gestor;
    }

    @FXML
    private void initialize() {
        filtroEstado.getItems().add(TODOS);
        for (EstadoCita estado : EstadoCita.values()) {
            filtroEstado.getItems().add(FormatosCitas.estado(estado));
        }
        filtroEstado.getSelectionModel().select(0);

        Catalogo catalogo = gestor.catalogo();
        columna(columnaId, c -> String.valueOf(c.id()));
        columna(columnaDni, Cita::dniPaciente);
        columna(columnaTipo, c -> gestor.catalogo().nombreTipo(c.idTipoPrueba()));
        columna(columnaRecurso, c -> gestor.catalogo().nombreRecurso(c.idRecurso()));
        columna(columnaInicio, c -> FormatosCitas.fechaHora(c.inicio()));
        columna(columnaFin, c -> FormatosCitas.hora(c.fin().toLocalTime()));
        columna(columnaEstado, c -> FormatosCitas.estado(c.estado()));
        columna(columnaMotivo, Cita::motivoCancelacion);
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, antes, ahora) -> actualizarBotones());

        if (catalogo.tipos().isEmpty()) {
            cargarCatalogo();
        }
    }

    /** Pide los tipos de prueba y los recursos; sin ellos no se puede asignar una cita. */
    private void cargarCatalogo() {
        TareaCitas.ejecutar(gestor::cargarCatalogo, this::ocupado, r -> {
            if (r.exito()) {
                tabla.refresh();
                mostrarInfo("Busca citas con los filtros (todos son opcionales) o asigna una nueva.");
            } else {
                mostrarMensaje(r);
            }
        });
    }

    @FXML
    private void buscar() {
        String dni = filtroDni.getText();
        String desde = filtroDesde.getText();
        String hasta = filtroHasta.getText();
        EstadoCita estado = estadoElegido();
        TareaCitas.ejecutar(() -> gestor.buscar(dni, desde, hasta, estado), this::ocupado, r -> {
            if (r.exito()) {
                tabla.getItems().setAll(r.valor());
            }
            mostrarMensaje(r);
        });
    }

    @FXML
    private void nuevaCita() {
        if (!catalogoCargado()) {
            return;
        }
        FormularioCita.abrir(ventana(), gestor, null).ifPresent(this::tratarGuardada);
    }

    @FXML
    private void reprogramar() {
        Cita cita = tabla.getSelectionModel().getSelectedItem();
        if (cita == null || !catalogoCargado()) {
            return;
        }
        FormularioCita.abrir(ventana(), gestor, cita).ifPresent(this::tratarGuardada);
    }

    @FXML
    private void cancelarCita() {
        Cita cita = tabla.getSelectionModel().getSelectedItem();
        if (cita == null) {
            return;
        }
        Optional<String> motivo = pedirMotivo(cita);
        if (motivo.isEmpty()) {
            mostrarInfo("No se ha cancelado la cita.");
            return;
        }
        TareaCitas.ejecutar(() -> gestor.cancelar(cita, motivo.get()), this::ocupado, r -> {
            if (r.exito()) {
                sustituir(r.valor());
            }
            mostrarMensaje(r);
        });
    }

    /** Pregunta de confirmación con un cuadro para escribir el motivo (obligatorio, DEC-046). */
    private Optional<String> pedirMotivo(Cita cita) {
        ButtonType confirmar = new ButtonType("Cancelar la cita", ButtonBar.ButtonData.OK_DONE);
        ButtonType volver = new ButtonType("Volver", ButtonBar.ButtonData.CANCEL_CLOSE);
        Label pregunta = new Label(gestor.preguntaConfirmacionCancelar(cita));
        pregunta.setWrapText(true);
        TextArea texto = new TextArea();
        texto.setPromptText("Motivo de la cancelación (obligatorio, hasta 255 caracteres)");
        texto.setPrefRowCount(3);
        texto.setWrapText(true);
        VBox contenido = new VBox(10, pregunta, texto);
        contenido.setPrefWidth(460);

        Dialog<String> dialogo = new Dialog<>();
        dialogo.initOwner(ventana());
        dialogo.setTitle("Confirmar cancelación");
        dialogo.setHeaderText("Cancelar la cita nº " + cita.id());
        dialogo.getDialogPane().setContent(contenido);
        dialogo.getDialogPane().getButtonTypes().setAll(confirmar, volver);
        dialogo.setResultConverter(boton -> boton == confirmar ? texto.getText() : null);
        return dialogo.showAndWait();
    }

    private void tratarGuardada(ResultadoCitas<Cita> resultado) {
        if (!sustituir(resultado.valor())) {
            tabla.getItems().add(resultado.valor());
        }
        tabla.getSelectionModel().select(resultado.valor());
        mostrarMensaje(resultado);
    }

    /** Cambia en la tabla la cita con el mismo número. Devuelve {@code false} si no estaba. */
    private boolean sustituir(Cita cita) {
        List<Cita> filas = tabla.getItems();
        for (int i = 0; i < filas.size(); i++) {
            if (filas.get(i).id() == cita.id()) {
                filas.set(i, cita);
                return true;
            }
        }
        return false;
    }

    private boolean catalogoCargado() {
        if (gestor.catalogo().tipos().isEmpty() || gestor.catalogo().recursos().isEmpty()) {
            cargarCatalogo();
            return false;
        }
        return true;
    }

    private EstadoCita estadoElegido() {
        int indice = filtroEstado.getSelectionModel().getSelectedIndex();
        return indice <= 0 ? null : EstadoCita.values()[indice - 1];
    }

    private void actualizarBotones() {
        boolean editable = gestor.puedeModificar(tabla.getSelectionModel().getSelectedItem());
        botonReprogramar.setDisable(!editable);
        botonCancelarCita.setDisable(!editable);
    }

    private void ocupado(boolean ocupado) {
        filtroDni.setDisable(ocupado);
        filtroDesde.setDisable(ocupado);
        filtroHasta.setDisable(ocupado);
        filtroEstado.setDisable(ocupado);
        botonBuscar.setDisable(ocupado);
        botonNueva.setDisable(ocupado);
        if (ocupado) {
            botonReprogramar.setDisable(true);
            botonCancelarCita.setDisable(true);
            mostrarInfo("Conectando con el servidor…");
        } else {
            actualizarBotones();
        }
    }

    private void mostrarMensaje(ResultadoCitas<?> resultado) {
        mensaje.setText(resultado.mensaje() == null ? "" : resultado.mensaje());
        estilo(resultado.exito() ? "mensaje-ok" : "mensaje-error");
    }

    private void mostrarInfo(String texto) {
        mensaje.setText(texto);
        estilo("mensaje-info");
    }

    private void estilo(String clase) {
        mensaje.getStyleClass().removeAll("mensaje-ok", "mensaje-error", "mensaje-info");
        mensaje.getStyleClass().add(clase);
    }

    private Window ventana() {
        return tabla.getScene().getWindow();
    }

    private static void columna(TableColumn<Cita, String> columna, Function<Cita, String> valor) {
        columna.setCellValueFactory(fila -> new ReadOnlyStringWrapper(valor.apply(fila.getValue())));
    }
}
