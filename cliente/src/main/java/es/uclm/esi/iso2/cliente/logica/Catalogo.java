package es.uclm.esi.iso2.cliente.logica;

import es.uclm.esi.iso2.comun.dominio.Recurso;
import es.uclm.esi.iso2.comun.dominio.TipoPrueba;

import java.util.List;
import java.util.Optional;

/**
 * Tipos de prueba y recursos que ofrece el servidor (DEC-047). Las citas solo guardan los
 * identificadores, y este catálogo sirve para mostrar los nombres.
 *
 * @param tipos    tipos de prueba
 * @param recursos salas y equipos
 */
public record Catalogo(List<TipoPrueba> tipos, List<Recurso> recursos) {

    /** Catálogo sin datos, mientras no se ha cargado. */
    public static final Catalogo VACIO = new Catalogo(List.of(), List.of());

    public Catalogo {
        tipos = List.copyOf(tipos);
        recursos = List.copyOf(recursos);
    }

    public Optional<TipoPrueba> tipo(int id) {
        return tipos.stream().filter(t -> t.id() == id).findFirst();
    }

    public Optional<Recurso> recurso(int id) {
        return recursos.stream().filter(r -> r.id() == id).findFirst();
    }

    /** Nombre del tipo de prueba, o «Prueba nº id» si no está en el catálogo. */
    public String nombreTipo(int id) {
        return tipo(id).map(TipoPrueba::nombre).orElse("Prueba nº " + id);
    }

    /** Nombre del recurso, o «Recurso nº id» si no está en el catálogo. */
    public String nombreRecurso(int id) {
        return recurso(id).map(Recurso::nombre).orElse("Recurso nº " + id);
    }
}
