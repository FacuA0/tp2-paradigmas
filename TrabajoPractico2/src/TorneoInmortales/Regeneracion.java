package TorneoInmortales;

public class Regeneracion extends EfectoTemporal {

    public static final int CURACION_POR_TURNO = 5;
    public static final int DURACION = 3;

    private final int curacionPorTurno;

    public Regeneracion() {
        this(CURACION_POR_TURNO, DURACION);
    }

    public Regeneracion(int curacionPorTurno, int duracion) {
        super("Regeneración", duracion);
        if (curacionPorTurno < 1) {
            throw new IllegalArgumentException("La curación por turno debe ser positiva");
        }
        this.curacionPorTurno = curacionPorTurno;
    }

    @Override
    public void alComenzarTurno(Combatiente afectado) {
        afectado.recibirCuracion(curacionPorTurno);
    }
}