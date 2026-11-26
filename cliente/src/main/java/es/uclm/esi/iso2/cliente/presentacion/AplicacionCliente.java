package es.uclm.esi.iso2.cliente.presentacion;

import es.uclm.esi.iso2.cliente.logica.GestorPacientesCliente;
import es.uclm.esi.iso2.cliente.servicio.FabricaServicio;
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

    @Override
    public void start(Stage ventana) throws IOException {
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
}
