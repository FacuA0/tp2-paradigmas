package TorneoInmortales;

public class LotoRegenerador extends Tecnica {

    private static final int CURACION_INMEDIATA = 10;

    public LotoRegenerador() {
        super("Loto Regenerador", 20, TipoTecnica.RECUPERACION);
    }

    @Override
    public void ejecutar(Combatiente ejecutor, Combatiente objetivo) {
        ejecutor.recibirCuracion(CURACION_INMEDIATA);
        ejecutor.aplicarEfecto(new Regeneracion());
    }
}