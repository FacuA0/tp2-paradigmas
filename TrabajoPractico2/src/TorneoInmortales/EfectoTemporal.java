package TorneoInmortales;

import java.util.Objects;

public abstract class EfectoTemporal {

    private final String nombre;
    private final int duracion;

    protected EfectoTemporal(String nombre, int duracion) {
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        if (duracion < 1) {
            throw new IllegalArgumentException("La duración debe ser de al menos 1 turno");
        }
        this.duracion = duracion;
    }

    public String getNombre() { return nombre; }

    public int getDuracion() { return duracion; }

  
    public abstract void alComenzarTurno(Combatiente afectado);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EfectoTemporal)) return false;
        return nombre.equals(((EfectoTemporal) o).nombre);
    }

    @Override
    public int hashCode() { return nombre.hashCode(); }

    @Override
    public String toString() { return nombre; }
}