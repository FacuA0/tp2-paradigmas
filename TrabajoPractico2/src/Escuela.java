import java.util.List;

public class Escuela {
	private String nombre;

	private List<Equipamiento> equipamientos;
    private List<Tecnica> tecnicas;
    
	public Escuela(String nombre, List<Tecnica> tecnicas, List<Equipamiento> equipamientos) {
		this.nombre = nombre;
		this.tecnicas = tecnicas;
		this.equipamientos = equipamientos;
	}
	
	public void enseñarTecnicas(Combatiente combatiente) {
        for (Tecnica tecnica : tecnicas) {
            combatiente.aprenderTecnica(tecnica);
        }
    }

	public void repartirEquipamiento(Combatiente combatiente) {
	    for (Equipamiento equipamiento : equipamientos) {
	        combatiente.agregarAlInventario(equipamiento);
	    }
	}
}
