package TorneoInmortales;

public record Proclamacion(int idPareja, String primerSanto, String segundoSanto) implements Acontecimiento {
	@Override
	public String descripcion() {
		return String.format("¡Los Dos Santos Supremos son %s y %s (Pareja %d)!", primerSanto, segundoSanto, idPareja);
	}
}