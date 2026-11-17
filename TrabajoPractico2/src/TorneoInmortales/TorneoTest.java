package TorneoInmortales;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TorneoTest {

    private List<Acontecimiento> historial;
    private List<Combatiente> todos;
    private Tecnica corte;

    private record Corrida(int idCampeona, List<Acontecimiento> historial) {}

    @BeforeEach
    void preparar() {
        historial = new ArrayList<>();
        todos = new ArrayList<>();
        corte = new CorteDeLosMilAmaneceres();
    }

    private Combatiente nuevo(int id) {
        Combatiente c = (id % 2 == 0) ? new MonjeCuerpoCelestial(id, "M" + id)
                                      : new CultivadorDeLaEspada(id, "C" + id);
        c.aprenderTecnica(new CorteDeLosMilAmaneceres());
        c.aprenderTecnica(new MeditacionDelInmortal());
        todos.add(c);
        return c;
    }


    private Pareja pareja(int id) {
        return new Pareja(id, nuevo(2 * id - 1), nuevo(2 * id));
    }

    private Torneo torneoCon(int... idsDeParejas) {
        Torneo t = new Torneo();
        t.suscribir(historial::add);
        for (int id : idsDeParejas) {
            t.inscribir(pareja(id));
        }
        return t;
    }

    private List<Integer> ids(List<Pareja> parejas) {
        return parejas.stream().map(Pareja::getId).toList();
    }

    private <T extends Acontecimiento> List<T> eventos(Class<T> tipo) {
        return historial.stream().filter(tipo::isInstance).map(tipo::cast).toList();
    }

    private Corrida correr(int... idsDeParejas) {
        List<Acontecimiento> h = new ArrayList<>();
        Torneo t = new Torneo();
        t.suscribir(h::add);
        for (int id : idsDeParejas) {
            t.inscribir(pareja(id));
        }
        t.iniciar();
        return new Corrida(t.getCampeona().getId(), h);
    }

    // 1
    @Test
    void laInscripcionConservaElOrden() {
        Torneo t = torneoCon(4, 2, 3, 1);

        assertEquals(List.of(4, 2, 3, 1), ids(t.getInscriptas()));
    }

    // 2
    @Test
    void unaParejaRepetidaSeRechazaSinCambios() {
        Torneo t = new Torneo();
        Pareja p = pareja(1);
        t.inscribir(p);
        Pareja mismoId = new Pareja(1, nuevo(101), nuevo(102));

        assertThrows(TorneoInvalidoException.class, () -> t.inscribir(p));
        assertThrows(TorneoInvalidoException.class, () -> t.inscribir(mismoId));

        assertEquals(List.of(p), t.getInscriptas());
    }

    // 3
    @Test
    void unCombatienteNoSePuedeInscribirDosVecesNiQuedaRegistradoAMedias() {
        Torneo t = new Torneo();
        Combatiente c1 = nuevo(1), c2 = nuevo(2), c3 = nuevo(3), c4 = nuevo(4);
        t.inscribir(new Pareja(1, c1, c2));

        assertThrows(TorneoInvalidoException.class, () -> t.inscribir(new Pareja(2, c2, c3)));

        assertEquals(1, t.getInscriptas().size());

        assertDoesNotThrow(() -> t.inscribir(new Pareja(3, c3, c4)));
    }

    // 4
    @Test
    void unaCantidadInvalidaDeParejasSeRechazaSinPublicarNada() {
        for (int cantidad : List.of(0, 1, 2, 3, 5, 6, 7, 9, 10)) {
            historial.clear();
            Torneo t = new Torneo();
            t.suscribir(historial::add);
            for (int i = 1; i <= cantidad; i++) {
                t.inscribir(pareja(i + 100 * cantidad));
            }

            assertThrows(TorneoInvalidoException.class, t::iniciar, "cantidad " + cantidad);

            assertTrue(historial.isEmpty(), "cantidad " + cantidad);
            assertNull(t.getCampeona());
        }
    }

    // 5
    @Test
    void trasUnRechazoSePuedeCompletarLaInscripcionYJugar() {
        Torneo t = torneoCon(1, 2, 3);
        assertThrows(TorneoInvalidoException.class, t::iniciar);

        t.inscribir(pareja(4));
        t.iniciar();

        assertNotNull(t.getCampeona());
    }

    // 6
    @Test
    void conCuatroOOchoParejasSeJuegaYHayCampeona() {
        Torneo cuatro = torneoCon(1, 2, 3, 4);
        cuatro.iniciar();
        assertNotNull(cuatro.getCampeona());
        assertTrue(cuatro.getInscriptas().contains(cuatro.getCampeona()));

        Torneo ocho = torneoCon(11, 12, 13, 14, 15, 16, 17, 18);
        ocho.iniciar();
        assertNotNull(ocho.getCampeona());
        assertTrue(ocho.getInscriptas().contains(ocho.getCampeona()));
    }

    // 7
    @Test
    void laFormaDelTorneoEsEliminatoria() {
        Torneo cuatro = torneoCon(1, 2, 3, 4);
        cuatro.iniciar();
        assertEquals(List.of(2, 1), cuatro.getRondas().stream().map(List::size).toList());

        Torneo ocho = torneoCon(11, 12, 13, 14, 15, 16, 17, 18);
        ocho.iniciar();
        assertEquals(List.of(4, 2, 1), ocho.getRondas().stream().map(List::size).toList());
    }

    // 8
    @Test
    void laPrimeraRondaSeArmaPorOrdenDeInscripcion() {
        Torneo t = torneoCon(7, 3, 9, 1);
        t.iniciar();

        List<Torneo.Cruce> primera = t.getRondas().get(0);

        assertEquals(7, primera.get(0).primera().getId());
        assertEquals(3, primera.get(0).segunda().getId());
        assertEquals(9, primera.get(1).primera().getId());
        assertEquals(1, primera.get(1).segunda().getId());
    }

    // 9
    @Test
    void lasRondasSiguientesSeArmanPorElOrdenDeLosCrucesAnteriores() {
        Torneo t = torneoCon(1, 2, 3, 4, 5, 6, 7, 8);
        t.iniciar();
        List<List<Torneo.Cruce>> rondas = t.getRondas();

        for (int r = 1; r < rondas.size(); r++) {
            List<Torneo.Cruce> anterior = rondas.get(r - 1);
            List<Torneo.Cruce> actual = rondas.get(r);
            for (int i = 0; i < actual.size(); i++) {
                assertEquals(anterior.get(2 * i).ganadora(), actual.get(i).primera());
                assertEquals(anterior.get(2 * i + 1).ganadora(), actual.get(i).segunda());
            }
        }
    }

    // 10
    @Test
    void unaParejaQuePierdeNoVuelveAJugar() {
        Torneo t = torneoCon(1, 2, 3, 4, 5, 6, 7, 8);
        t.iniciar();
        List<List<Torneo.Cruce>> rondas = t.getRondas();

        for (int r = 0; r < rondas.size(); r++) {
            for (Torneo.Cruce cruce : rondas.get(r)) {
                Pareja perdedora = cruce.ganadora().equals(cruce.primera()) ? cruce.segunda() : cruce.primera();
                for (int r2 = r + 1; r2 < rondas.size(); r2++) {
                    for (Torneo.Cruce posterior : rondas.get(r2)) {
                        assertNotEquals(perdedora, posterior.primera());
                        assertNotEquals(perdedora, posterior.segunda());
                    }
                }
            }
        }
    }

    // 11
    @Test
    void terminaConUnaSolaParejaQueNuncaPerdio() {
        Torneo t = torneoCon(1, 2, 3, 4, 5, 6, 7, 8);
        t.iniciar();
        List<List<Torneo.Cruce>> rondas = t.getRondas();
        List<Torneo.Cruce> ultima = rondas.get(rondas.size() - 1);

        assertEquals(1, ultima.size());
        assertEquals(t.getCampeona(), ultima.get(0).ganadora());

        for (List<Torneo.Cruce> ronda : rondas) {
            for (Torneo.Cruce cruce : ronda) {
                if (cruce.primera().equals(t.getCampeona()) || cruce.segunda().equals(t.getCampeona())) {
                    assertEquals(t.getCampeona(), cruce.ganadora());
                }
            }
        }
    }

    // 12
    @Test
    void seProclamaALaCampeonaAlFinalUnaSolaVez() {
        Torneo t = torneoCon(1, 2, 3, 4);
        t.iniciar();

        Proclamacion p = assertInstanceOf(Proclamacion.class, historial.get(historial.size() - 1));
        List<Combatiente> santos = t.getCampeona().getIntegrantes();

        assertEquals(1, eventos(Proclamacion.class).size());
        assertEquals(t.getCampeona().getId(), p.idPareja());
        assertEquals(santos.get(0).getNombre(), p.primerSanto());
        assertEquals(santos.get(1).getNombre(), p.segundoSanto());
    }

    // 13
    @Test
    void haceUnInicioYUnResultadoPorCadaCruceConSuRonda() {
        Torneo cuatro = torneoCon(1, 2, 3, 4);
        cuatro.iniciar();
        assertEquals(List.of(1, 1, 2), eventos(InicioDeCombate.class).stream().map(InicioDeCombate::ronda).toList());
        assertEquals(3, eventos(ResultadoDeCombate.class).size());

        historial.clear();
        Torneo ocho = torneoCon(11, 12, 13, 14, 15, 16, 17, 18);
        ocho.iniciar();
        assertEquals(List.of(1, 1, 1, 1, 2, 2, 3), eventos(InicioDeCombate.class).stream().map(InicioDeCombate::ronda).toList());
        assertEquals(7, eventos(ResultadoDeCombate.class).size());
    }

    // 14
    @Test
    void conElTorneoIniciadoNoSeInscribeNiSeInicia() {
        Torneo t = torneoCon(1, 2, 3, 4);
        t.iniciar();
        int eventos = historial.size();

        assertThrows(IllegalStateException.class, () -> t.inscribir(pareja(5)));
        assertThrows(IllegalStateException.class, t::iniciar);

        assertEquals(eventos, historial.size());
        assertEquals(4, t.getInscriptas().size());
    }

    // 15
    @Test
    void dosInteresadosRecibenExactamenteLoMismo() {
        List<Acontecimiento> a = new ArrayList<>();
        List<Acontecimiento> b = new ArrayList<>();
        Torneo t = torneoCon(1, 2, 3, 4);
        t.suscribir(a::add);
        t.suscribir(b::add);

        t.iniciar();

        assertFalse(a.isEmpty());
        assertEquals(a, b);
        assertEquals(a, historial);
    }

    // 16
    @Test
    void losUsosDeLasTecnicasSeConservanALoLargoDelTorneo() {
        Torneo t = torneoCon(1, 2, 3, 4, 5, 6, 7, 8);
        t.iniciar();

        boolean alguienUsoTecnicas = false;
        for (Combatiente c : todos) {
            long usadas = eventos(AccionRealizada.class).stream()
                    .filter(a -> a.idEjecutor() == c.getId() && a.esTecnica()
                            && a.accion().equals(corte.getNombre()))
                    .count();
            assertEquals(5 - c.usosRestantes(corte), usadas, "combatiente " + c.getId());
            assertTrue(c.usosRestantes(corte) >= 0);
            alguienUsoTecnicas |= usadas > 0;
        }
        assertTrue(alguienUsoTecnicas);
    }

    // 17
    @Test
    void dosTorneosArmadosIgualProducenElMismoCampeonYElMismoHistorial() {
        Corrida primera = correr(1, 2, 3, 4, 5, 6, 7, 8);
        Corrida segunda = correr(1, 2, 3, 4, 5, 6, 7, 8);

        assertFalse(primera.historial().isEmpty());
        assertEquals(primera.idCampeona(), segunda.idCampeona());
        assertEquals(primera.historial(), segunda.historial());
    }

    // 18
    @Test
    void rechazaNulosYLasListasDevueltasSonDeSoloLectura() {
        Torneo t = torneoCon(1, 2, 3, 4);

        assertThrows(NullPointerException.class, () -> t.inscribir(null));
        assertThrows(NullPointerException.class, () -> t.suscribir(null));

        t.iniciar();
        assertThrows(UnsupportedOperationException.class, () -> t.getInscriptas().clear());
        assertThrows(UnsupportedOperationException.class, () -> t.getRondas().clear());
    }
}