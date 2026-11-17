package TorneoInmortales;
import java.util.List;

public class ArmaduraDorada extends Equipamiento{

	public ArmaduraDorada() {
        super("Armadura dorada", TipoEquipamiento.ARMADURA,
              List.of(new BonusReduccionDanio(15)));
    }
}
