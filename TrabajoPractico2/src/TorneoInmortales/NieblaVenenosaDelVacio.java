package TorneoInmortales;

public class NieblaVenenosaDelVacio extends Tecnica {

    private static final int DANIO_BASE = 10;

    public NieblaVenenosaDelVacio() {
        super("Niebla Venenosa del Vacío", 20, TipoTecnica.OFENSIVA);
    }

    @Override
    public void ejecutar(Combatiente ejecutor, Combatiente objetivo) {
        objetivo.recibirDanio(ejecutor.danioCausado(DANIO_BASE));
        if (objetivo.estaEnPie()) {
            objetivo.aplicarEfecto(new Veneno());
        }
    }
}