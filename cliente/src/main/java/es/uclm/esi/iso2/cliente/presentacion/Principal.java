package es.uclm.esi.iso2.cliente.presentacion;

import javafx.application.Application;

/**
 * Punto de entrada del cliente.
 *
 * <p>Es una clase aparte de {@link AplicacionCliente} para que el cliente arranque aunque JavaFX
 * esté en el classpath y no en el module path.</p>
 */
public final class Principal {

    private Principal() {
    }

    public static void main(String[] args) {
        Application.launch(AplicacionCliente.class, args);
    }
}
