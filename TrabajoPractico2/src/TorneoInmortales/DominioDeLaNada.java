package TorneoInmortales;
public class DominioDeLaNada extends Tecnica{
	
	private static final int DANIO_BASE = 30;
	private static final int ENERGIA_ABSORBIDA = 20;
	
    public DominioDeLaNada() {
        super("Dominio de la nada", 20, TipoTecnica.OFENSIVA);
    }

    @Override
    public void ejecutar(Combatiente ejecutor, Combatiente objetivo) {
    	objetivo.recibirDanio(ejecutor.danioCausado(DANIO_BASE));
    	objetivo.perderEnergia(ENERGIA_ABSORBIDA);
    }
}