package TorneoInmortales;
import java.util.List;

public class SelloDelFenix extends Equipamiento{

	public SelloDelFenix() {
        super("Sello del Fénix", TipoEquipamiento.SELLO,
              List.of(new BonusVidaMaxima(20)));
    }

}
