package TorneoInmortales;

import java.util.List;
import java.util.Objects;

public class Escuela {

    private final String nombre;
    private final List<Tecnica> tecnicas;
    private final List<Equipamiento> equipamientos;

    public Escuela(String nombre, List<Tecnica> tecnicas, List<Equipamiento> equipamientos) {
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.tecnicas = List.copyOf(tecnicas);
        this.equipamientos = List.copyOf(equipamientos);
    }

    public String getNombre() {
        return nombre;
    }

    public void enseñarTecnicas(Combatiente combatiente) {
        for (Tecnica tecnica : tecnicas) {
            combatiente.aprenderTecnica(tecnica);
        }
    }

    public void repartirEquipamiento(Combatiente combatiente) {
        for (Equipamiento equipamiento : equipamientos) {
            combatiente.agregarAlInventario(equipamiento);
            combatiente.equipar(equipamiento);
        }
    }
}