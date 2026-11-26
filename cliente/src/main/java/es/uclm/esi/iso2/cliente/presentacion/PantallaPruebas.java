package es.uclm.esi.iso2.cliente.presentacion;

import es.uclm.esi.iso2.cliente.logica.FormatosPruebas;
import es.uclm.esi.iso2.cliente.logica.GestorPruebasCliente;
import es.uclm.esi.iso2.cliente.logica.ResultadoPruebas;
import es.uclm.esi.iso2.comun.dominio.EstadoPrueba;
import es.uclm.esi.iso2.comun.dominio.Incidencia;
import es.uclm.esi.iso2.comun.dominio.NuevaIncidencia;
import es.uclm.esi.iso2.comun.dominio.Prueba;
import es.uclm.esi.iso2.comun.dominio.ResumenPrueba;
import es.uclm.esi.iso2.comun.dominio.SolicitudPendiente;
import es.uclm.esi.iso2.comun.dominio.TipoIncidencia;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Controlador de las pantallas de CU10 (fronteras de registro y de consulta de pruebas). Solo
 * muestra lo que devuelve {@link GestorPruebasCliente}; no contiene lógica (DEC-059, DEC-060 y
 * DEC-080).
 */
public class PantallaPruebas {

    private static final String TODOS = "Todos los estados";
    private static final String REALIZADAS = "Realizadas";
    private static final String CANCELADAS = "Canceladas";

    private final GestorPruebasCliente gestor;

    @FXML private Label etiquetaMedico;
    @FXML private TextField campoDniPendientes;
    @FXML private Button botonPendientes;
    @FXML private TableView<SolicitudPendiente> tablaPendientes;
    @FXML private Button botonRealizada;
    @FXML private Button botonCancelada;
    @FXML private TextField filtroDni;
    @FXML private TextField filtroSolicitud;
    @FXML private TextField filtroDesde;
    @FXML private TextField filtroHasta;
    @FXML private ComboBox<String> filtroEstado;
    @FXML private Button botonBuscarPruebas;
    @FXML private TableView<ResumenPrueba> tablaPruebas;
    @FXML private Button botonDetalle;
    @FXML private Label mensaje;

    public PantallaPruebas(GestorPruebasCliente gestor) {
        this.gestor = gestor;
    }

    @FXML
    private void initialize() {
        etiquetaMedico.setText("Médico: " + gestor.idMedico());

        columna(tablaPendientes, "Solicitud", 80, s -> String.valueOf(s.idSolicitud()));
        columna(tablaPendientes, "Paciente", 200, SolicitudPendiente::nombrePaciente);
        columna(tablaPendientes, "DNI/NIE", 100, SolicitudPendiente::dniPaciente);
        columna(tablaPendientes, "Prueba", 180, SolicitudPendiente::tipoPrueba);
        columna(tablaPendientes, "Prioridad", 90, SolicitudPendiente::prioridad);
        columna(tablaPendientes, "Cita", 130, s -> FormatosPruebas.aPantalla(s.inicioCita()));
        tablaPendientes.getSelectionModel().selectedItemProperty().addListener((o, antes, ahora) -> {
            botonRealizada.setDisable(ahora == null);
            botonCancelada.setDisable(ahora == null);
        });

        columna(tablaPruebas, "Prueba", 70, p -> String.valueOf(p.id()));
        columna(tablaPruebas, "Solicitud", 80, p -> String.valueOf(p.idSolicitud()));
        columna(tablaPruebas, "DNI/NIE", 100, ResumenPrueba::dniPaciente);
        columna(tablaPruebas, "Estado", 90, p -> FormatosPruebas.aPantalla(p.estado()));
        columna(tablaPruebas, "Realizada el", 130, p -> FormatosPruebas.aPantalla(p.fechaHoraRealizacion()));
        columna(tablaPruebas, "Registrada el", 130, p -> FormatosPruebas.aPantalla(p.fechaRegistro()));
        columna(tablaPruebas, "Incidencias", 90, p -> String.valueOf(p.numIncidencias()));
        tablaPruebas.getSelectionModel().selectedItemProperty()
                .addListener((o, antes, ahora) -> botonDetalle.setDisable(ahora == null));

        filtroEstado.setItems(FXCollections.observableArrayList(TODOS, REALIZADAS, CANCELADAS));
        filtroEstado.setValue(TODOS);

        buscarPendientes();
    }

    private static <T> void columna(TableView<T> tabla, String titulo, double ancho, Function<T, String> valor) {
        TableColumn<T, String> c = new TableColumn<>(titulo);
        c.setPrefWidth(ancho);
        c.setCellValueFactory(f -> new ReadOnlyStringWrapper(valor.apply(f.getValue())));
        tabla.getColumns().add(c);
    }

    // ---- Flujo 6.1: solicitudes pendientes ----

    @FXML
    private void buscarPendientes() {
        String dni = campoDniPendientes.getText();
        TareaPruebas.ejecutar(() -> gestor.listarPendientes(dni), this::ocupado, resultado -> {
            if (resultado.exito()) {
                tablaPendientes.setItems(FXCollections.observableArrayList(resultado.valor()));
                informar(resultado.valor().isEmpty() ? "No hay solicitudes pendientes de registrar." : "");
            } else {
                mostrarError(resultado);
            }
        });
    }

    // ---- Flujo 6.2: realización ----

    @FXML
    private void registrarRealizacion() {
        SolicitudPendiente solicitud = tablaPendientes.getSelectionModel().getSelectedItem();
        if (solicitud == null) {
            return;
        }
        TextField fecha = new TextField(FormatosPruebas.aPantalla(LocalDateTime.now().withSecond(0).withNano(0)));
        TextArea observaciones = new TextArea();
        observaciones.setPrefRowCount(3);
        observaciones.setWrapText(true);
        List<NuevaIncidencia> incidencias = new ArrayList<>();
        ListView<String> lista = new ListView<>();
        lista.setPrefHeight(80);
        ComboBox<TipoIncidencia> tipo = new ComboBox<>(FXCollections.observableArrayList(TipoIncidencia.values()));
        tipo.setPromptText("Tipo");
        TextField descripcion = new TextField();
        descripcion.setPromptText("Descripción de la incidencia");
        HBox.setHgrow(descripcion, javafx.scene.layout.Priority.ALWAYS);
        Button anadir = new Button("Añadir incidencia");
        Label error = etiquetaError();
        anadir.setOnAction(e -> {
            if (tipo.getValue() == null || descripcion.getText().isBlank()) {
                error.setText("Indica el tipo y la descripción de la incidencia.");
                return;
            }
            error.setText("");
            incidencias.add(new NuevaIncidencia(tipo.getValue(), descripcion.getText()));
            lista.getItems().add(FormatosPruebas.aPantalla(tipo.getValue()) + ": " + descripcion.getText().strip());
            tipo.setValue(null);
            descripcion.clear();
        });

        VBox contenido = new VBox(8,
                new Label(solicitud.nombrePaciente() + " · " + solicitud.tipoPrueba()),
                new Label("Fecha y hora de realización (dd/mm/aaaa hh:mm)"), fecha,
                new Label("Observaciones (opcional)"), observaciones,
                new Label("Incidencias (opcional)"), lista, new HBox(6, tipo, descripcion, anadir), error);
        contenido.setPadding(new Insets(8));
        confirmar("Registrar realización de la prueba", "Registrar", contenido, error, () -> {
            ResultadoPruebas<Prueba> r = gestor.registrarRealizacion(solicitud.idSolicitud(), fecha.getText(),
                    observaciones.getText(), incidencias);
            return r;
        }, resultado -> {
            informarOk(resultado.mensaje());
            buscarPendientes();
        });
    }

    // ---- Flujo 6.3: cancelación ----

    @FXML
    private void registrarCancelacion() {
        SolicitudPendiente solicitud = tablaPendientes.getSelectionModel().getSelectedItem();
        if (solicitud == null) {
            return;
        }
        TextArea motivo = new TextArea();
        motivo.setPrefRowCount(3);
        motivo.setWrapText(true);
        Label error = etiquetaError();
        VBox contenido = new VBox(8, new Label(solicitud.nombrePaciente() + " · " + solicitud.tipoPrueba()),
                new Label("Motivo por el que no se realiza la prueba"), motivo, error);
        contenido.setPadding(new Insets(8));
        confirmar("Cancelar la prueba", "Cancelar prueba", contenido, error,
                () -> gestor.registrarCancelacion(solicitud.idSolicitud(), motivo.getText()), resultado -> {
                    informarOk(resultado.mensaje());
                    buscarPendientes();
                });
    }

    // ---- Flujo 6.4: consulta ----

    @FXML
    private void buscarPruebas() {
        String dni = filtroDni.getText();
        String solicitud = filtroSolicitud.getText();
        String desde = filtroDesde.getText();
        String hasta = filtroHasta.getText();
        EstadoPrueba estado = switch (filtroEstado.getValue() == null ? TODOS : filtroEstado.getValue()) {
            case REALIZADAS -> EstadoPrueba.REALIZADA;
            case CANCELADAS -> EstadoPrueba.CANCELADA;
            default -> null;
        };
        TareaPruebas.ejecutar(() -> gestor.buscar(dni, solicitud, desde, hasta, estado), this::ocupado, resultado -> {
            if (resultado.exito()) {
                tablaPruebas.setItems(FXCollections.observableArrayList(resultado.valor()));
                informar(resultado.mensaje());
            } else {
                mostrarError(resultado);
            }
        });
    }

    // ---- Flujos 6.5 y 6.6: detalle e incidencias ----

    @FXML
    private void verDetalle() {
        ResumenPrueba seleccionada = tablaPruebas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            return;
        }
        TareaPruebas.ejecutar(() -> gestor.obtener(seleccionada.id()), this::ocupado, resultado -> {
            if (resultado.exito()) {
                mostrarDetalle(resultado.valor());
            } else {
                mostrarError(resultado);
            }
        });
    }

    private void mostrarDetalle(Prueba prueba) {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.initOwner(ventana());
        dialogo.setTitle("Prueba " + prueba.id());
        dialogo.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        GridPane datos = new GridPane();
        datos.setHgap(12);
        datos.setVgap(6);
        String[][] filas = {
            {"Solicitud", String.valueOf(prueba.idSolicitud())},
            {"Paciente (DNI/NIE)", prueba.dniPaciente()},
            {"Estado", FormatosPruebas.aPantalla(prueba.estado())},
            {"Realizada el", FormatosPruebas.aPantalla(prueba.fechaHoraRealizacion())},
            {"Registrada el", FormatosPruebas.aPantalla(prueba.fechaRegistro())},
            {"Observaciones", vacioAGuion(prueba.observaciones())},
            {"Motivo de cancelación", vacioAGuion(prueba.motivoCancelacion())},
        };
        for (int i = 0; i < filas.length; i++) {
            Label etiqueta = new Label(filas[i][0]);
            etiqueta.getStyleClass().add("etiqueta");
            Label valor = new Label(filas[i][1]);
            valor.setWrapText(true);
            datos.add(etiqueta, 0, i);
            datos.add(valor, 1, i);
        }

        ListView<String> incidencias = new ListView<>();
        incidencias.setPrefHeight(110);
        for (Incidencia i : prueba.incidencias()) {
            incidencias.getItems().add(FormatosPruebas.aPantalla(i.fechaHora()) + " · "
                    + FormatosPruebas.aPantalla(i.tipo()) + ": " + i.descripcion());
        }
        ComboBox<TipoIncidencia> tipo = new ComboBox<>(FXCollections.observableArrayList(TipoIncidencia.values()));
        tipo.setPromptText("Tipo");
        TextField descripcion = new TextField();
        descripcion.setPromptText("Nueva incidencia");
        HBox.setHgrow(descripcion, javafx.scene.layout.Priority.ALWAYS);
        Button anadir = new Button("Añadir incidencia");
        Label error = etiquetaError();
        anadir.setOnAction(e -> TareaPruebas.ejecutar(
                () -> gestor.anadirIncidencia(prueba.id(), tipo.getValue(), descripcion.getText()),
                ocupado -> anadir.setDisable(ocupado),
                resultado -> {
                    if (resultado.exito()) {
                        Incidencia i = resultado.valor();
                        incidencias.getItems().add(FormatosPruebas.aPantalla(i.fechaHora()) + " · "
                                + FormatosPruebas.aPantalla(i.tipo()) + ": " + i.descripcion());
                        tipo.setValue(null);
                        descripcion.clear();
                        error.setText("");
                        buscarPruebas();
                    } else {
                        error.setText(resultado.mensaje());
                    }
                }));

        VBox contenido = new VBox(10, datos, new Label("Incidencias"), incidencias,
                new HBox(6, tipo, descripcion, anadir), error);
        contenido.setPadding(new Insets(8));
        dialogo.getDialogPane().setContent(contenido);
        dialogo.showAndWait();
    }

    // ---- Auxiliares ----

    /**
     * Muestra un diálogo con Aceptar/Cancelar. Al aceptar ejecuta la operación en segundo plano; si
     * falla deja el diálogo abierto con el mensaje y si va bien lo cierra y avisa.
     */
    private <T> void confirmar(String titulo, String textoAceptar, VBox contenido, Label error,
            java.util.function.Supplier<ResultadoPruebas<T>> operacion,
            java.util.function.Consumer<ResultadoPruebas<T>> alTerminar) {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.initOwner(ventana());
        dialogo.setTitle(titulo);
        ButtonType aceptar = new ButtonType(textoAceptar, javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialogo.getDialogPane().getButtonTypes().addAll(aceptar, ButtonType.CANCEL);
        dialogo.getDialogPane().setContent(contenido);
        Button botonAceptar = (Button) dialogo.getDialogPane().lookupButton(aceptar);
        botonAceptar.addEventFilter(ActionEvent.ACTION, evento -> {
            evento.consume();
            TareaPruebas.ejecutar(operacion, botonAceptar::setDisable, resultado -> {
                if (resultado.exito()) {
                    dialogo.setResult(aceptar);
                    dialogo.close();
                    alTerminar.accept(resultado);
                } else {
                    error.setText(resultado.mensaje());
                }
            });
        });
        dialogo.showAndWait();
    }

    private static Label etiquetaError() {
        Label etiqueta = new Label();
        etiqueta.getStyleClass().add("mensaje-error");
        etiqueta.setWrapText(true);
        return etiqueta;
    }

    private static String vacioAGuion(String texto) {
        return texto == null || texto.isBlank() ? "—" : texto;
    }

    private void ocupado(boolean ocupado) {
        botonPendientes.setDisable(ocupado);
        botonBuscarPruebas.setDisable(ocupado);
        if (ocupado) {
            informar("Conectando con el servidor…");
        }
    }

    private void mostrarError(ResultadoPruebas<?> resultado) {
        mensaje.setText(resultado.mensaje());
        estilo("mensaje-error");
    }

    private void informarOk(String texto) {
        mensaje.setText(texto);
        estilo("mensaje-ok");
    }

    private void informar(String texto) {
        mensaje.setText(texto);
        estilo("mensaje-info");
    }

    private void estilo(String clase) {
        mensaje.getStyleClass().removeAll("mensaje-ok", "mensaje-error", "mensaje-info");
        mensaje.getStyleClass().add(clase);
    }

    private Window ventana() {
        return mensaje.getScene().getWindow();
    }
}
