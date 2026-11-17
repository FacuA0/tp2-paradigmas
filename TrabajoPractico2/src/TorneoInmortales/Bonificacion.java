package TorneoInmortales;
public interface Bonificacion {
    void aplicar(Combatiente c);
    void revertir(Combatiente c);
}