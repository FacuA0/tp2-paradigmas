package TorneoInmortales;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HeraldoImperialTest {

    private ByteArrayOutputStream buffer;
    private HeraldoImperial heraldo;

    @BeforeEach
    void preparar() {
        buffer = new ByteArrayOutputStream();
        heraldo = new HeraldoImperial(new PrintStream(buffer, true, StandardCharsets.UTF_8));
    }

    private List<String> lineas() {
        String texto = buffer.toString(StandardCharsets.UTF_8);
        return texto.isEmpty() ? List.of() : Arrays.asList(texto.split("\\R"));
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

    // 1
    @Test
    void presentaCadaAcontecimientoComoUnaLineaEnOrden() {
        Acontecimiento e1 = new Eliminacion(1, 3, "Kael");
        Acontecimiento e2 = new Proclamacion(1, "Aurelio", "Nébula");

        heraldo.alOcurrir(e1);
        heraldo.alOcurrir(e2);

        assertEquals(List.of(e1.descripcion(), e2.descripcion()), lineas());
    }

    // 2
    @Test
    void anunciarEscribeElMensajeTalCual() {
        heraldo.anunciar("Comienza el Torneo de los Inmortales");

        assertEquals(List.of("Comienza el Torneo de los Inmortales"), lineas());
    }

    // 3
    @Test
    void presentaLaTablaConEncabezadoYUnaLineaPorFila() {
        heraldo.presentarTabla(List.of(
                new FilaDePosicion(1, "Aurelio y Nébula", 3, 120),
                new FilaDePosicion(2, "Kael y Darian", 3, 0)));

        List<String> lineas = lineas();

        assertEquals(3, lineas.size());
        assertTrue(lineas.get(1).startsWith("1."));
        assertTrue(lineas.get(1).contains("Pareja 1"));
        assertTrue(lineas.get(1).contains("Aurelio y Nébula"));
        assertTrue(lineas.get(1).contains("120"));
        assertTrue(lineas.get(2).startsWith("2."));
        assertTrue(lineas.get(2).contains("Kael y Darian"));
    }

    // 4
    @Test
    void unaTablaVaciaSePresentaSinFallar() {
        assertDoesNotThrow(() -> heraldo.presentarTabla(List.of()));

        assertEquals(2, lineas().size());
    }

    // 5
    @Test
    void rechazaNulos() {
        assertThrows(NullPointerException.class, () -> new HeraldoImperial(null));
        assertThrows(NullPointerException.class, () -> heraldo.alOcurrir(null));
        assertThrows(NullPointerException.class, () -> heraldo.anunciar(null));
        assertThrows(NullPointerException.class, () -> heraldo.presentarTabla(null));
    }

    // 6
    @Test
    void presentaEnteroUnTorneoCompleto() {
        Torneo torneo = new Torneo();
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        torneo.suscribir(heraldo);
        torneo.suscribir(cronista);
        for (int id = 1; id <= 4; id++) {
            torneo.inscribir(pareja(id));
        }

        torneo.iniciar();

        List<String> esperado = cronista.getHistorial().stream().map(Acontecimiento::descripcion).toList();
        assertFalse(esperado.isEmpty());
        assertEquals(esperado, lineas());
    }
}