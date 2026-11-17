package TorneoInmortales;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EscuelaTest {

    private Tecnica corte, rugido, dominio, meditacion, muro;
    private Equipamiento espada, armadura, anillo, sello;
    private Escuela escuelaEspada, palacioVacio;

    @BeforeEach
    void preparar() {
        corte = new CorteDeLosMilAmaneceres();
        rugido = new RugidoDeLaBestiaPrimordial();
        dominio = new DominioDeLaNada();
        meditacion = new MeditacionDelInmortal();
        muro = new MuroDelCuerpoDorado();

        espada = new EspadaCelestial();
        armadura = new ArmaduraDorada();
        anillo = new AnilloEspiritual();
        sello = new SelloDelFenix();

        escuelaEspada = new Escuela("Escuela de la Espada Celestial",
                List.of(corte, rugido), List.of(espada));
        palacioVacio = new Escuela("Palacio del Vacío",
                List.of(dominio, meditacion), List.of(anillo));
    }

    // 1
    @Test
    void laEscuelaGuardaSuNombre() {
        assertEquals("Palacio del Vacío", palacioVacio.getNombre());
    }

    // 2
    @Test
    void ensenaTodasSusTecnicasEnSuOrdenConCincoUsos() {
        Combatiente c = new HechiceroDelVacio(1, "Nébula");

        palacioVacio.enseñarTecnicas(c);

        assertEquals(List.of(dominio, meditacion), c.getTecnicas());
        assertEquals(5, c.usosRestantes(dominio));
        assertEquals(5, c.usosRestantes(meditacion));
    }

    // 3
    @Test
    void repartirEquipamientoLoDejaPuestoEnElCombatiente() {
        Combatiente c = new HechiceroDelVacio(1, "Nébula");

        palacioVacio.repartirEquipamiento(c);

        assertEquals(List.of(anillo), c.getEquipamientos());
    }

    // 4
    @Test
    void losEfectosDelEquipoInicialQuedanAplicados() {
        Combatiente sinEscuela = new CultivadorDeLaEspada(1, "A");
        Combatiente preparado = new CultivadorDeLaEspada(2, "B");

        escuelaEspada.repartirEquipamiento(preparado);

        assertTrue(preparado.danioCausado(20) > sinEscuela.danioCausado(20));
    }

    // 5
    @Test
    void elMismoTipoPreparadoPorEscuelasDistintasQuedaDistinto() {
        Combatiente deLaEspada = new CultivadorDeLaEspada(1, "A");
        Combatiente delVacio = new CultivadorDeLaEspada(2, "B");

        escuelaEspada.enseñarTecnicas(deLaEspada);
        escuelaEspada.repartirEquipamiento(deLaEspada);
        palacioVacio.enseñarTecnicas(delVacio);
        palacioVacio.repartirEquipamiento(delVacio);

        assertNotEquals(deLaEspada.getTecnicas(), delVacio.getTecnicas());
        assertNotEquals(deLaEspada.getEquipamientos(), delVacio.getEquipamientos());
    }

    // 6
    @Test
    void laMismaEscuelaNoDeterminaElTipo() {
        Combatiente cultivador = new CultivadorDeLaEspada(1, "A");
        Combatiente hechicero = new HechiceroDelVacio(2, "B");

        palacioVacio.enseñarTecnicas(cultivador);
        palacioVacio.repartirEquipamiento(cultivador);
        palacioVacio.enseñarTecnicas(hechicero);
        palacioVacio.repartirEquipamiento(hechicero);


        assertEquals(cultivador.getTecnicas(), hechicero.getTecnicas());
        assertEquals(cultivador.getEquipamientos(), hechicero.getEquipamientos());

        assertTrue(hechicero.costoDe(dominio) < cultivador.costoDe(dominio));
        assertTrue(cultivador.danioCausado(20) > hechicero.danioCausado(20));
    }

    // 7
    @Test
    void unaEscuelaVaciaDejaAlCombatienteSinTecnicasNiEquipo() {
        Escuela vacia = new Escuela("Sin enseñanzas", List.of(), List.of());
        Combatiente c = new MonjeCuerpoCelestial(1, "Kael");

        vacia.enseñarTecnicas(c);
        vacia.repartirEquipamiento(c);

        assertTrue(c.getTecnicas().isEmpty());
        assertTrue(c.getEquipamientos().isEmpty());
    }

    // 8
    @Test
    void unaEscuelaNuevaEsSoloOtroObjetoSinCodigoNuevo() {
        Escuela monasterio = new Escuela("Monasterio del Cuerpo Dorado",
                List.of(muro, meditacion), List.of(armadura, sello));
        Combatiente kael = new MonjeCuerpoCelestial(1, "Kael");

        monasterio.enseñarTecnicas(kael);
        monasterio.repartirEquipamiento(kael);

        assertEquals(List.of(muro, meditacion), kael.getTecnicas());
        assertEquals(List.of(armadura, sello), kael.getEquipamientos());
    }

    // 9
    @Test
    void laEscuelaGuardaCopiaDeLasListasQueRecibe() {
        List<Tecnica> tecnicas = new ArrayList<>(List.of(corte));
        List<Equipamiento> equipo = new ArrayList<>(List.of(espada));
        Escuela mutable = new Escuela("Mutable", tecnicas, equipo);

        tecnicas.add(rugido);
        equipo.add(armadura);

        Combatiente c = new CultivadorDeLaEspada(1, "A");
        mutable.enseñarTecnicas(c);
        mutable.repartirEquipamiento(c);

        assertEquals(List.of(corte), c.getTecnicas());
        assertEquals(List.of(espada), c.getEquipamientos());
    }

    // 10
    @Test
    void dosCombatientesDeLaMismaEscuelaSonIndependientes() {
        Combatiente a = new CultivadorDeLaEspada(1, "A");
        Combatiente b = new CultivadorDeLaEspada(2, "B");
        for (Combatiente c : List.of(a, b)) {
            escuelaEspada.enseñarTecnicas(c);
            escuelaEspada.repartirEquipamiento(c);
        }

        a.usarTecnica(corte);
        a.desequipar(espada);

        assertEquals(4, a.usosRestantes(corte));
        assertEquals(5, b.usosRestantes(corte));
        assertTrue(a.getEquipamientos().isEmpty());
        assertEquals(List.of(espada), b.getEquipamientos());
    }

    // 11
    @Test
    void unEquipamientoQueRompeLasReglasSeRechaza() {
        Equipamiento hacha = new Equipamiento("Hacha", TipoEquipamiento.ARMA,
                List.of(new BonusDanio(3)));
        Escuela invalida = new Escuela("Inválida", List.of(), List.of(espada, hacha));
        Combatiente c = new CultivadorDeLaEspada(1, "A");

        assertThrows(EquipamientoInvalidoException.class,
                () -> invalida.repartirEquipamiento(c));
    }

    // 12
    @Test
    void unaTecnicaRepetidaEnLaEscuelaSeRechaza() {
        Escuela repetida = new Escuela("Repetida", List.of(corte, corte), List.of());
        Combatiente c = new CultivadorDeLaEspada(1, "A");

        assertThrows(IllegalArgumentException.class, () -> repetida.enseñarTecnicas(c));
    }
}