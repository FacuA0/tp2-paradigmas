package TorneoInmortales;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Emisor {

    private final List<ObservadorDeAcontecimientos> observadores = new ArrayList<>();

    public void suscribir(ObservadorDeAcontecimientos observador) {
        Objects.requireNonNull(observador, "El interesado no puede ser nulo");
        if (!observadores.contains(observador)) {
            observadores.add(observador);
        }
    }

    public void publicar(Acontecimiento acontecimiento) {
        Objects.requireNonNull(acontecimiento, "El acontecimiento no puede ser nulo");
        for (ObservadorDeAcontecimientos o : List.copyOf(observadores)) {
            o.alOcurrir(acontecimiento);
        }
    }
}