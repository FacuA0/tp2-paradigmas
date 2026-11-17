package TorneoInmortales;

public record Eliminacion(int ronda, int idCombatiente, String nombre) implements Acontecimiento {
    @Override
    public String descripcion() {
        return String.format("%s queda fuera de combate", nombre);
    }
}