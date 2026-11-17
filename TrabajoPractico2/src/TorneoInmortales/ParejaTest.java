package TorneoInmortales;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParejaTest {

    private Combatiente a, b, otro;
    private Tecnica corte, rugido;
    private Pareja pareja;

    @BeforeEach
    void preparar() {
        corte = new CorteDeLosMilAmaneceres();
        rugido = new RugidoDeLaBestiaPrimordial();
        a = new CultivadorDeLaEspada(1, "Aurelio");
        b = new HechiceroDelVacio(2, "Nébula");
        otro = new MonjeCuerpoCelestial(3, "Kael");
        for (Combatiente c : List.of(a, b, otro)) {
            c.aprenderTecnica(corte);
            c.aprenderTecnica(rugido);
        }
        pareja = new Pareja(1, a, b);
    }

    // 1
    @Test
    void losIntegrantesQuedanOrdenadosPorIdSinImportarElOrdenDelConstructor() {
        Pareja alReves = new Pareja(7, b, a);

        assertEquals(List.of(a, b), alReves.getIntegrantes());
        assertEquals(7, alReves.getId());
    }

    // 2
    @Test
    void rechazaElMismoCombatienteDosVecesYLosNulos() {
        assertThrows(IllegalArgumentException.class, () -> new Pareja(9, a, a));
        assertThrows(NullPointerException.class, () -> new Pareja(9, a, null));
        assertThrows(NullPointerException.class, () -> new Pareja(9, null, b));
    }

    // 3
    @Test
    void contieneYCompaneroDe() {
        assertTrue(pareja.contiene(a));
        assertFalse(pareja.contiene(otro));
        assertEquals(b, pareja.companeroDe(a));
        assertEquals(a, pareja.companeroDe(b));
        assertThrows(IllegalArgumentException.class, () -> pareja.companeroDe(otro));
    }

    // 4
    @Test
    void unCaidoSaleDeLosIntegrantesEnPieYLaParejaSigueConUnoSolo() {
        a.recibirDanio(10_000);

        assertEquals(List.of(b), pareja.integrantesEnPie());
        assertTrue(pareja.sigueEnPie());

        b.recibirDanio(10_000);

        assertTrue(pareja.integrantesEnPie().isEmpty());
        assertFalse(pareja.sigueEnPie());
    }

    // 5
    @Test
    void laVidaTotalSumaLaVidaActualYUnCaidoSumaCero() {
        assertEquals(a.getVidaActual() + b.getVidaActual(), pareja.vidaTotal());

        a.recibirDanio(10_000);

        assertEquals(b.getVidaActual(), pareja.vidaTotal());
    }

    // 6
    @Test
    void dosParejasConElMismoIdSonLaMisma() {
        Combatiente c3 = new MonjeCuerpoCelestial(4, "X");
        Combatiente c4 = new MonjeCuerpoCelestial(5, "Y");

        assertEquals(pareja, new Pareja(1, c3, c4));
        assertEquals(pareja.hashCode(), new Pareja(1, c3, c4).hashCode());
        assertNotEquals(pareja, new Pareja(2, a, b));
    }

    // 7
    @Test
    void elCompaneroNoPuedeUsarLaTecnicaMientrasElPrimeroConserveUsos() {
        pareja.usarTecnica(a, corte);

        assertFalse(pareja.puedeUsar(b, corte));
        assertThrows(TecnicaNoDisponibleException.class, () -> pareja.usarTecnica(b, corte));
    }

    // 8
    @Test
    void elPrimeroSiPuedeVolverAUsarla() {
        pareja.usarTecnica(a, corte);

        assertTrue(pareja.puedeUsar(a, corte));
        pareja.usarTecnica(a, corte);
        assertEquals(3, a.usosRestantes(corte));
    }

    // 9
    @Test
    void siLaRechazaNoCambiaNada() {
        pareja.usarTecnica(a, corte);
        int energia = b.getEnergiaActual();
        int usos = b.usosRestantes(corte);

        assertThrows(TecnicaNoDisponibleException.class, () -> pareja.usarTecnica(b, corte));

        assertEquals(energia, b.getEnergiaActual());
        assertEquals(usos, b.usosRestantes(corte));
    }

    // 10
    @Test
    void elCompaneroPuedeUsarlaSiElPrimeroAgotaSusUsos() {
        for (int i = 0; i < 5; i++) {
            a.prepararParaCombate(); 
            pareja.usarTecnica(a, corte);
        }
        assertEquals(0, a.usosRestantes(corte));

        assertTrue(pareja.puedeUsar(b, corte));
        pareja.usarTecnica(b, corte);
        assertEquals(4, b.usosRestantes(corte));
    }

    // 11
    @Test
    void elCompaneroPuedeUsarlaSiElPrimeroQuedaFueraDeCombate() {
        pareja.usarTecnica(a, corte);
        a.recibirDanio(10_000);

        assertTrue(pareja.puedeUsar(b, corte));
    }

    // 12
    @Test
    void laRestriccionTerminaConElCombate() {
        pareja.usarTecnica(a, corte);
        assertFalse(pareja.puedeUsar(b, corte));

        pareja.prepararParaCombate();

        assertTrue(pareja.puedeUsar(b, corte));
        assertTrue(pareja.puedeUsar(a, corte));
    }

    // 13
    @Test
    void laRestriccionEsPorTecnica() {
        pareja.usarTecnica(a, corte);

        assertFalse(pareja.puedeUsar(b, corte));
        assertTrue(pareja.puedeUsar(b, rugido));
    }

    // 14
    @Test
    void funcionaEnAmbosSentidos() {
        pareja.usarTecnica(b, rugido);

        assertFalse(pareja.puedeUsar(a, rugido));
        assertTrue(pareja.puedeUsar(b, rugido));
    }

    // 15
    @Test
    void unAjenoNoPuedeUsarTecnicasPorLaPareja() {
        assertThrows(IllegalArgumentException.class, () -> pareja.usarTecnica(otro, corte));
        assertThrows(IllegalArgumentException.class, () -> pareja.puedeUsar(otro, corte));
        assertEquals(5, otro.usosRestantes(corte));
    }

    // 16
    @Test
    void prepararParaCombateLevantaALosCaidosYReponeEnergia() {
        pareja.usarTecnica(a, corte);
        a.recibirDanio(10_000);
        assertFalse(a.estaEnPie());

        pareja.prepararParaCombate();

        assertTrue(a.estaEnPie());
        assertEquals(a.getVidaMaxima(), a.getVidaActual());
        assertEquals(a.getEnergiaMaxima(), a.getEnergiaActual());
        assertEquals(b.getEnergiaMaxima(), b.getEnergiaActual());
    }

    // 17
    @Test
    void prepararParaCombateNoRestituyeLosUsos() {
        pareja.usarTecnica(a, corte);

        pareja.prepararParaCombate();

        assertEquals(4, a.usosRestantes(corte));
    }
}