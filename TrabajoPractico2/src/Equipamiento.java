import java.util.List;

public class Equipamiento {

    public TipoEquipamiento getCategoria() {
		return categoria;
	}

	public String getNombre() {
		return nombre;
	}

	private String nombre;
    private TipoEquipamiento categoria;
    private List<Bonificacion> bonificaciones;

    public List<Bonificacion> getBonificaciones() {
		return bonificaciones;
	}

	public Equipamiento(String nombre, TipoEquipamiento categoria, List<Bonificacion> bonificaciones) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.bonificaciones = bonificaciones;
    }
    
    @Override
    public String toString() {
        return "{" + nombre +
                ", Categoria:" + categoria +
                ", Bonificaciones:" + bonificaciones +
                '}';
    }
}