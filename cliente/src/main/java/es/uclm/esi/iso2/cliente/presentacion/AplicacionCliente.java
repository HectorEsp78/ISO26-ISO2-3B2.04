package es.uclm.esi.iso2.cliente.presentacion;

import es.uclm.esi.iso2.cliente.logica.GestorCitasCliente;
import es.uclm.esi.iso2.cliente.logica.GestorPacientesCliente;
import es.uclm.esi.iso2.cliente.servicio.FabricaServicio;
import es.uclm.esi.iso2.cliente.servicio.FabricaServicioCitas;
import es.uclm.esi.iso2.cliente.servicio.ServicioCitas;
import es.uclm.esi.iso2.cliente.servicio.ServicioCitasSocket;
import es.uclm.esi.iso2.cliente.servicio.ServicioPacientes;
import es.uclm.esi.iso2.cliente.servicio.ServicioPacientesSocket;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Aplicación JavaFX del cliente. Mientras no exista CU23 arranca directamente en la pantalla de
 * pacientes (DEC-059).
 */
public class AplicacionCliente extends Application {

    /** Con {@code -Dradiodiagnostico.pantalla=citas} se abre la pantalla de CU13 en lugar de la de pacientes. */
    public static final String PROPIEDAD_PANTALLA = "radiodiagnostico.pantalla";
    public static final String PANTALLA_CITAS = "citas";

    @Override
    public void start(Stage ventana) throws IOException {
        if (PANTALLA_CITAS.equals(System.getProperty(PROPIEDAD_PANTALLA))) {
            abrirCitas(ventana);
            return;
        }
        ServicioPacientes servicio;
        try {
            servicio = FabricaServicio.crear(System::getProperty);
        } catch (IllegalArgumentException e) {
            new Alert(Alert.AlertType.ERROR, "Configuración no válida: " + e.getMessage()).showAndWait();
            return;
        }
        GestorPacientesCliente gestor = new GestorPacientesCliente(servicio);

        FXMLLoader cargador = new FXMLLoader(getClass().getResource("PantallaBusquedaPacientes.fxml"));
        cargador.setControllerFactory(tipo -> new PantallaBusquedaPacientes(gestor));
        Parent raiz = cargador.load();

        Scene escena = new Scene(raiz, 820, 560);
        escena.getStylesheets().add(getClass().getResource("estilos.css").toExternalForm());
        ventana.setTitle("Radiodiagnóstico · Gestión de pacientes" + descripcion(servicio));
        ventana.setMinWidth(700);
        ventana.setMinHeight(480);
        ventana.setScene(escena);
        ventana.show();
    }

    private static String descripcion(ServicioPacientes servicio) {
        if (servicio instanceof ServicioPacientesSocket socket) {
            return " · servidor " + socket.host() + ":" + socket.puerto();
        }
        return " · MODO SIN SERVIDOR (datos de ejemplo, no se guardan)";
    }

    /** Pantalla de CU13, provisional hasta que CU23 ofrezca el menú principal. */
    private void abrirCitas(Stage ventana) throws IOException {
        ServicioCitas servicio;
        try {
            servicio = FabricaServicioCitas.crear(System::getProperty);
        } catch (IllegalArgumentException e) {
            new Alert(Alert.AlertType.ERROR, "Configuración no válida: " + e.getMessage()).showAndWait();
            return;
        }
        GestorCitasCliente gestor = new GestorCitasCliente(servicio);

        FXMLLoader cargador = new FXMLLoader(getClass().getResource("PantallaBusquedaCitas.fxml"));
        cargador.setControllerFactory(tipo -> new PantallaBusquedaCitas(gestor));
        Parent raiz = cargador.load();

        Scene escena = new Scene(raiz, 1020, 600);
        escena.getStylesheets().add(getClass().getResource("estilos.css").toExternalForm());
        String donde = servicio instanceof ServicioCitasSocket socket
                ? " · servidor " + socket.host() + ":" + socket.puerto()
                : " · MODO SIN SERVIDOR (datos de ejemplo, no se guardan)";
        ventana.setTitle("Radiodiagnóstico · Gestión de citas" + donde);
        ventana.setMinWidth(900);
        ventana.setMinHeight(480);
        ventana.setScene(escena);
        ventana.show();
    }
}
