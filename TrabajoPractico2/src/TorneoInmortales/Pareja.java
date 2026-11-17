package TorneoInmortales;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Pareja {

    private final int id;
    private final List<Combatiente> integrantes;
    private final Map<Tecnica, Combatiente> tecnicaEnUso = new LinkedHashMap<>();

    public Pareja(int id, Combatiente uno, Combatiente dos) {
        Objects.requireNonNull(uno, "El primer integrante no puede ser nulo");
        Objects.requireNonNull(dos, "El segundo integrante no puede ser nulo");
        if (uno.equals(dos)) {
            throw new IllegalArgumentException("Una pareja necesita dos combatientes distintos");
        }
        this.id = id;
        this.integrantes = uno.getId() < dos.getId() ? List.of(uno, dos) : List.of(dos, uno);
    }

    // ==================== CONSULTAS ====================

    public int getId() { return id; }

    public String nombres() {
        return integrantes.get(0).getNombre() + " y " + integrantes.get(1).getNombre();
    }
    
    public List<Combatiente> getIntegrantes() { return integrantes; }

    public boolean contiene(Combatiente c) { return integrantes.contains(c); }

    public Combatiente companeroDe(Combatiente c) {
        exigirIntegrante(c);
        return integrantes.get(0).equals(c) ? integrantes.get(1) : integrantes.get(0);
    }

    public List<Combatiente> integrantesEnPie() {
        return integrantes.stream().filter(Combatiente::estaEnPie).toList();
    }

    public boolean sigueEnPie() {
        return integrantes.stream().anyMatch(Combatiente::estaEnPie);
    }

    public int vidaTotal() {
        return integrantes.stream().mapToInt(Combatiente::getVidaActual).sum();
    }

    // ==================== REGLA DEL COMPAÑERO ====================

    public boolean puedeUsar(Combatiente c, Tecnica t) {
        exigirIntegrante(c);
        return c.puedeUsar(t) && !bloqueadaPorCompanero(c, t);
    }

    public void usarTecnica(Combatiente c, Tecnica t) {
        exigirIntegrante(c);
        if (bloqueadaPorCompanero(c, t)) {
            throw TecnicaNoDisponibleException.usadaPorCompanero(c.getNombre(), t.getNombre());
        }
        c.usarTecnica(t);
        tecnicaEnUso.putIfAbsent(t, c);
    }

    private boolean bloqueadaPorCompanero(Combatiente c, Tecnica t) {
        Combatiente primero = tecnicaEnUso.get(t);
        return primero != null
                && !primero.equals(c)
                && primero.estaEnPie()
                && primero.usosRestantes(t) > 0;
    }

    // ==================== CICLO DEL COMBATE ====================

    public void prepararParaCombate() {
        integrantes.forEach(Combatiente::prepararParaCombate);
        tecnicaEnUso.clear();
    }

    private void exigirIntegrante(Combatiente c) {
        if (!contiene(c)) {
            throw new IllegalArgumentException(c.getNombre() + " no pertenece a la pareja " + id);
        }
    }

    // ==================== OVERRIDE ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pareja)) return false;
        return id == ((Pareja) o).id;
    }

    @Override
    public int hashCode() { return Integer.hashCode(id); }

    @Override
    public String toString() {
        return "Pareja " + id + " " + integrantes;
    }
}