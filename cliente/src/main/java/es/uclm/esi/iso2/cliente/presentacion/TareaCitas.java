package es.uclm.esi.iso2.cliente.presentacion;

import es.uclm.esi.iso2.cliente.logica.ResultadoCitas;
import javafx.concurrent.Task;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Igual que {@link TareaSegundoPlano} (CU12), pero para los resultados de citas: ejecuta la
 * petición al servidor fuera del hilo de JavaFX para que la ventana no se congele.
 */
final class TareaCitas {

    private TareaCitas() {
    }

    /**
     * @param operacion  llamada al gestor (se ejecuta en segundo plano)
     * @param ocupado    se llama con {@code true} al empezar y con {@code false} al terminar
     * @param alTerminar recibe el resultado en el hilo de JavaFX
     */
    static <T> void ejecutar(Supplier<ResultadoCitas<T>> operacion, Consumer<Boolean> ocupado,
            Consumer<ResultadoCitas<T>> alTerminar) {
        Task<ResultadoCitas<T>> tarea = new Task<>() {
            @Override
            protected ResultadoCitas<T> call() {
                return operacion.get();
            }
        };
        tarea.setOnSucceeded(e -> {
            ocupado.accept(false);
            alTerminar.accept(tarea.getValue());
        });
        tarea.setOnFailed(e -> {
            ocupado.accept(false);
            alTerminar.accept(ResultadoCitas.error("Error inesperado: " + tarea.getException().getMessage(),
                    List.of()));
        });
        ocupado.accept(true);
        Thread hilo = new Thread(tarea, "peticion-citas");
        hilo.setDaemon(true);
        hilo.start();
    }
}
