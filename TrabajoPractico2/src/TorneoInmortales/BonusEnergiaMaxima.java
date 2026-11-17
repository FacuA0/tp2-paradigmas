package TorneoInmortales;

public class BonusEnergiaMaxima implements Bonificacion {
    private final int valor;

    public BonusEnergiaMaxima(int valor) {
        this.valor = valor;
    }

    @Override
    public void aplicar(Combatiente c) {
        c.modificarEnergiaMaxima(valor);
    }

    @Override
    public void revertir(Combatiente c) {
        c.modificarEnergiaMaxima(-valor);
    }
    
    @Override
    public String toString() {
        return "{Bonus de energia maxima}";
    }
}