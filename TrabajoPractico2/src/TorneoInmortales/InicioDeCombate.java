package TorneoInmortales;

public record InicioDeCombate(int ronda, int idParejaA, String integrantesA, int idParejaB, String integrantesB) implements Acontecimiento {
	@Override
	public String descripcion() {
		return String.format("Ronda %d - Comienza el combate: Pareja %d (%s) contra Pareja %d (%s)", ronda, idParejaA, integrantesA, idParejaB, integrantesB);
	}
}