package TorneoInmortales;

public class RugidoDeLaBestiaPrimordial extends Tecnica{

private static final int DANIO_BASE = 35;
	
    public RugidoDeLaBestiaPrimordial() {
        super("Rugido de la bestia primordial", 25, TipoTecnica.OFENSIVA);
    }

    @Override
    public void ejecutar(Combatiente ejecutor, Combatiente objetivo) {
    	objetivo.recibirDanio(ejecutor.danioCausado(DANIO_BASE));
    }
}
