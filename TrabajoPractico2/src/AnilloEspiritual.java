import java.util.List;

public class AnilloEspiritual extends Equipamiento{

	public AnilloEspiritual() {
		super("Anillo espiritual", TipoEquipamiento.ANILLO,  List.of(new Bonificacion(TipoBonificacion.ENERGIA_MAXIMA, 20)));
	}
	
}
