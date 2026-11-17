package TorneoInmortales;

public class HechiceroDelVacio extends Combatiente{

	private static final int PORCENTAJE_COSTO = 75; 
	
	public HechiceroDelVacio(int id, String nombre) {
		super(id,nombre);
	}

	@Override
	public int modificarDanioInfligido(int danio) {
		// TODO Auto-generated method stub
		return danio;
	}

	@Override
	public int modificarDanioRecibido(int danio) {
		// TODO Auto-generated method stub
		return danio;
	}

	@Override
	public int modificarCostoTecnica(int costo) {
		// TODO Auto-generated method stub
		return costo * PORCENTAJE_COSTO / 100;
	}

}
