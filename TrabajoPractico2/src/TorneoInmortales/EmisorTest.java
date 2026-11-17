package TorneoInmortales;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EmisorTest {

    private Emisor emisor;
    private List<Acontecimiento> recibidosPorA, recibidosPorB;
    private Acontecimiento e1, e2;

    @BeforeEach
    void preparar() {
        emisor = new Emisor();
        recibidosPorA = new ArrayList<>();
        recibidosPorB = new ArrayList<>();
        e1 = new Eliminacion(1, 3, "Kael");
        e2 = new Proclamacion(2, "Aurelio", "Nébula");
    }

    // 1
    @Test
    void todosLosSuscriptosRecibenCadaAcontecimientoEnOrden() {
        emisor.suscribir(recibidosPorA::add);
        emisor.suscribir(recibidosPorB::add);

        emisor.publicar(e1);
        emisor.publicar(e2);

        assertEquals(List.of(e1, e2), recibidosPorA);
        assertEquals(List.of(e1, e2), recibidosPorB);
    }

    // 2
    @Test
    void losInteresadosSeAvisanEnElOrdenDeSuscripcion() {
        List<String> orden = new ArrayList<>();
        emisor.suscribir(a -> orden.add("primero"));
        emisor.suscribir(a -> orden.add("segundo"));
        emisor.suscribir(a -> orden.add("tercero"));

        emisor.publicar(e1);

        assertEquals(List.of("primero", "segundo", "tercero"), orden);
    }

    // 3
    @Test
    void unInteresadoNuevoNoRecibeLoQueYaPaso() {
        emisor.suscribir(recibidosPorA::add);
        emisor.publicar(e1);

        emisor.suscribir(recibidosPorB::add);
        emisor.publicar(e2);

        assertEquals(List.of(e1, e2), recibidosPorA);
        assertEquals(List.of(e2), recibidosPorB);
    }

    // 4
    @Test
    void suscribirseDosVecesNoDuplicaLosAvisos() {
        ObservadorDeAcontecimientos o = recibidosPorA::add;
        emisor.suscribir(o);
        emisor.suscribir(o);

        emisor.publicar(e1);

        assertEquals(1, recibidosPorA.size());
    }

    // 5
    @Test
    void rechazaInteresadosYAcontecimientosNulos() {
        assertThrows(NullPointerException.class, () -> emisor.suscribir(null));
        assertThrows(NullPointerException.class, () -> emisor.publicar(null));
    }

    // 6
    @Test
    void sinInteresadosPublicarNoFalla() {
        assertDoesNotThrow(() -> emisor.publicar(e1));
    }

    // 7
    @Test
    void unInteresadoNuevoSeSumaSinModificarElEmisor() {
        class Contador implements ObservadorDeAcontecimientos {
            int cantidad = 0;
            @Override public void alOcurrir(Acontecimiento a) { cantidad++; }
        }
        Contador contador = new Contador();
        emisor.suscribir(contador);

        emisor.publicar(e1);
        emisor.publicar(e2);

        assertEquals(2, contador.cantidad);
    }

    // 8
    @Test
    void suscribirDuranteUnaNotificacionNoRompeNada() {
        emisor.suscribir(a -> emisor.suscribir(recibidosPorB::add));

        assertDoesNotThrow(() -> emisor.publicar(e1));

        emisor.publicar(e2);
        assertEquals(List.of(e2), recibidosPorB);
    }
}