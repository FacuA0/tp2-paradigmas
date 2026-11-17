package TorneoInmortales;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CombateTest {

    private Emisor emisor;
    private List<Acontecimiento> historial;
    private Tecnica corte, rugido, muro, meditacion;
    private Combatiente c;
    @BeforeEach
    void preparar() {
        emisor = new Emisor();
        historial = new ArrayList<>();
        emisor.suscribir(historial::add);
        corte = new CorteDeLosMilAmaneceres();
        rugido = new RugidoDeLaBestiaPrimordial();
        muro = new MuroDelCuerpoDorado();
        meditacion = new MeditacionDelInmortal();
        c = new CultivadorDeLaEspada(1, "Aurelio"); 
    }


    private Combatiente cultivador(int id) { return new CultivadorDeLaEspada(id, "C" + id); }

    private Combatiente monje(int id) { return new MonjeCuerpoCelestial(id, "M" + id); }

    private List<AccionRealizada> acciones() {
        return historial.stream()
                .filter(AccionRealizada.class::isInstance)
                .map(AccionRealizada.class::cast)
                .toList();
    }

    private List<AccionRealizada> accionesDe(int idEjecutor) {
        return acciones().stream().filter(a -> a.idEjecutor() == idEjecutor).toList();
    }


    private Combate combate(Combatiente c1, Combatiente c2, Combatiente c3, Combatiente c4) {
        return new Combate(new Pareja(1, c1, c2), new Pareja(2, c3, c4), 1, emisor);
    }

    // 1
    @Test
    void empiezaLaParejaDeMenorIdYDentroDeEllaElDeMenorId() {
        Pareja p1 = new Pareja(1, cultivador(4), cultivador(2));
        Pareja p2 = new Pareja(2, cultivador(3), cultivador(1));

        new Combate(p2, p1, 1, emisor).jugar();

        List<Integer> ejecutores = acciones().stream().limit(4).map(AccionRealizada::idEjecutor).toList();
        assertEquals(List.of(2, 4, 1, 3), ejecutores);
    }

    // 2
    @Test
    void cadaIntegranteEnPieRealizaUnaAccionPorAsalto() {
        combate(cultivador(1), cultivador(2), cultivador(3), cultivador(4)).jugar();

        List<AccionRealizada> primerAsalto = acciones().stream().filter(a -> a.asalto() == 1).toList();

        assertEquals(4, primerAsalto.size());
        assertEquals(4L, primerAsalto.stream().map(AccionRealizada::idEjecutor).distinct().count());
    }

    // 3
    @Test
    void publicaElInicioAlPrincipioYElResultadoAlFinal() {
        combate(cultivador(1), cultivador(2), cultivador(3), cultivador(4)).jugar();

        assertInstanceOf(InicioDeCombate.class, historial.get(0));
        assertInstanceOf(ResultadoDeCombate.class, historial.get(historial.size() - 1));
        assertEquals(1L, historial.stream().filter(ResultadoDeCombate.class::isInstance).count());
    }

    // 4
    @Test
    void unCaidoNoActuaNiEsElegidoComoObjetivo() {
        Combatiente c1 = cultivador(1), c2 = cultivador(2), c3 = cultivador(3), c4 = cultivador(4);
        combate(c1, c2, c3, c4).jugar();

        int eliminaciones = 0;
        for (int i = 0; i < historial.size(); i++) {
            if (historial.get(i) instanceof Eliminacion e) {
                eliminaciones++;
                for (int j = i + 1; j < historial.size(); j++) {
                    if (historial.get(j) instanceof AccionRealizada a) {
                        assertNotEquals(e.idCombatiente(), a.idEjecutor(), "un caído actuó");
                        assertNotEquals(e.idCombatiente(), a.idObjetivo(), "un caído fue objetivo");
                    }
                }
            }
        }
        long caidos = List.of(c1, c2, c3, c4).stream().filter(c -> !c.estaEnPie()).count();
        assertTrue(eliminaciones > 0);
        assertEquals(caidos, eliminaciones);
    }

    // 5
    @Test
    void elObjetivoEsElRivalEnPieConMenosVidaYEnEmpateElDeMenorId() {
        combate(cultivador(1), cultivador(2), cultivador(3), cultivador(4)).jugar();

        List<Integer> objetivos = acciones().stream().limit(4).map(AccionRealizada::idObjetivo).toList();

        assertEquals(List.of(3, 3, 1, 1), objetivos);
    }

    // 6
    @Test
    void laDefensivaSeUsaPrimeroYSeAplicaAlEjecutor() {
        Combatiente c1 = cultivador(1);
        c1.aprenderTecnica(muro);
        c1.aprenderTecnica(corte);
        combate(c1, cultivador(2), cultivador(3), cultivador(4)).jugar();

        List<AccionRealizada> deC1 = accionesDe(1);

        assertEquals(muro.getNombre(), deC1.get(0).accion());
        assertEquals(1, deC1.get(0).idObjetivo());
        assertEquals(corte.getNombre(), deC1.get(1).accion());
    }

    // 7
    @Test
    void laRecuperacionSeUsaCuandoLeFaltaVida() {
        Combatiente c1 = cultivador(1);
        c1.aprenderTecnica(corte);
        c1.aprenderTecnica(meditacion);
        combate(c1, cultivador(2), cultivador(3), cultivador(4)).jugar();

        List<AccionRealizada> deC1 = accionesDe(1);

        assertEquals(corte.getNombre(), deC1.get(0).accion());
        assertEquals(meditacion.getNombre(), deC1.get(1).accion());
        assertEquals(1, deC1.get(1).idObjetivo());
    }

    // 8
    @Test
    void laOfensivaElegidaEsLaDeMasUsosYEnEmpateLaPrimeraDeLaEscuela() {
        Combatiente c1 = cultivador(1);
        c1.aprenderTecnica(corte);
        c1.aprenderTecnica(rugido);
        combate(c1, cultivador(2), cultivador(3), cultivador(4)).jugar();

        List<String> primeras = accionesDe(1).stream().limit(3).map(AccionRealizada::accion).toList();

        assertEquals(List.of(corte.getNombre(), rugido.getNombre(), corte.getNombre()), primeras);
    }

    // 9
    @Test
    void sinUsosRestantesSeHaceAtaqueBasico() {
        Combatiente c1 = cultivador(1);
        c1.aprenderTecnica(corte);
        for (int i = 0; i < 5; i++) {
            c1.prepararParaCombate();
            c1.usarTecnica(corte);
        }
        assertEquals(0, c1.usosRestantes(corte));

        combate(c1, cultivador(2), cultivador(3), cultivador(4)).jugar();

        assertFalse(accionesDe(1).get(0).esTecnica());
    }

    // 10
    @Test
    void sinEnergiaSuficienteSeHaceAtaqueBasico() {
        Combatiente c1 = cultivador(1);
        c1.aprenderTecnica(rugido);
        new Combate(new Pareja(1, c1, cultivador(2)), new Pareja(2, cultivador(3), cultivador(4)),
                1, emisor, Combate.LIMITE_ASALTOS_POR_DEFECTO, 0).jugar();

        List<AccionRealizada> deC1 = accionesDe(1);

        assertTrue(deC1.get(0).esTecnica());
        assertFalse(deC1.get(1).esTecnica());
    }

    // 11
    @Test
    void alComenzarCadaAsaltoSeRecuperaEnergia() {
        Combatiente c1 = cultivador(1);
        c1.aprenderTecnica(rugido);
        combate(c1, cultivador(2), cultivador(3), cultivador(4)).jugar();

        List<AccionRealizada> deC1 = accionesDe(1);

        assertEquals(rugido.getNombre(), deC1.get(0).accion());
        assertEquals(rugido.getNombre(), deC1.get(1).accion());
    }

    // 12
    @Test
    void laReglaDelCompaneroSeAplicaEnElCombate() {
        Combatiente c1 = cultivador(1), c2 = cultivador(2);
        c1.aprenderTecnica(corte);
        c2.aprenderTecnica(corte);
        combate(c1, c2, cultivador(3), cultivador(4)).jugar();

        List<AccionRealizada> primerAsalto = acciones().stream().filter(a -> a.asalto() == 1).toList();

        assertTrue(primerAsalto.get(0).esTecnica());
        assertFalse(primerAsalto.get(1).esTecnica());
    }

    // 13
    @Test
    void elCombateTerminaCuandoUnaParejaQuedaSinIntegrantesEnPie() {
        Pareja p1 = new Pareja(1, cultivador(1), cultivador(2));
        Pareja p2 = new Pareja(2, cultivador(3), cultivador(4));
        Combate combate = new Combate(p1, p2, 1, emisor);

        Pareja ganadora = combate.jugar();
        Pareja perdedora = ganadora.equals(p1) ? p2 : p1;

        assertTrue(ganadora.sigueEnPie());
        assertFalse(perdedora.sigueEnPie());
        assertTrue(combate.getAsaltosJugados() < Combate.LIMITE_ASALTOS_POR_DEFECTO);

        int ultimaEliminacion = -1;
        for (int i = 0; i < historial.size(); i++) {
            if (historial.get(i) instanceof Eliminacion) ultimaEliminacion = i;
        }
        assertEquals(historial.size() - 2, ultimaEliminacion);
    }

    // 14
    @Test
    void alLlegarAlLimiteGanaLaParejaConMasVidaTotal() {
        Pareja pA = new Pareja(1, cultivador(1), cultivador(2));
        Pareja pB = new Pareja(2, monje(3), monje(4));

        Pareja ganadora = new Combate(pA, pB, 1, emisor, 1, Combate.ENERGIA_POR_ASALTO_POR_DEFECTO).jugar();

        assertEquals(pB, ganadora);
        assertTrue(pB.vidaTotal() > pA.vidaTotal());
    }

    // 15
    @Test
    void enEmpateDeVidaTotalAlLimiteGanaLaDeMenorId() {
        Pareja pA = new Pareja(1, cultivador(1), cultivador(2));
        Pareja pB = new Pareja(2, cultivador(3), cultivador(4));

        Pareja ganadora = new Combate(pB, pA, 1, emisor, 1, Combate.ENERGIA_POR_ASALTO_POR_DEFECTO).jugar();

        assertEquals(pA.vidaTotal(), pB.vidaTotal());
        assertEquals(pA, ganadora);
    }

    // 16
    @Test
    void elResultadoPublicadoCoincideConElEstadoFinal() {
        Pareja p1 = new Pareja(1, cultivador(1), cultivador(2));
        Pareja p2 = new Pareja(2, cultivador(3), cultivador(4));

        Pareja ganadora = new Combate(p1, p2, 3, emisor).jugar();
        Pareja perdedora = ganadora.equals(p1) ? p2 : p1;
        ResultadoDeCombate r = assertInstanceOf(ResultadoDeCombate.class, historial.get(historial.size() - 1));

        assertEquals(3, r.ronda());
        assertEquals(ganadora.getId(), r.idGanadora());
        assertEquals(ganadora.vidaTotal(), r.vidaTotalGanadora());
        assertEquals(perdedora.getId(), r.idPerdedora());
        assertEquals(perdedora.vidaTotal(), r.vidaTotalPerdedora());
    }

    // 17
    @Test
    void alComenzarElCombateTodosRecuperanVidaYEnergiaIncluidosLosCaidos() {
        Combatiente c1 = cultivador(1);
        c1.aprenderTecnica(rugido);
        c1.perderEnergia(1000);
        c1.recibirDanio(10_000);
        assertFalse(c1.estaEnPie());

        new Combate(new Pareja(1, c1, cultivador(2)), new Pareja(2, cultivador(3), cultivador(4)),
                1, emisor, 1, Combate.ENERGIA_POR_ASALTO_POR_DEFECTO).jugar();

        List<AccionRealizada> deC1 = accionesDe(1);
        assertFalse(deC1.isEmpty());
        assertEquals(rugido.getNombre(), deC1.get(0).accion());
    }

    // 18
    @Test
    void dosCombatesArmadosIgualProducenElMismoHistorial() {
        List<Acontecimiento> primero = jugarEscenarioCompleto();
        List<Acontecimiento> segundo = jugarEscenarioCompleto();

        assertFalse(primero.isEmpty());
        assertEquals(primero, segundo);
    }

    private List<Acontecimiento> jugarEscenarioCompleto() {
        Emisor e = new Emisor();
        List<Acontecimiento> h = new ArrayList<>();
        e.suscribir(h::add);
        Combatiente c1 = cultivador(1), c2 = monje(2), c3 = cultivador(3), c4 = monje(4);
        c1.aprenderTecnica(new CorteDeLosMilAmaneceres());
        c1.aprenderTecnica(new MeditacionDelInmortal());
        c2.aprenderTecnica(new MuroDelCuerpoDorado());
        c3.aprenderTecnica(new RugidoDeLaBestiaPrimordial());
        c4.aprenderTecnica(new CorteDeLosMilAmaneceres());
        new Combate(new Pareja(1, c1, c2), new Pareja(2, c3, c4), 1, e).jugar();
        return h;
    }

    // 19
    @Test
    void rechazaCombatesInvalidos() {
        Pareja p1 = new Pareja(1, cultivador(1), cultivador(2));
        Pareja p2 = new Pareja(2, cultivador(3), cultivador(4));
        Pareja comparteCombatiente = new Pareja(3, p1.getIntegrantes().get(0), cultivador(9));

        assertThrows(NullPointerException.class, () -> new Combate(null, p2, 1, emisor));
        assertThrows(NullPointerException.class, () -> new Combate(p1, p2, 1, null));
        assertThrows(IllegalArgumentException.class, () -> new Combate(p1, p1, 1, emisor));
        assertThrows(IllegalArgumentException.class, () -> new Combate(p1, comparteCombatiente, 1, emisor));
        assertThrows(IllegalArgumentException.class, () -> new Combate(p1, p2, 1, emisor, 0, 10));
        assertThrows(IllegalArgumentException.class, () -> new Combate(p1, p2, 1, emisor, 20, -1));
    }

    // 20
    @Test
    void unCombateSoloSePuedeJugarUnaVez() {
        Combate combate = combate(cultivador(1), cultivador(2), cultivador(3), cultivador(4));
        combate.jugar();
        int eventos = historial.size();

        assertThrows(IllegalStateException.class, combate::jugar);

        assertEquals(eventos, historial.size());
    }
    

    @Test
    void alLlegarACeroDeVidaQuedaFueraDeCombate() {
        c.recibirDanio(10_000);

        assertEquals(0, c.getVidaActual());
        assertFalse(c.estaEnPie());
    }

    @Test
    void unGolpeQueIgualaLaVidaRestanteLoDejaCaido() {
        c.recibirDanio(c.getVidaActual());

        assertEquals(0, c.getVidaActual());
        assertFalse(c.estaEnPie());
    }

    @Test
    void conVidaPositivaSigueEnPie() {
        c.recibirDanio(10);

        assertTrue(c.getVidaActual() > 0);
        assertTrue(c.estaEnPie());
    }

    @Test
    void unCaidoVuelveAEstarEnPieAlComenzarElSiguienteCombate() {
        c.recibirDanio(10_000);
        assertFalse(c.estaEnPie());

        c.prepararParaCombate();

        assertTrue(c.estaEnPie());
        assertEquals(c.getVidaMaxima(), c.getVidaActual());
    }
}