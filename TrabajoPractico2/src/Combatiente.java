import java.util.ArrayList;
import java.util.List;
public abstract class Combatiente {

	private static int siguienteId = 1;
	private final int id;
    private String nombre;

    private int danio;
    
    private int vidaActual;
    private int vidaMaxima;

    private int energiaActual;
    private int energiaMaxima;


    private List<Equipamiento> inventario = new ArrayList<>();
    private List<Equipamiento> equipamientos = new ArrayList<>();
    private List<TecnicaDisponible> tecnicas = new ArrayList<>();
    
    public Combatiente(String nombre, int vidaMaxima, int energiaMaxima, int danio) {
    		this.id = siguienteId++;
            this.nombre = nombre;
            this.vidaMaxima = vidaMaxima;
            this.vidaActual = vidaMaxima;
            this.energiaMaxima = energiaMaxima;
            this.energiaActual = energiaMaxima;
            this.danio = danio;
        }
    
    public void aprenderTecnica(Tecnica tecnica) {
        tecnicas.add(new TecnicaDisponible(tecnica));
    }
    
    public void equipar(String nombreEquipamiento) {
        Equipamiento equipamiento = inventario.stream()
            .filter(e -> e.getNombre().equals(nombreEquipamiento))
            .findFirst()
            .orElseThrow(() ->
                new IllegalArgumentException("No posee ese equipamiento")
            );

        equipar(equipamiento);
    }
    
    public void equipar(Equipamiento equipamiento) {
        if (!inventario.contains(equipamiento)) {
            throw new IllegalArgumentException("El combatiente no posee este equipamiento");
        }

        if (equipamientos.contains(equipamiento)) {
            throw new IllegalArgumentException("Este equipamiento ya está equipado");
        }

        if (equipamientos.size() >= 3) {
            throw new IllegalArgumentException("El combatiente ya tiene 3 equipamientos");
        }

        if (tieneEquipadoDeEsaCategoria(equipamiento.getCategoria())) {
            throw new IllegalArgumentException("Ya tiene un equipamiento de esa categoría");
        }

        equipamientos.add(equipamiento);
        aplicarBonificaciones(equipamiento);
    }
    
    public void desequipar(String nombreEquipamiento) {
        Equipamiento equipamiento = inventario.stream()
            .filter(e -> e.getNombre().equals(nombreEquipamiento))
            .findFirst()
            .orElseThrow(() ->
                new IllegalArgumentException("No posee ese equipamiento")
            );

        desequipar(equipamiento);
    }
    
    public void desequipar(Equipamiento equipamiento) {
        if (!equipamientos.contains(equipamiento)) {
            throw new IllegalArgumentException("El combatiente no tiene equipado este objeto");
        }

        equipamientos.remove(equipamiento);
        revertirBonificaciones(equipamiento);
    }
    
    private void aplicarBonificaciones(Equipamiento equipamiento) {
        for (Bonificacion bonificacion : equipamiento.getBonificaciones()) {
            switch (bonificacion.getTipo()) {
                case TipoBonificacion.DANIO:
                    danio += bonificacion.getValor();
                    break;

                case TipoBonificacion.VIDA_MAXIMA:
                    vidaMaxima += bonificacion.getValor();
                    break;

                case TipoBonificacion.ENERGIA_MAXIMA:
                    energiaMaxima += bonificacion.getValor();
                    break;
            }
        }
    }

    private void revertirBonificaciones(Equipamiento equipamiento) {
        for (Bonificacion bonificacion : equipamiento.getBonificaciones()) {
            switch (bonificacion.getTipo()) {
                case TipoBonificacion.DANIO:
                    danio -= bonificacion.getValor();
                    break;

                case TipoBonificacion.VIDA_MAXIMA:
                    vidaMaxima -= bonificacion.getValor();

                    if (vidaActual > vidaMaxima) {
                        vidaActual = vidaMaxima;
                    }
                    break;

                case TipoBonificacion.ENERGIA_MAXIMA:
                    energiaMaxima -= bonificacion.getValor();

                    if (energiaActual > energiaMaxima) {
                        energiaActual = energiaMaxima;
                    }
                    break;
            }
        }
    }
    
    private boolean tieneEquipadoDeEsaCategoria(TipoEquipamiento categoria) {
        for (Equipamiento e : equipamientos) {
            if (e.getCategoria() == categoria) {
                return true;
            }
        }

        return false;
    }

    public void agregarAlInventario(Equipamiento equipamiento) {
        inventario.add(equipamiento);
    }

    
    public List<Equipamiento> getEquipamientos() {
    	return equipamientos;
    }
    
    public abstract int modificarDanioInfligido(int danio);

    public abstract int modificarDanioRecibido(int danio);

    public abstract int modificarCostoTecnica(int costo);
    
    @Override
    public String toString() {
        return "Combatiente {" +
                "id:" + id +
                ", Nombre:" + nombre + 
                ", Daño:" + danio + 
                ", vida=" + vidaActual + '/' + vidaMaxima +
                ", energia=" + energiaActual + '/' + energiaMaxima +
                ", tecnicas=" + tecnicas +
                ", equipamientos=" + equipamientos +
                '}';
    }
}
