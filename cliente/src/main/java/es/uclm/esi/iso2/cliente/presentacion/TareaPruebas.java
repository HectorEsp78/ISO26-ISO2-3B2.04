package es.uclm.esi.iso2.cliente.presentacion;

import es.uclm.esi.iso2.cliente.logica.ResultadoPruebas;
import javafx.concurrent.Task;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Ejecuta una operación de CU10 con el servidor fuera del hilo de JavaFX para que la ventana no
 * se congele mientras espera la respuesta.
 */
final class TareaPruebas {

    private TareaPruebas() {
    }

    /**
     * @param operacion  llamada al gestor (se ejecuta en segundo plano)
     * @param ocupado    se llama con {@code true} al empezar y con {@code false} al terminar
     * @param alTerminar recibe el resultado en el hilo de JavaFX
     */
    static <T> void ejecutar(Supplier<ResultadoPruebas<T>> operacion, Consumer<Boolean> ocupado,
            Consumer<ResultadoPruebas<T>> alTerminar) {
        Task<ResultadoPruebas<T>> tarea = new Task<>() {
            @Override
            protected ResultadoPruebas<T> call() {
                return operacion.get();
            }
        };
        tarea.setOnSucceeded(e -> {
            ocupado.accept(false);
            alTerminar.accept(tarea.getValue());
        });
        tarea.setOnFailed(e -> {
            ocupado.accept(false);
            alTerminar.accept(ResultadoPruebas.error("Error inesperado: " + tarea.getException().getMessage(), null));
        });
        ocupado.accept(true);
        Thread hilo = new Thread(tarea, "peticion-servidor");
        hilo.setDaemon(true);
        hilo.start();
    }
}
