package TorneoInmortales;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EquipamientoTest {

    private Combatiente c;
    private Equipamiento espada, armadura, anillo, sello, hacha;

    private record Estado(List<Equipamiento> equipados, int vidaMax, int energiaMax, int danio) {}

    private Estado foto() {
        return new Estado(new ArrayList<>(c.getEquipamientos()),
                c.getVidaMaxima(), c.getEnergiaMaxima(), c.danioCausado(10));
    }

    @BeforeEach
    void preparar() {
        c = new CultivadorDeLaEspada(1, "Aurelio");
        espada = new EspadaCelestial();
        armadura = new ArmaduraDorada();
        anillo = new AnilloEspiritual();
        sello = new SelloDelFenix();
        hacha = new Equipamiento("Hacha", TipoEquipamiento.ARMA, List.of(new BonusDanio(3)));

        for (Equipamiento e : List.of(espada, armadura, anillo, sello, hacha)) {
            c.agregarAlInventario(e);
        }
    }

    // 1
    @Test
    void equiparYConsultarLosObjetosQueLleva() {
        c.equipar(espada);
        c.equipar(anillo);

        assertEquals(List.of(espada, anillo), c.getEquipamientos());
    }

    // 2
    @Test
    void elCuartoObjetoSeRechazaSinCambios() {
        c.equipar(espada);
        c.equipar(armadura);
        c.equipar(anillo);
        Estado antes = foto();

        assertThrows(EquipamientoInvalidoException.class, () -> c.equipar(sello));

        assertEquals(antes, foto());
    }

    // 3
    @Test
    void laCategoriaRepetidaSeRechazaSinCambios() {
        c.equipar(espada);
        Estado antes = foto();

        assertThrows(EquipamientoInvalidoException.class, () -> c.equipar(hacha));

        assertEquals(antes, foto());
    }

    // 4
    @Test
    void losEfectosSeAcumulan() {
        int vidaMaxInicial = c.getVidaMaxima();
        int energiaMaxInicial = c.getEnergiaMaxima();

        c.equipar(sello);
        int vidaMaxConSello = c.getVidaMaxima();
        c.equipar(anillo);

        assertEquals(vidaMaxConSello, c.getVidaMaxima());
        assertTrue(vidaMaxConSello > vidaMaxInicial);
        assertTrue(c.getEnergiaMaxima() > energiaMaxInicial);
    }

    // 5
    @Test
    void equiparUnObjetoDeVidaMaximaNoSubeLaVidaActual() {
        int vidaActualAntes = c.getVidaActual();
        int vidaMaxAntes = c.getVidaMaxima();

        c.equipar(sello);

        assertTrue(c.getVidaMaxima() > vidaMaxAntes);
        assertEquals(vidaActualAntes, c.getVidaActual());
    }

    // 6
    @Test
    void alQuitarUnObjetoDeVidaMaximaLaVidaActualSeRecorta() {
        int vidaMaxOriginal = c.getVidaMaxima();
        c.equipar(sello);
        c.prepararParaCombate();
        assertTrue(c.getVidaActual() > vidaMaxOriginal);

        c.desequipar(sello);

        assertEquals(vidaMaxOriginal, c.getVidaMaxima());
        assertEquals(vidaMaxOriginal, c.getVidaActual());
    }

    // 7
    @Test
    void alQuitarUnObjetoDeEnergiaMaximaLaEnergiaActualSeRecorta() {
        int energiaMaxOriginal = c.getEnergiaMaxima();
        c.equipar(anillo);
        assertTrue(c.getEnergiaMaxima() > energiaMaxOriginal);
        c.prepararParaCombate();

        c.desequipar(anillo);

        assertEquals(energiaMaxOriginal, c.getEnergiaMaxima());
        assertEquals(energiaMaxOriginal, c.getEnergiaActual());
    }

    // 8
    @Test
    void alQuitarLaEspadaElDanioVuelveAlOriginal() {
        int danioSinEspada = c.danioCausado(10);

        c.equipar(espada);
        assertTrue(c.danioCausado(10) > danioSinEspada);

        c.desequipar(espada);
        assertEquals(danioSinEspada, c.danioCausado(10));
    }

    // 9
    @Test
    void laArmaduraReduceElDanioRecibido() {
        c.prepararParaCombate();
        int vidaInicial = c.getVidaActual();
        c.recibirDanio(20);
        int perdidaSinArmadura = vidaInicial - c.getVidaActual();

        c.prepararParaCombate();
        c.equipar(armadura);
        c.recibirDanio(20);
        int perdidaConArmadura = vidaInicial - c.getVidaActual();

        assertTrue(perdidaConArmadura < perdidaSinArmadura);
    }

    // 9b
    @Test
    void laArmaduraSigueActivaEntreCombates() {
        c.prepararParaCombate();
        int vida = c.getVidaActual();
        c.recibirDanio(20);
        int perdidaSinArmadura = vida - c.getVidaActual();

        c.equipar(armadura);
        c.prepararParaCombate();
        c.recibirDanio(20);
        int perdidaConArmadura = vida - c.getVidaActual();

        assertTrue(perdidaConArmadura < perdidaSinArmadura);
    }

    // 9c
    @Test
    void ponerseYQuitarseLaArmaduraNoActivaLaDefensa() {
        c.equipar(armadura);
        c.desequipar(armadura);

        assertFalse(c.tieneDefensaActiva());
    }

    // 9d
    @Test
    void laDefensaActivaTerminaConElCombate() {
        c.activarDefensa(10);
        assertTrue(c.tieneDefensaActiva());

        c.prepararParaCombate();

        assertFalse(c.tieneDefensaActiva());
    }

    // 10
    @Test
    void noPuedeEquiparUnObjetoQueNoPosee() {
        Equipamiento ajeno = new Equipamiento("Ajeno", TipoEquipamiento.ARMA, List.of());
        Estado antes = foto();

        assertThrows(EquipamientoInvalidoException.class, () -> c.equipar(ajeno));

        assertEquals(antes, foto());
    }

    // 11
    @Test
    void noPuedeEquiparDosVecesElMismoObjeto() {
        c.equipar(espada);
        Estado antes = foto();

        assertThrows(EquipamientoInvalidoException.class, () -> c.equipar(espada));

        assertEquals(antes, foto());
    }

    // 12
    @Test
    void noPuedeQuitarUnObjetoQueNoLleva() {
        Estado antes = foto();

        assertThrows(EquipamientoInvalidoException.class, () -> c.desequipar(espada));

        assertEquals(antes, foto());
    }

    // 13
    @Test
    void sePuedeQuitarYVolverAEquiparSinDuplicarBonus() {
        c.equipar(sello);
        int vidaMaxConSello = c.getVidaMaxima();

        c.desequipar(sello);
        c.equipar(sello);

        assertEquals(vidaMaxConSello, c.getVidaMaxima());
        assertEquals(List.of(sello), c.getEquipamientos());
    }

    // 14
    @Test
    void laListaDeEquipamientosEsDeSoloLectura() {
        c.equipar(espada);

        assertThrows(UnsupportedOperationException.class,
                () -> c.getEquipamientos().add(hacha));

        assertEquals(List.of(espada), c.getEquipamientos());
    }

    // 15
    @Test
    void laDefensaActivaReduceElDanioRecibido() {
        c.prepararParaCombate();
        int vida = c.getVidaActual();
        c.recibirDanio(20);
        int perdidaSinDefensa = vida - c.getVidaActual();

        c.prepararParaCombate();
        c.activarDefensa(25);
        c.recibirDanio(20);
        int perdidaConDefensa = vida - c.getVidaActual();

        assertTrue(perdidaConDefensa < perdidaSinDefensa);
    }

    // 16
    @Test
    void laArmaduraYLaDefensaActivaSeSuman() {
        c.prepararParaCombate();
        int vida = c.getVidaActual();
        c.recibirDanio(60);
        int sinNada = vida - c.getVidaActual();

        c.prepararParaCombate();
        c.equipar(armadura);
        c.recibirDanio(60);
        int soloArmadura = vida - c.getVidaActual();

        c.prepararParaCombate();
        c.activarDefensa(25);
        c.recibirDanio(60);
        int armaduraYDefensa = vida - c.getVidaActual();

        assertTrue(soloArmadura < sinNada);
        assertTrue(armaduraYDefensa < soloArmadura);
    }

    // 17
    @Test
    void alQuitarLaArmaduraElDanioRecibidoVuelveAlOriginal() {
        c.prepararParaCombate();
        int vida = c.getVidaActual();
        c.recibirDanio(20);
        int perdidaOriginal = vida - c.getVidaActual();

        c.equipar(armadura);
        c.desequipar(armadura);
        c.prepararParaCombate();
        c.recibirDanio(20);

        assertEquals(perdidaOriginal, vida - c.getVidaActual());
    }

    // 18
    @Test
    void elDanioRecibidoNuncaCuraAlCombatiente() {
        c.prepararParaCombate();
        int vida = c.getVidaActual();
        c.activarDefensa(1_000_000);

        c.recibirDanio(20);

        assertTrue(c.getVidaActual() <= vida);
    }
}