package TorneoInmortales;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TecnicasConEfectoTest {

    private Combatiente c, rival;
    private Tecnica niebla, loto;

    @BeforeEach
    void preparar() {
        c = new CultivadorDeLaEspada(1, "Aurelio");
        rival = new CultivadorDeLaEspada(2, "Darian");
        niebla = new NieblaVenenosaDelVacio();
        loto = new LotoRegenerador();
    }

    // 1
    @Test
    void laNieblaDaniaYEnvenenaAlObjetivoYNoAlEjecutor() {
        int vidaRival = rival.getVidaActual();

        niebla.ejecutar(c, rival);

        assertTrue(rival.getVidaActual() < vidaRival);
        assertTrue(rival.tieneEfecto(new Veneno()));
        assertFalse(c.tieneEfecto(new Veneno()));
        assertEquals(c.getVidaMaxima(), c.getVidaActual());
    }

    // 2
    @Test
    void laNieblaNoEnvenenaAUnObjetivoQueCae() {
        rival.recibirDanio(rival.getVidaActual() - 1);

        niebla.ejecutar(c, rival);

        assertFalse(rival.estaEnPie());
        assertFalse(rival.tieneEfecto(new Veneno()));
    }

    // 3
    @Test
    void elLotoCuraDeInmediatoYDejaRegeneracionEnElEjecutor() {
        c.recibirDanio(50);
        int herido = c.getVidaActual();

        loto.ejecutar(c, c);

        assertTrue(c.getVidaActual() > herido);
        assertTrue(c.tieneEfecto(new Regeneracion()));
    }

    // 4
    @Test
    void usarLaNieblaDosVecesRenuevaElVenenoSinAcumularlo() {
        niebla.ejecutar(c, rival);
        rival.procesarEfectos();
        niebla.ejecutar(c, rival);

        assertEquals(Veneno.DURACION, rival.turnosRestantesDe(new Veneno()));
        assertEquals(1, rival.getEfectos().size());
    }

    // 5
    @Test
    void laCategoriaDeCadaTecnicaEsLaCorrecta() {
        assertEquals(TipoTecnica.OFENSIVA, niebla.getCategoria());
        assertEquals(TipoTecnica.RECUPERACION, loto.getCategoria());
    }
}