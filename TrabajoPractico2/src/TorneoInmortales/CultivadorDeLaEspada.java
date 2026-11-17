package TorneoInmortales;

public class CultivadorDeLaEspada extends Combatiente{

	private static final int PORCENTAJE_DANIO_CAUSADO = 120;
	
	public CultivadorDeLaEspada(int id, String nombre) {
		super(id,nombre);
	}

	@Override
	public int modificarDanioInfligido(int danio) {
		return danio * PORCENTAJE_DANIO_CAUSADO / 100;
	}

	@Override
	public int modificarDanioRecibido(int danio) {
		return danio;
	}

	@Override
	public int modificarCostoTecnica(int costo) {
		return costo;
	}

}
