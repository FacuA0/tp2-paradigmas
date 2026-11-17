package TorneoInmortales;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EfectosTemporalesTest {

    private Combatiente c, rival;
    private EfectoTemporal veneno, regeneracion;

    @BeforeEach
    void preparar() {
        c = new CultivadorDeLaEspada(1, "Aurelio");
        rival = new CultivadorDeLaEspada(2, "Darian");
        veneno = new Veneno();
        regeneracion = new Regeneracion();
    }

    // 1
    @Test
    void aplicarUnEfectoNoHaceDanioYQuedaConSuDuracionCompleta() {
        c.aplicarEfecto(veneno);

        assertTrue(c.tieneEfecto(veneno));
        assertEquals(Veneno.DURACION, c.turnosRestantesDe(veneno));
        assertEquals(c.getVidaMaxima(), c.getVidaActual());
    }

    // 2
    @Test
    void cadaTurnoElVenenoQuitaVidaYDescuentaUnTurno() {
        int vida = c.getVidaActual();
        c.aplicarEfecto(veneno);

        c.procesarEfectos();

        assertEquals(vida - Veneno.DANIO_POR_TURNO, c.getVidaActual());
        assertEquals(Veneno.DURACION - 1, c.turnosRestantesDe(veneno));
    }

    // 3
    @Test
    void elEfectoDuraExactamenteSusTurnosYDespuesSeQuita() {
        int vida = c.getVidaActual();
        c.aplicarEfecto(veneno);

        for (int i = 0; i < Veneno.DURACION; i++) {
            c.procesarEfectos();
        }

        assertFalse(c.tieneEfecto(veneno));
        assertEquals(vida - Veneno.DURACION * Veneno.DANIO_POR_TURNO, c.getVidaActual());

        int vidaFinal = c.getVidaActual();
        c.procesarEfectos();
        assertEquals(vidaFinal, c.getVidaActual());
    }

    // 4
    @Test
    void elVenenoIgnoraLaArmaduraYElTipo() {
        Combatiente monje = new MonjeCuerpoCelestial(3, "Kael");
        Equipamiento armadura = new ArmaduraDorada();
        monje.agregarAlInventario(armadura);
        monje.equipar(armadura);
        int vida = monje.getVidaActual();

        monje.aplicarEfecto(veneno);
        monje.procesarEfectos();

        assertEquals(vida - Veneno.DANIO_POR_TURNO, monje.getVidaActual());
    }

    // 5
    @Test
    void laRegeneracionCuraPorTurno() {
        c.recibirDanio(30);
        int herido = c.getVidaActual();
        c.aplicarEfecto(regeneracion);

        c.procesarEfectos();

        assertEquals(herido + Regeneracion.CURACION_POR_TURNO, c.getVidaActual());
    }

    // 6
    @Test
    void laRegeneracionNoSuperaLaVidaMaxima() {
        c.recibirDanio(2);
        c.aplicarEfecto(regeneracion);

        c.procesarEfectos();

        assertEquals(c.getVidaMaxima(), c.getVidaActual());
    }

    // 7
    @Test
    void reaplicarRenuevaLaDuracionYNoDuplica() {
        c.aplicarEfecto(veneno);
        c.procesarEfectos();
        c.procesarEfectos();
        assertEquals(Veneno.DURACION - 2, c.turnosRestantesDe(veneno));

        c.aplicarEfecto(new Veneno());

        assertEquals(Veneno.DURACION, c.turnosRestantesDe(veneno));
        assertEquals(1, c.getEfectos().size());
    }

    // 8
    @Test
    void reaplicarConOtroValorReemplazaAlAnterior() {
        c.aplicarEfecto(new Veneno(2, 3));
        c.aplicarEfecto(new Veneno(10, 3));
        int vida = c.getVidaActual();

        c.procesarEfectos();

        assertEquals(vida - 10, c.getVidaActual());
    }

    // 9
    @Test
    void dosEfectosDistintosConvivenYActuanEnOrden() {
        int vida = c.getVidaActual();
        c.aplicarEfecto(veneno);
        c.aplicarEfecto(regeneracion);

        c.procesarEfectos();

        assertEquals(vida - Veneno.DANIO_POR_TURNO + Regeneracion.CURACION_POR_TURNO, c.getVidaActual());
        assertEquals(List.of(veneno, regeneracion), c.getEfectos());
    }

    // 10
    @Test
    void unCaidoNoSufreEfectos() {
        c.recibirDanio(10_000);
        c.aplicarEfecto(regeneracion);

        c.procesarEfectos();

        assertEquals(0, c.getVidaActual());
        assertFalse(c.estaEnPie());
        assertEquals(Regeneracion.DURACION, c.turnosRestantesDe(regeneracion));
    }

    // 11
    @Test
    void siElVenenoLoDejaSinVidaLosEfectosSiguientesNoActuan() {
        c.recibirDanio(c.getVidaActual() - 1);
        c.aplicarEfecto(veneno);
        c.aplicarEfecto(regeneracion);

        c.procesarEfectos();

        assertEquals(0, c.getVidaActual());
        assertFalse(c.estaEnPie());
        assertEquals(Regeneracion.DURACION, c.turnosRestantesDe(regeneracion));
    }

    // 12
    @Test
    void prepararParaCombateLimpiaLosEfectos() {
        c.aplicarEfecto(veneno);
        c.aplicarEfecto(regeneracion);

        c.prepararParaCombate();

        assertTrue(c.getEfectos().isEmpty());
        assertFalse(c.tieneEfecto(veneno));
    }

    // 13
    @Test
    void rechazaNulosYLaListaDeEfectosEsDeSoloLectura() {
        assertThrows(NullPointerException.class, () -> c.aplicarEfecto(null));

        c.aplicarEfecto(veneno);
        assertThrows(UnsupportedOperationException.class, () -> c.getEfectos().clear());
    }

    // 14
    @Test
    void dosEfectosConElMismoNombreSonIguales() {
        assertEquals(new Veneno(), new Veneno(99, 5));
        assertEquals(new Veneno().hashCode(), new Veneno(99, 5).hashCode());
        assertNotEquals(veneno, regeneracion);
    }

    // 15
    @Test
    void rechazaDuracionesYValoresInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new Veneno(0, 3));
        assertThrows(IllegalArgumentException.class, () -> new Veneno(5, 0));
        assertThrows(IllegalArgumentException.class, () -> new Regeneracion(0, 3));
        assertThrows(IllegalArgumentException.class, () -> new Regeneracion(5, 0));
    }

    // 16
    @Test
    void unaInstanciaCompartidaNoCompartaElEstado() {
        c.aplicarEfecto(veneno);
        rival.aplicarEfecto(veneno);

        c.procesarEfectos();

        assertEquals(Veneno.DURACION - 1, c.turnosRestantesDe(veneno));
        assertEquals(Veneno.DURACION, rival.turnosRestantesDe(veneno));
    }
}