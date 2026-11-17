package TorneoInmortales;
import java.util.List;

public class AnilloEspiritual extends Equipamiento{

	public AnilloEspiritual() {
        super("Anillo espiritual", TipoEquipamiento.ANILLO,
              List.of(new BonusEnergiaMaxima(20)));
    }
}
