package TorneoInmortales;

public class BonusReduccionDanio implements Bonificacion {
    private final int valor;

    public BonusReduccionDanio(int valor) {
        this.valor = valor;
    }

    @Override
    public void aplicar(Combatiente c) {
        c.modificarReduccionDanio(valor);
    }

    @Override
    public void revertir(Combatiente c) {
        c.modificarReduccionDanio(-valor);
    }
    
    @Override
    public String toString() {
        return "{Bonus de defensa}";
    }
}