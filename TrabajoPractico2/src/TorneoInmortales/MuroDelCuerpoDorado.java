package TorneoInmortales;

public class MuroDelCuerpoDorado extends Tecnica{

	private static final int DEFENSA = 25;
	
    public MuroDelCuerpoDorado() {
        super("Muro del cuerpo dorado", 15, TipoTecnica.DEFENSIVA);
    }

    @Override
    public void ejecutar(Combatiente ejecutor, Combatiente objetivo) {
    	ejecutor.activarDefensa(DEFENSA);
    }
}

