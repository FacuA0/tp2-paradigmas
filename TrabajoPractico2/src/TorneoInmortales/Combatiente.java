package TorneoInmortales;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
public abstract class Combatiente {
	
	// ==================== ATRIBUTOS ====================
	public static final int VIDA_BASE = 100;
	public static final int ENERGIA_BASE = 40;
	public static final int DANIO_BASICO_BASE = 15;
	private static final int USOS_POR_TORNEO = 5;
	private final int id;
    private final String nombre;
    private final int danioBasico;
    private int bonusDanio = 0;
    private int armadura = 0;
    private int defensaActiva = 0;
    private int vidaActual;
    private int vidaMaxima;
    private int energiaActual;
    private int energiaMaxima;
    private List<Equipamiento> inventario = new ArrayList<>();
    private List<Equipamiento> equipamientos = new ArrayList<>();
    private final Map<Tecnica, Integer> usosRestantes = new LinkedHashMap<>();
    private final Map<EfectoTemporal, Integer> efectos = new LinkedHashMap<>();
    
    // ==================== CONSTRUCTOR ====================
    
    public Combatiente(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        this.vidaMaxima = VIDA_BASE;
        this.vidaActual = VIDA_BASE;
        this.energiaMaxima = ENERGIA_BASE;
        this.energiaActual = ENERGIA_BASE;
        this.danioBasico = DANIO_BASICO_BASE;
    }
    
    // ==================== TÉCNICAS ====================
    
    public void aprenderTecnica(Tecnica tecnica) {
        Objects.requireNonNull(tecnica, "La técnica no puede ser nula");

        if (usosRestantes.containsKey(tecnica)) {
            throw new IllegalArgumentException(nombre + " ya conoce la técnica " + tecnica.getNombre());
        }
        usosRestantes.put(tecnica, USOS_POR_TORNEO);
    }
    
    public int usosRestantes(Tecnica t) {
        return usosRestantes.getOrDefault(t, 0);
    }

    public boolean puedeUsar(Tecnica t) {
        return usosRestantes(t) > 0 && energiaActual >= costoDe(t);
    }

    public int costoDe(Tecnica t) {
        return Math.max(0, modificarCostoTecnica(t.getCostoEnergia()));
    }
    
	// ==================== EQUIPAMIENTO ====================

    public void equipar(Equipamiento equipamiento) {
        if (!inventario.contains(equipamiento)) {
        	throw EquipamientoInvalidoException.noPosee(nombre,equipamiento.getNombre());
        }

        if (equipamientos.contains(equipamiento)) {
        	throw EquipamientoInvalidoException.yaEquipado(nombre,equipamiento.getNombre());
        }

        if (equipamientos.size() >= 3) {
        	throw EquipamientoInvalidoException.limiteAlcanzado(nombre);
        }

        if (tieneEquipadoDeEsaCategoria(equipamiento.getCategoria())) {
        	throw EquipamientoInvalidoException.categoriaRepetida(nombre,equipamiento.getCategoria().name());
        }

        equipamientos.add(equipamiento);
        aplicarBonificaciones(equipamiento);
    }
    
    public void desequipar(Equipamiento equipamiento) {
        if (!equipamientos.contains(equipamiento)) {
        	throw EquipamientoInvalidoException.noEquipado(nombre,equipamiento.getNombre());
        }

        equipamientos.remove(equipamiento);
        revertirBonificaciones(equipamiento);
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
    	return Collections.unmodifiableList(equipamientos);
    }
    
    // ==================== MODIFICADORES ====================
    
    private void aplicarBonificaciones(Equipamiento e) {
        for (Bonificacion b : e.getBonificaciones()) {
            b.aplicar(this);
        }
    }

    private void revertirBonificaciones(Equipamiento e) {
        for (Bonificacion b : e.getBonificaciones()) {
            b.revertir(this);
        }
    }
    
    void modificarVidaMaxima(int vida) {
        vidaMaxima += vida;
        if (vidaActual > vidaMaxima) vidaActual = vidaMaxima;
    }
    void modificarEnergiaMaxima(int energia) {
        energiaMaxima += energia;
        if (energiaActual > energiaMaxima) energiaActual = energiaMaxima;
    }
    void modificarBonusDanio(int danio) {
        bonusDanio += danio;
    }
    
    void modificarReduccionDanio(int defensa) {
    	armadura += defensa;
    }

    public void activarDefensa(int reduccion) { 
        defensaActiva = reduccion;
    }
    
    public abstract int modificarDanioInfligido(int danio);

    public abstract int modificarDanioRecibido(int danio);

    public abstract int modificarCostoTecnica(int costo);
    
 // ==================== EFECTOS TEMPORALES ====================

    public void aplicarEfecto(EfectoTemporal efecto) {
        Objects.requireNonNull(efecto, "El efecto no puede ser nulo");
        efectos.remove(efecto);
        efectos.put(efecto, efecto.getDuracion());
    }

    public void procesarEfecto(EfectoTemporal efecto) {
        Integer restantes = efectos.get(efecto);
        if (restantes == null || !estaEnPie()) {
            return;
        }
        efecto.alComenzarTurno(this);
        if (restantes <= 1) {
            efectos.remove(efecto);
        } else {
            efectos.put(efecto, restantes - 1);
        }
    }

    public List<EfectoTemporal> getEfectos() { return List.copyOf(efectos.keySet()); }

    public boolean tieneEfecto(EfectoTemporal efecto) { return efectos.containsKey(efecto); }

    public int turnosRestantesDe(EfectoTemporal efecto) { return efectos.getOrDefault(efecto, 0); }

    public void perderVida(int cantidad) {
        vidaActual = Math.max(0, vidaActual - cantidad);
    }
    
    // ==================== ACCIONES ====================
    
    public void recibirDanio(int danio) {
        int armaduraTotal = armadura + defensaActiva;
        int danioReal = modificarDanioRecibido(danio) * 100 / (100 + armaduraTotal);
        vidaActual = Math.max(0, vidaActual - danioReal);
    }
    
    public int danioCausado(int base) {
    	return modificarDanioInfligido(base + bonusDanio);
    }

    public int danioAtaqueBasico() {
        return danioCausado(danioBasico);
    }
    
    public void usarTecnica(Tecnica t) {
        if (!usosRestantes.containsKey(t)) {
            throw TecnicaNoDisponibleException.noLaConoce(nombre, t.getNombre());
        }
        if (usosRestantes(t) <= 0) {
            throw TecnicaNoDisponibleException.sinUsos(nombre, t.getNombre());
        }
        int costo = costoDe(t);
        if (energiaActual < costo) {
            throw TecnicaNoDisponibleException.sinEnergia(nombre, t.getNombre());
        }
        energiaActual -= costo;
        usosRestantes.merge(t, -1, Integer::sum);
    }
    
    public void curar(int cantidad, Combatiente c) {
        c.recibirCuracion(cantidad);
    }
    
    public void recibirCuracion(int cantidad) {
        vidaActual = Math.min(vidaMaxima, vidaActual + cantidad);
    }
    
    public void perderEnergia(int cantidad) {
        energiaActual = Math.max(0, energiaActual - cantidad);
    }

    public void recuperarEnergia(int cantidad) {
        energiaActual = Math.min(energiaMaxima, energiaActual + cantidad);
    }

    public void prepararParaCombate() {
        vidaActual = vidaMaxima;
        energiaActual = energiaMaxima;
        defensaActiva = 0;
        efectos.clear();    
    }
    
    // ==================== GETTERS SETTERS ====================
    
    public int getId() { return id; }
    
    public List<Tecnica> getTecnicas() {
        return List.copyOf(usosRestantes.keySet());
    }

    public String getNombre() {
		return nombre;
	}

	public int getVidaActual() {
		return vidaActual;
	}

	public int getVidaMaxima() {
		return vidaMaxima;
	}

	public int getEnergiaActual() {
		return energiaActual;
	}

	public int getEnergiaMaxima() {
		return energiaMaxima;
	}

	public boolean estaEnPie() { return vidaActual > 0; }
    
	public boolean tieneDefensaActiva() {return defensaActiva > 0;};
    // ==================== OVERRIDE ====================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Combatiente)) return false;
        return id == ((Combatiente) o).id;
    }

    @Override
    public int hashCode() { return Integer.hashCode(id); }
    
    @Override
    public String toString() {
        return "Combatiente {" +
                "id:" + id +
                ", Nombre:" + nombre + 
                ", Daño:" + danioBasico + 
                ", vida=" + vidaActual + '/' + vidaMaxima +
                ", energia=" + energiaActual + '/' + energiaMaxima +
                ", tecnicas=" + getTecnicas() +
                ", equipamientos=" + equipamientos +
                '}';
    }
}
