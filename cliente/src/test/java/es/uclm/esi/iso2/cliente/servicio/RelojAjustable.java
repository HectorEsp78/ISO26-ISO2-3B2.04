package es.uclm.esi.iso2.cliente.servicio;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Reloj para las pruebas: marca el momento que se le indica y se puede mover, para probar citas
 * que ya han empezado (RN-08).
 */
public class RelojAjustable extends Clock {

    private static final ZoneId ZONA = ZoneId.systemDefault();

    /** Miércoles 25/11/2026 a las 12:00. */
    public static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 25, 12, 0);

    private Instant instante;

    public RelojAjustable() {
        this(AHORA);
    }

    public RelojAjustable(LocalDateTime momento) {
        poner(momento);
    }

    public final void poner(LocalDateTime momento) {
        instante = momento.atZone(ZONA).toInstant();
    }

    @Override
    public ZoneId getZone() {
        return ZONA;
    }

    @Override
    public Clock withZone(ZoneId zona) {
        return this;
    }

    @Override
    public Instant instant() {
        return instante;
    }
}
