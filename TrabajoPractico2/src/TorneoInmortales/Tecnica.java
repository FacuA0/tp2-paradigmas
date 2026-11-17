package TorneoInmortales;
import java.util.Objects;

public abstract class Tecnica {
	
	private final String nombre;
    private final int costoEnergia;
    private final TipoTecnica categoria;

    protected Tecnica(String nombre, int costoEnergia, TipoTecnica categoria) {
        this.nombre = Objects.requireNonNull(nombre);
        this.costoEnergia = costoEnergia;
        this.categoria = Objects.requireNonNull(categoria);
    }

    public String getNombre() { return nombre; }
    public int getCostoEnergia() { return costoEnergia; }
    public TipoTecnica getCategoria() { return categoria; }

    public abstract void ejecutar(Combatiente ejecutor, Combatiente objetivo);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tecnica)) return false;
        return nombre.equals(((Tecnica) o).nombre);
    }

    @Override
    public int hashCode() { return nombre.hashCode(); }

    @Override
    public String toString() { return nombre; }
}
