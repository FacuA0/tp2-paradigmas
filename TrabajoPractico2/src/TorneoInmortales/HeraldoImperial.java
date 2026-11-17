package TorneoInmortales;

import java.io.PrintStream;
import java.util.List;
import java.util.Objects;

public class HeraldoImperial implements ObservadorDeAcontecimientos {

    private final PrintStream salida;

    public HeraldoImperial() {
        this(System.out);
    }

    public HeraldoImperial(PrintStream salida) {
        this.salida = Objects.requireNonNull(salida, "La salida no puede ser nula");
    }

    @Override
    public void alOcurrir(Acontecimiento acontecimiento) {
        Objects.requireNonNull(acontecimiento, "El acontecimiento no puede ser nulo");
        salida.println(acontecimiento.descripcion());
    }

    public void anunciar(String mensaje) {
        Objects.requireNonNull(mensaje, "El mensaje no puede ser nulo");
        salida.println(mensaje);
    }

    public void presentarTabla(List<FilaDePosicion> tabla) {
        Objects.requireNonNull(tabla, "La tabla no puede ser nula");
        salida.println("=== TABLA DE POSICIONES ===");
        if (tabla.isEmpty()) {
            salida.println("(sin resultados registrados)");
            return;
        }
        int posicion = 1;
        for (FilaDePosicion fila : tabla) {
            salida.println(String.format("%d. Pareja %d (%s) - última ronda: %d - vida total final: %d",
                    posicion++, fila.idPareja(), fila.integrantes(),
                    fila.ultimaRonda(), fila.vidaTotalFinal()));
        }
    }
}