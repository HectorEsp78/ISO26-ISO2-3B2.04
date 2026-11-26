package es.uclm.esi.iso2.cliente.presentacion;

import es.uclm.esi.iso2.cliente.logica.Resultado;
import javafx.concurrent.Task;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Ejecuta una operación con el servidor fuera del hilo de JavaFX para que la ventana no se
 * congele mientras espera la respuesta.
 */
final class TareaSegundoPlano {

    private TareaSegundoPlano() {
    }

    /**
     * @param operacion  llamada al gestor (se ejecuta en segundo plano)
     * @param ocupado    se llama con {@code true} al empezar y con {@code false} al terminar
     * @param alTerminar recibe el resultado en el hilo de JavaFX
     */
    static void ejecutar(Supplier<Resultado> operacion, Consumer<Boolean> ocupado, Consumer<Resultado> alTerminar) {
        Task<Resultado> tarea = new Task<>() {
            @Override
            protected Resultado call() {
                return operacion.get();
            }
        };
        tarea.setOnSucceeded(e -> {
            ocupado.accept(false);
            alTerminar.accept(tarea.getValue());
        });
        tarea.setOnFailed(e -> {
            ocupado.accept(false);
            alTerminar.accept(Resultado.error("Error inesperado: " + tarea.getException().getMessage(), List.of()));
        });
        ocupado.accept(true);
        Thread hilo = new Thread(tarea, "peticion-servidor");
        hilo.setDaemon(true);
        hilo.start();
    }
}
