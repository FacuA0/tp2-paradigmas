
public class TecnicaDisponible {

	private Tecnica tecnica;
    private int usosRestantes;
    private int usosTotalesTorneo;

    public TecnicaDisponible(Tecnica tecnica) {
        this.tecnica = tecnica;
        this.usosRestantes = tecnica.getUsosMaximos();
        this.usosTotalesTorneo = tecnica.getUsosMaximos();
    }
    
    @Override
    public String toString() {
        return "{" + tecnica.getNombre() +
                ", Usos restantes:" + usosRestantes +
                ", Usos restantes en el torneo:" + usosTotalesTorneo +
                '}';
    }
}
