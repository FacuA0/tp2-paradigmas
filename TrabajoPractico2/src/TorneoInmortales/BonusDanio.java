package TorneoInmortales;

public class BonusDanio implements Bonificacion {
    private final int valor;

    public BonusDanio(int valor) {
        this.valor = valor;
    }

    @Override
    public void aplicar(Combatiente c) {
        c.modificarBonusDanio(valor);
    }

    @Override
    public void revertir(Combatiente c) {
        c.modificarBonusDanio(-valor);
    }
    
    @Override
    public String toString() {
        return "{Bonus de daño}";
    }
}