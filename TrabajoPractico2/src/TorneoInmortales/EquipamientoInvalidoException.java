package TorneoInmortales;
public class EquipamientoInvalidoException extends RuntimeException {

    public EquipamientoInvalidoException(String mensaje) { super(mensaje); }

    public static EquipamientoInvalidoException noPosee(String combatiente, String objeto) {
        return new EquipamientoInvalidoException(combatiente + " no posee " + objeto);
    }
    public static EquipamientoInvalidoException yaEquipado(String combatiente, String objeto) {
        return new EquipamientoInvalidoException(combatiente + " ya lleva equipado " + objeto);
    }
    public static EquipamientoInvalidoException limiteAlcanzado(String combatiente) {
        return new EquipamientoInvalidoException(combatiente + " ya lleva 3 equipamientos");
    }
    public static EquipamientoInvalidoException categoriaRepetida(String combatiente, String categoria) {
        return new EquipamientoInvalidoException(combatiente + " ya lleva un equipamiento de categoría " + categoria);
    }
    public static EquipamientoInvalidoException noEquipado(String combatiente, String objeto) {
        return new EquipamientoInvalidoException(combatiente + " no lleva equipado " + objeto);
    }
}