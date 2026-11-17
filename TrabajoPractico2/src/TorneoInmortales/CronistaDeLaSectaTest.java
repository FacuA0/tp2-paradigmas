package TorneoInmortales;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CronistaDeLaSectaTest {

    private CronistaDeLaSecta cronista;

    @BeforeEach
    void preparar() {
        cronista = new CronistaDeLaSecta();
    }


    private ResultadoDeCombate resultado(int ronda, int idGanadora, int vidaGanadora,
                                         int idPerdedora, int vidaPerdedora) {
        return new ResultadoDeCombate(ronda, idGanadora, "P" + idGanadora, vidaGanadora,
                idPerdedora, "P" + idPerdedora, vidaPerdedora);
    }

    private List<Integer> orden() {
        return cronista.tablaDePosiciones().stream().map(FilaDePosicion::idPareja).toList();
    }

    private Pareja pareja(int id) {
        Combatiente a = new CultivadorDeLaEspada(2 * id - 1, "C" + (2 * id - 1));
        Combatiente b = new MonjeCuerpoCelestial(2 * id, "M" + (2 * id));
        for (Combatiente c : List.of(a, b)) {
            c.aprenderTecnica(new CorteDeLosMilAmaneceres());
            c.aprenderTecnica(new MeditacionDelInmortal());
        }
        return new Pareja(id, a, b);
    }

    private Torneo torneoDeOcho(CronistaDeLaSecta c) {
        Torneo t = new Torneo();
        t.suscribir(c);
        for (int id = 1; id <= 8; id++) {
            t.inscribir(pareja(id));
        }
        t.iniciar();
        return t;
    }

    // 1
    @Test
    void registraLosAcontecimientosEnOrden() {
        Acontecimiento e1 = new Eliminacion(1, 3, "Kael");
        Acontecimiento e2 = resultado(1, 1, 100, 2, 0);
        Acontecimiento e3 = new Proclamacion(1, "Aurelio", "Nébula");

        cronista.alOcurrir(e1);
        cronista.alOcurrir(e2);
        cronista.alOcurrir(e3);

        assertEquals(List.of(e1, e2, e3), cronista.getHistorial());
    }

    // 2
    @Test
    void elHistorialDevueltoEsUnaCopiaDeSoloLectura() {
        cronista.alOcurrir(new Eliminacion(1, 3, "Kael"));
        List<Acontecimiento> antes = cronista.getHistorial();

        assertThrows(UnsupportedOperationException.class, () -> antes.add(new Eliminacion(1, 4, "X")));

        cronista.alOcurrir(new Eliminacion(1, 5, "Y"));
        assertEquals(1, antes.size());
        assertEquals(2, cronista.getHistorial().size());
    }

    // 3
    @Test
    void sePuedeConsultarElHistorialPorTipo() {
        cronista.alOcurrir(new Eliminacion(1, 3, "Kael"));
        cronista.alOcurrir(resultado(1, 1, 100, 2, 0));
        cronista.alOcurrir(new Eliminacion(1, 4, "Darian"));

        assertEquals(2, cronista.acontecimientosDe(Eliminacion.class).size());
        assertEquals(1, cronista.acontecimientosDe(ResultadoDeCombate.class).size());
        assertTrue(cronista.acontecimientosDe(Proclamacion.class).isEmpty());
    }

    // 4
    @Test
    void armaLaTablaConLaUltimaRondaYLaVidaDelUltimoCombate() {
        cronista.alOcurrir(resultado(1, 1, 150, 2, 0));
        cronista.alOcurrir(resultado(1, 4, 120, 3, 0));
        cronista.alOcurrir(resultado(2, 4, 90, 1, 0));

        List<FilaDePosicion> tabla = cronista.tablaDePosiciones();

        assertEquals(List.of(4, 1, 2, 3), orden());
        FilaDePosicion pareja1 = tabla.stream().filter(f -> f.idPareja() == 1).findFirst().orElseThrow();
        assertEquals(2, pareja1.ultimaRonda());
        assertEquals(0, pareja1.vidaTotalFinal());
        assertEquals(4, tabla.size());
    }

    // 5
    @Test
    void enLaMismaRondaVaPrimeroLaDeMasVidaTotal() {
        cronista.alOcurrir(resultado(1, 7, 80, 5, 0));
        cronista.alOcurrir(resultado(1, 3, 120, 2, 0));

        assertEquals(List.of(3, 7), orden().subList(0, 2));
    }

    // 6
    @Test
    void conIgualVidaTotalVaPrimeroLaDeMenorId() {
        cronista.alOcurrir(resultado(1, 6, 50, 9, 50));

        assertEquals(List.of(6, 9), orden());
    }

    // 7
    @Test
    void laUltimaRondaPesaMasQueLaVidaTotal() {
        cronista.alOcurrir(resultado(1, 8, 500, 7, 0));
        cronista.alOcurrir(resultado(2, 1, 10, 2, 0));

        assertEquals(List.of(1, 2, 8, 7), orden());
    }

    // 8
    @Test
    void sinResultadosLaTablaEstaVacia() {
        assertTrue(cronista.tablaDePosiciones().isEmpty());
    }

    // 9
    @Test
    void losAcontecimientosQueNoSonResultadosSeRegistranPeroNoArmanFilas() {
        cronista.alOcurrir(new Eliminacion(1, 3, "Kael"));
        cronista.alOcurrir(new Proclamacion(1, "Aurelio", "Nébula"));

        assertEquals(2, cronista.getHistorial().size());
        assertTrue(cronista.tablaDePosiciones().isEmpty());
    }

    // 10
    @Test
    void conUnTorneoRealLaTablaTieneUnaFilaPorParejaYEstaOrdenada() {
        Torneo torneo = torneoDeOcho(cronista);
        List<FilaDePosicion> tabla = cronista.tablaDePosiciones();

        assertEquals(8, tabla.size());
        assertEquals(8L, tabla.stream().map(FilaDePosicion::idPareja).distinct().count());

        assertEquals(torneo.getCampeona().getId(), tabla.get(0).idPareja());
        assertEquals(torneo.getCampeona().vidaTotal(), tabla.get(0).vidaTotalFinal());

        assertEquals(4L, tabla.stream().filter(f -> f.ultimaRonda() == 1).count());
        assertEquals(2L, tabla.stream().filter(f -> f.ultimaRonda() == 2).count());
        assertEquals(2L, tabla.stream().filter(f -> f.ultimaRonda() == 3).count());

        for (FilaDePosicion fila : tabla) {
            int ultima = 0;
            for (int r = 0; r < torneo.getRondas().size(); r++) {
                for (Torneo.Cruce cruce : torneo.getRondas().get(r)) {
                    if (cruce.primera().getId() == fila.idPareja() || cruce.segunda().getId() == fila.idPareja()) {
                        ultima = r + 1;
                    }
                }
            }
            assertEquals(ultima, fila.ultimaRonda(), "pareja " + fila.idPareja());
        }

        for (int i = 1; i < tabla.size(); i++) {
            FilaDePosicion ant = tabla.get(i - 1), act = tabla.get(i);
            boolean ordenada = ant.ultimaRonda() > act.ultimaRonda()
                    || (ant.ultimaRonda() == act.ultimaRonda() && ant.vidaTotalFinal() > act.vidaTotalFinal())
                    || (ant.ultimaRonda() == act.ultimaRonda() && ant.vidaTotalFinal() == act.vidaTotalFinal()
                            && ant.idPareja() < act.idPareja());
            assertTrue(ordenada, "filas " + (i - 1) + " y " + i + " desordenadas");
        }
    }

    // 11
    @Test
    void dosTorneosArmadosIgualDanElMismoHistorialYLaMismaTabla() {
        CronistaDeLaSecta otro = new CronistaDeLaSecta();
        torneoDeOcho(cronista);
        torneoDeOcho(otro);

        assertFalse(cronista.getHistorial().isEmpty());
        assertEquals(cronista.getHistorial(), otro.getHistorial());
        assertEquals(cronista.tablaDePosiciones(), otro.tablaDePosiciones());
    }
}