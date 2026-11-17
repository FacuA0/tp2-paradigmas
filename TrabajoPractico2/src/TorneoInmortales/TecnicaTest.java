package TorneoInmortales;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TecnicaTest {

    private Combatiente c;
    private Combatiente rival;
    private Tecnica corte, meditacion, muro, dominio;

    @BeforeEach
    void preparar() {
        c = new CultivadorDeLaEspada(1, "Aurelio");
        rival = new MonjeCuerpoCelestial(2, "Kael");
        corte = new CorteDeLosMilAmaneceres();
        meditacion = new MeditacionDelInmortal();
        muro = new MuroDelCuerpoDorado();
        dominio = new DominioDeLaNada();
    }

    // 1
    @Test
    void unaTecnicaAprendidaArrancaConCincoUsos() {
        c.aprenderTecnica(corte);

        assertEquals(5, c.usosRestantes(corte));
    }

    // 2
    @Test
    void aprenderDosVecesLaMismaTecnicaSeRechazaSinCambios() {
        c.aprenderTecnica(corte);

        assertThrows(IllegalArgumentException.class, () -> c.aprenderTecnica(corte));

        assertEquals(List.of(corte), c.getTecnicas());
        assertEquals(5, c.usosRestantes(corte));
    }

    // 3
    @Test
    void usarUnaTecnicaDescuentaUnUsoYSuCostoDeEnergia() {
        c.aprenderTecnica(corte);
        int energiaAntes = c.getEnergiaActual();

        c.usarTecnica(corte);

        assertEquals(4, c.usosRestantes(corte));
        assertEquals(energiaAntes - c.costoDe(corte), c.getEnergiaActual());
    }

    // 4
    @Test
    void sinEnergiaNoSeEjecutaYNoCambiaNada() {
        c.aprenderTecnica(corte);
        c.perderEnergia(c.getEnergiaActual());
        int usosAntes = c.usosRestantes(corte);

        assertThrows(TecnicaNoDisponibleException.class, () -> c.usarTecnica(corte));

        assertEquals(0, c.getEnergiaActual());
        assertEquals(usosAntes, c.usosRestantes(corte));
    }

    // 5
    @Test
    void sinUsosNoSeEjecutaAunqueSobreEnergia() {
        c.aprenderTecnica(corte);
        for (int i = 0; i < 5; i++) {
            c.prepararParaCombate();
            c.usarTecnica(corte);
        }
        c.prepararParaCombate();
        assertTrue(c.getEnergiaActual() >= c.costoDe(corte));

        assertEquals(0, c.usosRestantes(corte));
        assertFalse(c.puedeUsar(corte));
        assertThrows(TecnicaNoDisponibleException.class, () -> c.usarTecnica(corte));
    }

    // 6
    @Test
    void usarUnaTecnicaQueNoConoceSeRechaza() {
        int energiaAntes = c.getEnergiaActual();

        assertThrows(TecnicaNoDisponibleException.class, () -> c.usarTecnica(corte));

        assertEquals(energiaAntes, c.getEnergiaActual());
    }

    // 7
    @Test
    void puedeUsarRefleja​EnergiaYUsosSinModificarNada() {
        c.aprenderTecnica(corte);
        assertTrue(c.puedeUsar(corte));

        c.perderEnergia(c.getEnergiaActual());
        int usos = c.usosRestantes(corte);
        assertFalse(c.puedeUsar(corte));

        assertEquals(usos, c.usosRestantes(corte));
        assertEquals(0, c.getEnergiaActual());
        assertFalse(c.puedeUsar(muro));
    }

    // 8
    @Test
    void losUsosSeLlevanPorCombatiente() {
        Combatiente otro = new CultivadorDeLaEspada(3, "Darian");
        c.aprenderTecnica(corte);
        otro.aprenderTecnica(corte);

        c.usarTecnica(corte);

        assertEquals(4, c.usosRestantes(corte));
        assertEquals(5, otro.usosRestantes(corte));
    }

    // 9
    @Test
    void losUsosSeConservanEntreCombatesYVidaYEnergiaSeRecuperan() {
        c.aprenderTecnica(corte);
        c.prepararParaCombate();
        c.usarTecnica(corte);
        c.recibirDanio(15);
        assertTrue(c.getEnergiaActual() < c.getEnergiaMaxima());
        assertTrue(c.getVidaActual() < c.getVidaMaxima());

        c.prepararParaCombate();

        assertEquals(c.getVidaMaxima(), c.getVidaActual());
        assertEquals(c.getEnergiaMaxima(), c.getEnergiaActual());
        assertEquals(4, c.usosRestantes(corte));
    }

    // 10
    @Test
    void lasTecnicasSeListanEnOrdenDeAprendizaje() {
        c.aprenderTecnica(muro);
        c.aprenderTecnica(corte);
        c.aprenderTecnica(meditacion);

        assertEquals(List.of(muro, corte, meditacion), c.getTecnicas());
    }

    // 11
    @Test
    void laEnergiaNoSuperaElMaximoNiBajaDeCero() {
        c.recuperarEnergia(1000);
        assertEquals(c.getEnergiaMaxima(), c.getEnergiaActual());

        c.perderEnergia(1000);
        assertEquals(0, c.getEnergiaActual());
    }

    // 12
    @Test
    void curarNoSuperaLaVidaMaxima() {
        c.recibirDanio(10);

        c.curar(1000,c);

        assertEquals(c.getVidaMaxima(), c.getVidaActual());
    }

    // 13
    @Test
    void laTecnicaOfensivaDaniaAlObjetivoYNoAlEjecutor() {
        int vidaRival = rival.getVidaActual();
        int vidaPropia = c.getVidaActual();

        corte.ejecutar(c, rival);

        assertTrue(rival.getVidaActual() < vidaRival);
        assertEquals(vidaPropia, c.getVidaActual());
    }

    // 14
    @Test
    void laTecnicaDeRecuperacionCuraAlEjecutorSinPasarElMaximo() {
        c.recibirDanio(50);
        int vidaHerida = c.getVidaActual();

        meditacion.ejecutar(c, c);
        assertTrue(c.getVidaActual() > vidaHerida);

        for (int i = 0; i < 10; i++) {
            meditacion.ejecutar(c, c);
        }
        assertEquals(c.getVidaMaxima(), c.getVidaActual());
    }

    // 15
    @Test
    void laTecnicaDefensivaActivaLaDefensaYReduceElDanioRecibido() {
        c.prepararParaCombate();
        int vida = c.getVidaActual();
        c.recibirDanio(20);
        int perdidaSinMuro = vida - c.getVidaActual();

        c.prepararParaCombate();
        assertFalse(c.tieneDefensaActiva());
        muro.ejecutar(c, c);
        assertTrue(c.tieneDefensaActiva());
        c.recibirDanio(20);
        int perdidaConMuro = vida - c.getVidaActual();

        assertTrue(perdidaConMuro < perdidaSinMuro);
    }

    // 16
    @Test
    void dominioDeLaNadaDaniaYQuitaEnergiaAlObjetivo() {
        int vidaRival = rival.getVidaActual();
        int energiaRival = rival.getEnergiaActual();

        dominio.ejecutar(c, rival);

        assertTrue(rival.getVidaActual() < vidaRival);
        assertTrue(rival.getEnergiaActual() < energiaRival);
    }

    // 17
    @Test
    void dosTecnicasConElMismoNombreSonIguales() {
        Tecnica otraInstancia = new CorteDeLosMilAmaneceres();

        assertEquals(corte, otraInstancia);
        assertEquals(corte.hashCode(), otraInstancia.hashCode());
        assertNotEquals(corte, muro);
    }
    
    
    @Test
    void todasLasTecnicasTienenUnNombreDistinto() {
        List<Tecnica> todas = List.of(
                new CorteDeLosMilAmaneceres(), new RugidoDeLaBestiaPrimordial(),
                new DominioDeLaNada(), new MuroDelCuerpoDorado(), new MeditacionDelInmortal(),
                new NieblaVenenosaDelVacio(), new LotoRegenerador());

        Set<Tecnica> distintas = new HashSet<>(todas);

        assertEquals(todas.size(), distintas.size());
    }

    @Test
    void elPalacioDelVacioUsaLaNieblaEnSuPrimeraAccionYEnvenenaAlRival() {
        Emisor emisor = new Emisor();
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        emisor.suscribir(cronista);
        Escuela palacio = new Escuela("Palacio del Vacío",
                List.of(new NieblaVenenosaDelVacio(), new DominioDeLaNada(), new MeditacionDelInmortal()),
                List.of(new AnilloEspiritual()));
        CreadorCombatientes creador = new CreadorCombatientes();
        Combatiente nebula = creador.crear(TipoCombatiente.HECHICERO_VACIO, "Nébula", palacio);
        Combatiente selene = creador.crear(TipoCombatiente.MONJE_CUERPO_CELESTIAL, "Selene",
                new Escuela("Sin escuela", List.of(), List.of()));
        Combatiente rival1 = creador.crear(TipoCombatiente.CULTIVADOR_ESPADA, "Aurelio",
                new Escuela("Sin escuela", List.of(), List.of()));
        Combatiente rival2 = creador.crear(TipoCombatiente.CULTIVADOR_ESPADA, "Darian",
                new Escuela("Sin escuela", List.of(), List.of()));

        new Combate(new Pareja(1, nebula, selene), new Pareja(2, rival1, rival2), 1, emisor).jugar();

        AccionRealizada primera = cronista.acontecimientosDe(AccionRealizada.class).stream()
                .filter(a -> a.idEjecutor() == nebula.getId()).findFirst().orElseThrow();
        assertEquals(new NieblaVenenosaDelVacio().getNombre(), primera.accion());
        assertTrue(cronista.acontecimientosDe(EfectoAplicado.class).stream()
                .anyMatch(e -> e.efecto().equals(new Veneno().getNombre())));
    }
    
    
    
    
    
}
