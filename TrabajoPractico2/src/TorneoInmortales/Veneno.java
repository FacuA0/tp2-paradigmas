package TorneoInmortales;

public class Veneno extends EfectoTemporal {

    public static final int DANIO_POR_TURNO = 8;
    public static final int DURACION = 3;

    private final int danioPorTurno;

    public Veneno() {
        this(DANIO_POR_TURNO, DURACION);
    }

    public Veneno(int danioPorTurno, int duracion) {
        super("Veneno", duracion);
        if (danioPorTurno < 1) {
            throw new IllegalArgumentException("El daño por turno debe ser positivo");
        }
        this.danioPorTurno = danioPorTurno;
    }

    @Override
    public void alComenzarTurno(Combatiente afectado) {
        afectado.perderVida(danioPorTurno);                      // daño directo: ignora armadura y tipo
    }
}