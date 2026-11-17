package TorneoInmortales;

public class TorneoInvalidoException extends RuntimeException {

    public TorneoInvalidoException(String mensaje) {
        super(mensaje);
    }

    public static TorneoInvalidoException parejaRepetida(int idPareja) {
        return new TorneoInvalidoException("La pareja " + idPareja + " ya está inscripta");
    }

    public static TorneoInvalidoException combatienteRepetido(String nombre) {
        return new TorneoInvalidoException(nombre + " ya está inscripto en otra pareja");
    }

    public static TorneoInvalidoException cantidadInvalida(int cantidad) {
        return new TorneoInvalidoException(
                "El torneo se juega con 4 u 8 parejas y hay " + cantidad + " inscriptas");
    }
}