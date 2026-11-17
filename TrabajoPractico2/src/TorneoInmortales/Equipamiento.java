package TorneoInmortales;
import java.util.List;

public class Equipamiento {
	
	private String nombre;
    private TipoEquipamiento categoria;
    private List<Bonificacion> bonificaciones;
    
	public Equipamiento(String nombre, TipoEquipamiento categoria, List<Bonificacion> bonificaciones) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.bonificaciones = bonificaciones;
    }
	
	public String getNombre() {
		return nombre;
	}
	
	public TipoEquipamiento getCategoria() {
		return categoria;
	}
	
    public List<Bonificacion> getBonificaciones() {
		return bonificaciones;
	}
    
    @Override
    public String toString() {
        return "{" + nombre +
                ", Categoria:" + categoria +
                ", Bonificaciones:" + bonificaciones +
                '}';
    }
}