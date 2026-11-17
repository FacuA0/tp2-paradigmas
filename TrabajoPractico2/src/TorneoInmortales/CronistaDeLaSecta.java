package TorneoInmortales;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CronistaDeLaSecta implements ObservadorDeAcontecimientos {

   
    private static final Comparator<FilaDePosicion> ORDEN_DE_POSICIONES =
            Comparator.comparingInt(FilaDePosicion::ultimaRonda).reversed()
                    .thenComparing(Comparator.comparingInt(FilaDePosicion::vidaTotalFinal).reversed())
                    .thenComparingInt(FilaDePosicion::idPareja);

    private final List<Acontecimiento> historial = new ArrayList<>();

    @Override
    public void alOcurrir(Acontecimiento acontecimiento) {
        historial.add(Objects.requireNonNull(acontecimiento, "El acontecimiento no puede ser nulo"));
    }

    public List<Acontecimiento> getHistorial() {
        return List.copyOf(historial);
    }

    public <T extends Acontecimiento> List<T> acontecimientosDe(Class<T> tipo) {
        return historial.stream().filter(tipo::isInstance).map(tipo::cast).toList();
    }

    public List<FilaDePosicion> tablaDePosiciones() {
        Map<Integer, FilaDePosicion> filas = new LinkedHashMap<>();
        for (ResultadoDeCombate r : acontecimientosDe(ResultadoDeCombate.class)) {
            agregar(filas, new FilaDePosicion(r.idGanadora(), r.integrantesGanadora(), r.ronda(), r.vidaTotalGanadora()));
            agregar(filas, new FilaDePosicion(r.idPerdedora(), r.integrantesPerdedora(), r.ronda(), r.vidaTotalPerdedora()));
        }
        return filas.values().stream().sorted(ORDEN_DE_POSICIONES).toList();
    }

    private void agregar(Map<Integer, FilaDePosicion> filas, FilaDePosicion nueva) {
        filas.merge(nueva.idPareja(), nueva,
                (vieja, otra) -> otra.ultimaRonda() >= vieja.ultimaRonda() ? otra : vieja);
    }
}