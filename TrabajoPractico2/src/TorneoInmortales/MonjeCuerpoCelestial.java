package TorneoInmortales;

public class MonjeCuerpoCelestial extends Combatiente{

	private static final int PORCENTAJE_DANIO_RECIBIDO = 75;
	
	public MonjeCuerpoCelestial(int id, String nombre) {
		super(id, nombre);
	}

	@Override
	public int modificarDanioInfligido(int danio) {
		// TODO Auto-generated method stub
		return danio;
	}

	@Override
	public int modificarDanioRecibido(int danio) {
		// TODO Auto-generated method stub
		 return danio * PORCENTAJE_DANIO_RECIBIDO / 100;
	}

	@Override
	public int modificarCostoTecnica(int costo) {
		// TODO Auto-generated method stub
		return costo;
	}

}
