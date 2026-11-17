package TorneoInmortales;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CreadorCombatientesTest {

    private CreadorCombatientes creador;
    private Tecnica corte, dominio, meditacion;
    private Equipamiento espada, anillo;
    private Escuela escuelaEspada, palacioVacio;

    @BeforeEach
    void preparar() {
        creador = new CreadorCombatientes();
        corte = new CorteDeLosMilAmaneceres();
        dominio = new DominioDeLaNada();
        meditacion = new MeditacionDelInmortal();
        espada = new EspadaCelestial();
        anillo = new AnilloEspiritual();

        escuelaEspada = new Escuela("Escuela de la Espada Celestial",
                List.of(corte), List.of(espada));
        palacioVacio = new Escuela("Palacio del Vacío",
                List.of(dominio, meditacion), List.of(anillo));
    }

    // 1
    @Test
    void creaCadaTipoDeCombatiente() {
        assertInstanceOf(CultivadorDeLaEspada.class,
                creador.crear(TipoCombatiente.CULTIVADOR_ESPADA, "Aurelio", escuelaEspada));
        assertInstanceOf(HechiceroDelVacio.class,
                creador.crear(TipoCombatiente.HECHICERO_VACIO, "Nébula", palacioVacio));
        assertInstanceOf(MonjeCuerpoCelestial.class,
                creador.crear(TipoCombatiente.MONJE_CUERPO_CELESTIAL, "Kael", palacioVacio));
    }

    // 2
    @Test
    void asignaIdsConsecutivosEnOrdenDeCreacion() {
        Combatiente a = creador.crear(TipoCombatiente.CULTIVADOR_ESPADA, "A", escuelaEspada);
        Combatiente b = creador.crear(TipoCombatiente.HECHICERO_VACIO, "B", palacioVacio);
        Combatiente c = creador.crear(TipoCombatiente.MONJE_CUERPO_CELESTIAL, "C", palacioVacio);

        assertEquals(1, a.getId());
        assertEquals(2, b.getId());
        assertEquals(3, c.getId());
    }

    // 3
    @Test
    void cadaCreadorLlevaSuPropioContador() {
        creador.crear(TipoCombatiente.CULTIVADOR_ESPADA, "A", escuelaEspada);
        creador.crear(TipoCombatiente.CULTIVADOR_ESPADA, "B", escuelaEspada);

        CreadorCombatientes otro = new CreadorCombatientes();
        Combatiente primero = otro.crear(TipoCombatiente.CULTIVADOR_ESPADA, "C", escuelaEspada);

        assertEquals(1, primero.getId());
    }

    // 4
    @Test
    void laIgualdadDependeDelIdYNoDelNombre() {
        Combatiente a = creador.crear(TipoCombatiente.CULTIVADOR_ESPADA, "Aurelio", escuelaEspada);
        Combatiente mismoIdOtroNombre = new CreadorCombatientes()
                .crear(TipoCombatiente.HECHICERO_VACIO, "Otro", palacioVacio);
        Combatiente mismoNombreOtroId = creador
                .crear(TipoCombatiente.CULTIVADOR_ESPADA, "Aurelio", escuelaEspada);

        assertEquals(a, mismoIdOtroNombre);
        assertEquals(a.hashCode(), mismoIdOtroNombre.hashCode());
        assertNotEquals(a, mismoNombreOtroId);
    }

    // 5
    @Test
    void salePreparadoConLasTecnicasDeSuEscuelaEnOrden() {
        Combatiente nebula = creador.crear(TipoCombatiente.HECHICERO_VACIO, "Nébula", palacioVacio);

        assertEquals(List.of(dominio, meditacion), nebula.getTecnicas());
    }

    // 6
    @Test
    void cadaTecnicaArrancaConCincoUsos() {
        Combatiente nebula = creador.crear(TipoCombatiente.HECHICERO_VACIO, "Nébula", palacioVacio);

        assertEquals(5, nebula.usosRestantes(dominio));
        assertEquals(5, nebula.usosRestantes(meditacion));
    }

    // 7
    @Test
    void salePreparadoConElEquipamientoDeSuEscuela() {
        Combatiente nebula = creador.crear(TipoCombatiente.HECHICERO_VACIO, "Nébula", palacioVacio);

        assertEquals(List.of(anillo), nebula.getEquipamientos());
    }

    // 8
    @Test
    void elEquipamientoSubeElMaximoPeroNoElValorActual() {
        Combatiente nebula = creador.crear(TipoCombatiente.HECHICERO_VACIO, "Nébula", palacioVacio);

        assertTrue(nebula.getEnergiaMaxima() > nebula.getEnergiaActual());

        nebula.prepararParaCombate();

        assertEquals(nebula.getEnergiaMaxima(), nebula.getEnergiaActual());
    }

    // 9
    @Test
    void rechazaTipoNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> creador.crear(null, "X", escuelaEspada));
    }

    // 10
    @Test
    void rechazaEscuelaNula() {
        assertThrows(NullPointerException.class,
                () -> creador.crear(TipoCombatiente.CULTIVADOR_ESPADA, "X", null));
    }

    // 11
    @Test
    void siLaCreacionFallaNoSeConsumeElId() {
        Equipamiento otraArma = new Equipamiento("Hacha", TipoEquipamiento.ARMA, List.of());
        Escuela invalida = new Escuela("Inválida", List.of(), List.of(espada, otraArma));

        assertThrows(EquipamientoInvalidoException.class,
                () -> creador.crear(TipoCombatiente.CULTIVADOR_ESPADA, "Falla", invalida));

        Combatiente siguiente = creador.crear(TipoCombatiente.CULTIVADOR_ESPADA, "Ok", escuelaEspada);
        assertEquals(1, siguiente.getId());
    }

    // 12
    @Test
    void combatientesDeLaMismaEscuelaSonIndependientes() {
        Combatiente a = creador.crear(TipoCombatiente.CULTIVADOR_ESPADA, "A", escuelaEspada);
        Combatiente b = creador.crear(TipoCombatiente.CULTIVADOR_ESPADA, "B", escuelaEspada);

        a.usarTecnica(corte);
        a.desequipar(espada);

        assertEquals(4, a.usosRestantes(corte));
        assertEquals(5, b.usosRestantes(corte));
        assertTrue(b.getEquipamientos().contains(espada));
    }
}