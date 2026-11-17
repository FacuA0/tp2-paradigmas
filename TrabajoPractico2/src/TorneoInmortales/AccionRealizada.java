package TorneoInmortales;

public record AccionRealizada(int ronda, int asalto, int idEjecutor, String ejecutor, String accion, boolean esTecnica, int idObjetivo, String objetivo, int vidaObjetivo) implements Acontecimiento {
	@Override
	public String descripcion() {
		String que = esTecnica ? "usa " + accion : "realiza un ataque básico";
		if (idEjecutor == idObjetivo) {                       // defensiva o recuperación: se aplica al ejecutor
			return String.format("Asalto %d: %s %s (vida: %d)", asalto, ejecutor, que, vidaObjetivo);
		}
		return String.format("Asalto %d: %s %s sobre %s (vida de %s: %d)", asalto, ejecutor, que, objetivo, objetivo, vidaObjetivo);
	}
}