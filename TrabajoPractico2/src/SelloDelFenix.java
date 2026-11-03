import java.util.List;

public class SelloDelFenix extends Equipamiento{

	public SelloDelFenix() {
		super("Sello del fenix", TipoEquipamiento.SELLO,  List.of(new Bonificacion(TipoBonificacion.VIDA_MAXIMA, 10)));
	}

}
