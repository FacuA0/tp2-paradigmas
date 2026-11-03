import java.util.List;

public class ArmaduraDorada extends Equipamiento{

	public ArmaduraDorada() {
		super("Armadura dorada", TipoEquipamiento.ARMADURA,  List.of(new Bonificacion(TipoBonificacion.DANIO_REDUCIDO, 10)));

	}

}
