package TorneoInmortales;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class AcontecimientosTest {

    // 1
    @Test
    void guardaLosDatosDelMomentoEnQueOcurrio() {
        Combatiente kael = new MonjeCuerpoCelestial(3, "Kael");
        AccionRealizada accion = new AccionRealizada(1, 1, 1, "Aurelio",
                "Corte de los Mil Amaneceres", true,
                kael.getId(), kael.getNombre(), kael.getVidaActual());
        int vidaEnElMomento = accion.vidaObjetivo();

        kael.recibirDanio(30);

        assertEquals(vidaEnElMomento, accion.vidaObjetivo());
        assertNotEquals(accion.vidaObjetivo(), kael.getVidaActual());
    }

    // 2
    @Test
    void dosAcontecimientosConLosMismosDatosSonIguales() {
        assertEquals(new Eliminacion(1, 3, "Kael"), new Eliminacion(1, 3, "Kael"));
        assertEquals(new Eliminacion(1, 3, "Kael").hashCode(), new Eliminacion(1, 3, "Kael").hashCode());
        assertNotEquals(new Eliminacion(1, 3, "Kael"), new Eliminacion(2, 3, "Kael"));
    }

    // 3
    @Test
    void cadaAcontecimientoSeDescribeConSusDatosClave() {
        String inicio = new InicioDeCombate(1, 1, "Aurelio y Nébula", 2, "Kael y Darian").descripcion();
        assertTrue(inicio.contains("Aurelio y Nébula") && inicio.contains("Kael y Darian"));

        String resultado = new ResultadoDeCombate(2, 1, "Aurelio y Nébula", 150, 2, "Kael y Darian", 0).descripcion();
        assertTrue(resultado.contains("150") && resultado.contains("Aurelio y Nébula"));

        String eliminacion = new Eliminacion(1, 3, "Kael").descripcion();
        assertTrue(eliminacion.contains("Kael"));

        String proclamacion = new Proclamacion(1, "Aurelio", "Nébula").descripcion();
        assertTrue(proclamacion.contains("Aurelio") && proclamacion.contains("Nébula"));
    }

    // 4
    @Test
    void laDescripcionDistingueTecnicaDeAtaqueBasico() {
        String tecnica = new AccionRealizada(1, 2, 1, "Aurelio", "Corte de los Mil Amaneceres", true,
                3, "Kael", 70).descripcion();
        String basico = new AccionRealizada(1, 2, 1, "Aurelio", "Ataque básico", false,
                3, "Kael", 85).descripcion();

        assertTrue(tecnica.contains("Corte de los Mil Amaneceres"));
        assertTrue(basico.contains("ataque básico"));
        assertFalse(basico.contains("Corte"));
    }

    // 5
    @Test
    void unaAccionSobreElPropioEjecutorNoMencionaUnObjetivoAparte() {
        String defensiva = new AccionRealizada(1, 1, 1, "Aurelio", "Muro del Cuerpo Dorado", true,
                1, "Aurelio", 100).descripcion();

        assertFalse(defensiva.contains("sobre"));
    }
    
    // 6
    @Test
    void elEfectoAplicadoSeDescribeConElAfectadoElEfectoYLaVida() {
        String texto = new EfectoAplicado(1, 2, 3, "Kael", "Veneno", 62).descripcion();

        assertTrue(texto.contains("Kael"));
        assertTrue(texto.contains("Veneno"));
        assertTrue(texto.contains("62"));
    }
}