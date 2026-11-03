import java.util.List;

public class EspadaCelestial extends Equipamiento{

	public EspadaCelestial() {
		super("Espada celestial", TipoEquipamiento.ARMA,  List.of(new Bonificacion(TipoBonificacion.DANIO, 20)));
	}

}
