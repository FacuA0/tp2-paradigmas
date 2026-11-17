package TorneoInmortales;

public record EfectoAplicado(int ronda, int asalto, int idAfectado, String afectado,
                             String efecto, int vidaAfectado) implements Acontecimiento {
    @Override
    public String descripcion() {
        return String.format("Asalto %d: %s siente el efecto de %s (vida: %d)",
                asalto, afectado, efecto, vidaAfectado);
    }
}