package TorneoInmortales;
public class MeditacionDelInmortal extends Tecnica {

    private static final int CURACION = 20;

    public MeditacionDelInmortal() {
        super("Meditación del Inmortal", 20, TipoTecnica.RECUPERACION);
    }

    @Override
    public void ejecutar(Combatiente ejecutor, Combatiente objetivo) {
        ejecutor.curar(CURACION, objetivo);
    }
}