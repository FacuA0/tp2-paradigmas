
public class Tecnica {
	
	private String nombre;
	private int usosMaximos;
	public Tecnica(String nombre, int usos) {
		this.nombre = nombre;
		this.usosMaximos = usos;
	}
	public int getUsosMaximos() {
		return usosMaximos;
	}
	public String getNombre() {
		return nombre;
	}
}
