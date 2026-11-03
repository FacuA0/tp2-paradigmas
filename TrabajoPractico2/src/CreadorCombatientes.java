import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CreadorCombatientes {

    private final Map<TipoCombatiente, Function<String, Combatiente>> creadores
        = new HashMap<>();
    //CultivadorDeLaEspada::new reemplaza a nombre -> new CultivadorDeLaEspada(nombre)
    public CreadorCombatientes() {
        registrar(TipoCombatiente.CULTIVADOR_ESPADA, CultivadorDeLaEspada::new);
        registrar(TipoCombatiente.HECHICERO_VACIO, HechiceroDelVacio::new);
        registrar(TipoCombatiente.MONJE_CUERPO_CELESTIAL, MonjeCuerpoCelestial::new);
    }

    public void registrar(
        TipoCombatiente tipo,
        Function<String, Combatiente> constructor
    ) {
        creadores.put(tipo, constructor);
    }

    public Combatiente crear(TipoCombatiente tipo, String nombre, Escuela escuela) {
        Function<String, Combatiente> constructor = creadores.get(tipo);

        if (constructor == null) {
            throw new IllegalArgumentException("Tipo desconocido");
        }

        Combatiente combatiente = constructor.apply(nombre);
        
        escuela.enseñarTecnicas(combatiente);
        escuela.repartirEquipamiento(combatiente);

        return combatiente;
    }
}