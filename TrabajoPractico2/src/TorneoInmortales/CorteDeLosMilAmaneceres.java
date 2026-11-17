package TorneoInmortales;
public class CorteDeLosMilAmaneceres extends Tecnica {

    private static final int DANIO_BASE = 20;

    public CorteDeLosMilAmaneceres() {
        super("Corte de los Mil Amaneceres", 15, TipoTecnica.OFENSIVA);
    }

    @Override
    public void ejecutar(Combatiente ejecutor, Combatiente objetivo) {
        objetivo.recibirDanio(ejecutor.danioCausado(DANIO_BASE));
    }
}