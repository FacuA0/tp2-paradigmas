package TorneoInmortales;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Torneo {

   
    public record Cruce(Pareja primera, Pareja segunda, Pareja ganadora) {}

    private final Emisor emisor = new Emisor();
    private final Set<Pareja> inscriptas = new LinkedHashSet<>();
    private final Set<Combatiente> combatientesInscriptos = new LinkedHashSet<>();
    private final List<List<Cruce>> rondas = new ArrayList<>();
    private Pareja campeona;
    private boolean iniciado = false;

    // ==================== INTERESADOS ====================

    public void suscribir(ObservadorDeAcontecimientos observador) {
        emisor.suscribir(observador);
    }

    // ==================== INSCRIPCIÓN ====================

    public void inscribir(Pareja pareja) {
        Objects.requireNonNull(pareja, "La pareja no puede ser nula");
        if (iniciado) {
            throw new IllegalStateException("El torneo ya comenzó: no se admiten más inscripciones");
        }
        if (inscriptas.contains(pareja)) {
            throw TorneoInvalidoException.parejaRepetida(pareja.getId());
        }
        for (Combatiente c : pareja.getIntegrantes()) {
            if (combatientesInscriptos.contains(c)) {
                throw TorneoInvalidoException.combatienteRepetido(c.getNombre());
            }
        }
        inscriptas.add(pareja);
        combatientesInscriptos.addAll(pareja.getIntegrantes());
    }

    // ==================== DESARROLLO ====================

    public void iniciar() {
        if (iniciado) {
            throw new IllegalStateException("El torneo ya se jugó");
        }
        int cantidad = inscriptas.size();
        if (cantidad != 4 && cantidad != 8) {
            throw TorneoInvalidoException.cantidadInvalida(cantidad);
        }
        iniciado = true;

        List<Pareja> enCompetencia = new ArrayList<>(inscriptas);
        int ronda = 1;
        while (enCompetencia.size() > 1) {
            List<Cruce> cruces = new ArrayList<>();
            List<Pareja> ganadoras = new ArrayList<>();
            for (int i = 0; i < enCompetencia.size(); i += 2) {
                Pareja a = enCompetencia.get(i);
                Pareja b = enCompetencia.get(i + 1);
                Pareja ganadora = new Combate(a, b, ronda, emisor).jugar();
                cruces.add(new Cruce(a, b, ganadora));
                ganadoras.add(ganadora);
            }
            rondas.add(List.copyOf(cruces));
            enCompetencia = ganadoras;
            ronda++;
        }

        campeona = enCompetencia.get(0);
        List<Combatiente> santos = campeona.getIntegrantes();
        emisor.publicar(new Proclamacion(campeona.getId(),
                santos.get(0).getNombre(), santos.get(1).getNombre()));
    }

    // ==================== CONSULTAS ====================

    public List<Pareja> getInscriptas() { return List.copyOf(inscriptas); }

    public List<List<Cruce>> getRondas() { return List.copyOf(rondas); }

    public Pareja getCampeona() { return campeona; }
}