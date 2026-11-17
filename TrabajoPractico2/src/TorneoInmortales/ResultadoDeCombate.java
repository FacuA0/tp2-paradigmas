package TorneoInmortales;

public record ResultadoDeCombate(int ronda, int idGanadora, String integrantesGanadora, int vidaTotalGanadora, int idPerdedora, String integrantesPerdedora, int vidaTotalPerdedora) implements Acontecimiento {
	@Override
	public String descripcion() {
		return String.format("Fin del combate (ronda %d): gana la Pareja %d (%s) con %d de vida total; " + "la Pareja %d (%s) termina con %d", ronda, idGanadora, integrantesGanadora, vidaTotalGanadora, idPerdedora, integrantesPerdedora, vidaTotalPerdedora);
	}
}