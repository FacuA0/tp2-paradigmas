package TorneoInmortales;
public class BonusVidaMaxima implements Bonificacion {
    private final int valor;

    public BonusVidaMaxima(int valor) {
        this.valor = valor;
    }

    @Override
    public void aplicar(Combatiente c) {
        c.modificarVidaMaxima(valor);
    }

    @Override
    public void revertir(Combatiente c) {
        c.modificarVidaMaxima(-valor);
    }
    
    @Override
    public String toString() {
        return "{Bonus de vida maxima}";
    }
}