package TorneoInmortales;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;


public class CreadorCombatientes {

	private final Map<TipoCombatiente, BiFunction<Integer, String, Combatiente>> creadores = new HashMap<>();
    private int proximoId = 1;
    
    public CreadorCombatientes() {
        registrar(TipoCombatiente.CULTIVADOR_ESPADA, CultivadorDeLaEspada::new);
        registrar(TipoCombatiente.HECHICERO_VACIO, HechiceroDelVacio::new);
        registrar(TipoCombatiente.MONJE_CUERPO_CELESTIAL, MonjeCuerpoCelestial::new);
    }

    public void registrar(
        TipoCombatiente tipo,
        BiFunction<Integer, String, Combatiente> constructor
    ) {
        creadores.put(tipo, constructor);
    }

    public Combatiente crear(TipoCombatiente tipo, String nombre, Escuela escuela) {
    	BiFunction<Integer, String, Combatiente> constructor = creadores.get(tipo);

        if (constructor == null) {
            throw new IllegalArgumentException("Tipo desconocido");
        }

        Combatiente combatiente = constructor.apply(proximoId, nombre);
        
        escuela.enseñarTecnicas(combatiente);
        escuela.repartirEquipamiento(combatiente);

        proximoId++;
        return combatiente;
    }
}