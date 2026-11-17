package TorneoInmortales;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CombateEfectosTest {

    private Emisor emisor;
    private List<Acontecimiento> historial;
    private Tecnica niebla, loto;

    
    private final EfectoTemporal maldicion = new EfectoTemporal("Maldición", 5) {
        @Override
        public void alComenzarTurno(Combatiente afectado) {
            afectado.perderVida(1000);
        }
    };
    private final Tecnica lanzarMaldicion = new Tecnica("Maldición Letal", 10, TipoTecnica.OFENSIVA) {
        @Override
        public void ejecutar(Combatiente ejecutor, Combatiente objetivo) {
            objetivo.aplicarEfecto(maldicion);
        }
    };

    @BeforeEach
    void preparar() {
        emisor = new Emisor();
        historial = new ArrayList<>();
        emisor.suscribir(historial::add);
        niebla = new NieblaVenenosaDelVacio();
        loto = new LotoRegenerador();
    }

    // ---------- auxiliares ----------

    private Combatiente cultivador(int id) { return new CultivadorDeLaEspada(id, "C" + id); }

    private <T extends Acontecimiento> List<T> eventos(Class<T> tipo) {
        return historial.stream().filter(tipo::isInstance).map(tipo::cast).toList();
    }

    private List<AccionRealizada> accionesDe(int idEjecutor) {
        return eventos(AccionRealizada.class).stream().filter(a -> a.idEjecutor() == idEjecutor).toList();
    }

    private Combate combate(Combatiente c1, Combatiente c2, Combatiente c3, Combatiente c4) {
        return new Combate(new Pareja(1, c1, c2), new Pareja(2, c3, c4), 1, emisor);
    }

    // 1
    @Test
    void elVenenoApareceDespuesDeLaTecnicaYSobreUnRival() {
        Combatiente c1 = cultivador(1);
        c1.aprenderTecnica(niebla);
        combate(c1, cultivador(2), cultivador(3), cultivador(4)).jugar();

        List<EfectoAplicado> efectos = eventos(EfectoAplicado.class);
        assertFalse(efectos.isEmpty());
        EfectoAplicado primero = efectos.get(0);
        assertEquals(new Veneno().getNombre(), primero.efecto());
        assertTrue(primero.idAfectado() == 3 || primero.idAfectado() == 4);

        int indiceTecnica = -1;
        for (int i = 0; i < historial.size(); i++) {
            if (historial.get(i) instanceof AccionRealizada a
                    && a.idEjecutor() == 1 && a.accion().equals(niebla.getNombre())) {
                indiceTecnica = i;
                break;
            }
        }
        assertTrue(indiceTecnica >= 0);
        assertTrue(indiceTecnica < historial.indexOf(primero));
    }

    // 2
    @Test
    void losEfectosActuanAlComenzarElTurnoAntesDeLaAccionDelAfectado() {
        Combatiente c1 = cultivador(1);
        c1.aprenderTecnica(niebla);
        combate(c1, cultivador(2), cultivador(3), cultivador(4)).jugar();

        int comprobados = 0;
        for (int i = 0; i < historial.size(); i++) {
            if (historial.get(i) instanceof EfectoAplicado e) {
                for (int j = 0; j < historial.size(); j++) {
                    if (historial.get(j) instanceof AccionRealizada a
                            && a.idEjecutor() == e.idAfectado() && a.asalto() == e.asalto()) {
                        assertTrue(i < j, "el efecto debe actuar antes que la acción del mismo turno");
                        comprobados++;
                    }
                }
            }
        }
        assertTrue(comprobados > 0);
    }

    // 3
    @Test
    void unEfectoPuedeDejarFueraDeCombateElAfectadoNoActuaYElCombateTermina() {
        Combatiente c1 = cultivador(1);
        c1.aprenderTecnica(lanzarMaldicion);
        Combatiente c3 = cultivador(3);
        Combatiente c4 = cultivador(4);
        Pareja p1 = new Pareja(1, c1, cultivador(2));
        Pareja p2 = new Pareja(2, c3, c4);
        Combate combate = new Combate(p1, p2, 1, emisor);

        Pareja ganadora = combate.jugar();


        assertFalse(c3.estaEnPie());
        assertTrue(accionesDe(3).isEmpty());
        EfectoAplicado efecto = eventos(EfectoAplicado.class).stream()
                .filter(e -> e.idAfectado() == 3).findFirst().orElseThrow();
        assertEquals(0, efecto.vidaAfectado());
        Eliminacion eliminacion = assertInstanceOf(Eliminacion.class, historial.get(historial.indexOf(efecto) + 1));
        assertEquals(3, eliminacion.idCombatiente());


        assertFalse(c4.estaEnPie());
        assertEquals(p1, ganadora);
        assertEquals(2, combate.getAsaltosJugados());
        int ultimaEliminacion = -1;
        for (int i = 0; i < historial.size(); i++) {
            if (historial.get(i) instanceof Eliminacion) ultimaEliminacion = i;
        }
        assertEquals(historial.size() - 2, ultimaEliminacion);
    }

    // 4
    @Test
    void losEfectosNoPasanDeUnCombateAlSiguiente() {
        Combatiente c1 = cultivador(1);
        c1.aplicarEfecto(new Veneno());
        Pareja p1 = new Pareja(1, c1, cultivador(2));
        Pareja p2 = new Pareja(2, cultivador(3), cultivador(4));

        new Combate(p1, p2, 1, emisor, 1, Combate.ENERGIA_POR_ASALTO_POR_DEFECTO).jugar();

        assertTrue(eventos(EfectoAplicado.class).isEmpty());
    }

    // 5
    @Test
    void laRegeneracionActuaEnLosTurnosSiguientesALaTecnica() {
        Combatiente c1 = cultivador(1);
        c1.aprenderTecnica(loto);
        combate(c1, cultivador(2), cultivador(3), cultivador(4)).jugar();

        int indiceLoto = -1;
        for (int i = 0; i < historial.size(); i++) {
            if (historial.get(i) instanceof AccionRealizada a
                    && a.idEjecutor() == 1 && a.accion().equals(loto.getNombre())) {
                indiceLoto = i;
                break;
            }
        }
        EfectoAplicado primero = eventos(EfectoAplicado.class).stream()
                .filter(e -> e.idAfectado() == 1).findFirst().orElseThrow();

        assertTrue(indiceLoto >= 0);
        assertEquals(new Regeneracion().getNombre(), primero.efecto());
        assertTrue(historial.indexOf(primero) > indiceLoto);
    }

    // 6
    @Test
    void dosCombatesArmadosIgualConEfectosProducenElMismoHistorial() {
        List<Acontecimiento> primero = jugarEscenarioConEfectos();
        List<Acontecimiento> segundo = jugarEscenarioConEfectos();

        assertTrue(primero.stream().anyMatch(EfectoAplicado.class::isInstance));
        assertEquals(primero, segundo);
    }

    private List<Acontecimiento> jugarEscenarioConEfectos() {
        Emisor e = new Emisor();
        List<Acontecimiento> h = new ArrayList<>();
        e.suscribir(h::add);
        Combatiente c1 = cultivador(1), c2 = new MonjeCuerpoCelestial(2, "M2");
        Combatiente c3 = new HechiceroDelVacio(3, "H3"), c4 = cultivador(4);
        c1.aprenderTecnica(new NieblaVenenosaDelVacio());
        c2.aprenderTecnica(new LotoRegenerador());
        c3.aprenderTecnica(new NieblaVenenosaDelVacio());
        c3.aprenderTecnica(new MeditacionDelInmortal());
        c4.aprenderTecnica(new LotoRegenerador());
        new Combate(new Pareja(1, c1, c2), new Pareja(2, c3, c4), 1, e).jugar();
        return h;
    }
}