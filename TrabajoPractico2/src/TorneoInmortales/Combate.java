package TorneoInmortales;

import java.util.Comparator;
import java.util.Objects;

public class Combate {

    public static final int LIMITE_ASALTOS_POR_DEFECTO = 20;
    public static final int ENERGIA_POR_ASALTO_POR_DEFECTO = 10;
    private static final String ATAQUE_BASICO = "Ataque básico";

    private final Pareja primera;
    private final Pareja segunda;
    private final int ronda;
    private final Emisor emisor;
    private final int limiteAsaltos;
    private final int energiaPorAsalto;
    private Pareja ganadora;
    private int asaltosJugados;

    public Combate(Pareja a, Pareja b, int ronda, Emisor emisor) {
        this(a, b, ronda, emisor, LIMITE_ASALTOS_POR_DEFECTO, ENERGIA_POR_ASALTO_POR_DEFECTO);
    }

    public Combate(Pareja a, Pareja b, int ronda, Emisor emisor, int limiteAsaltos, int energiaPorAsalto) {
        Objects.requireNonNull(a, "La primera pareja no puede ser nula");
        Objects.requireNonNull(b, "La segunda pareja no puede ser nula");
        Objects.requireNonNull(emisor, "El emisor no puede ser nulo");
        if (a.equals(b)) {
            throw new IllegalArgumentException("Una pareja no puede combatir contra sí misma");
        }
        for (Combatiente c : a.getIntegrantes()) {
            if (b.contiene(c)) {
                throw new IllegalArgumentException(c.getNombre() + " está en las dos parejas");
            }
        }
        if (limiteAsaltos < 1) {
            throw new IllegalArgumentException("El límite de asaltos debe ser al menos 1");
        }
        if (energiaPorAsalto < 0) {
            throw new IllegalArgumentException("La energía por asalto no puede ser negativa");
        }
        boolean aPrimero = a.getId() < b.getId();
        this.primera = aPrimero ? a : b;
        this.segunda = aPrimero ? b : a;
        this.ronda = ronda;
        this.emisor = emisor;
        this.limiteAsaltos = limiteAsaltos;
        this.energiaPorAsalto = energiaPorAsalto;
    }

    // ==================== DESARROLLO ====================

    public Pareja jugar() {
        if (ganadora != null) {
            throw new IllegalStateException("El combate ya se jugó");
        }
        primera.prepararParaCombate();
        segunda.prepararParaCombate();
        emisor.publicar(new InicioDeCombate(ronda, primera.getId(), primera.nombres(), segunda.getId(), segunda.nombres()));

        while (asaltosJugados < limiteAsaltos && primera.sigueEnPie() && segunda.sigueEnPie()) {
            asaltosJugados++;
            jugarAsalto(asaltosJugados);
        }

        ganadora = determinarGanadora();
        Pareja perdedora = ganadora.equals(primera) ? segunda : primera;
        emisor.publicar(new ResultadoDeCombate(ronda, ganadora.getId(), ganadora.nombres(), ganadora.vidaTotal(), perdedora.getId(), perdedora.nombres(), perdedora.vidaTotal()));
        return ganadora;
    }

    private void jugarAsalto(int asalto) {
        recuperarEnergia(primera);
        recuperarEnergia(segunda);
        jugarTurno(asalto, primera, segunda);
        jugarTurno(asalto, segunda, primera);
    }

    private void recuperarEnergia(Pareja pareja) {
        for (Combatiente c : pareja.integrantesEnPie()) {
            c.recuperarEnergia(energiaPorAsalto);
        }
    }

    private void jugarTurno(int asalto, Pareja propia, Pareja rival) {
        for (Combatiente c : propia.getIntegrantes()) {
            if (!propia.sigueEnPie() || !rival.sigueEnPie()) {
                return;
            }
            if (!c.estaEnPie()) {
                continue;
            }
            procesarEfectos(asalto, c);
            if (c.estaEnPie()) {
                actuar(asalto, c, propia, rival);
            }
        }
    }

    private void procesarEfectos(int asalto, Combatiente c) {
        for (EfectoTemporal efecto : c.getEfectos()) {
            if (!c.estaEnPie()) {
                break;
            }
            c.procesarEfecto(efecto);
            emisor.publicar(new EfectoAplicado(ronda, asalto,
                    c.getId(), c.getNombre(), efecto.getNombre(), c.getVidaActual()));
        }
        if (!c.estaEnPie()) {
            emisor.publicar(new Eliminacion(ronda, c.getId(), c.getNombre()));
        }
    }
    
    // ==================== ACCIÓN ====================

    private void actuar(int asalto, Combatiente c, Pareja propia, Pareja rival) {
        Tecnica tecnica = elegirTecnica(c, propia);

        if (tecnica == null) {
            Combatiente objetivo = elegirObjetivo(rival);
            objetivo.recibirDanio(c.danioAtaqueBasico());
            informar(asalto, c, ATAQUE_BASICO, false, objetivo);
        } else if (tecnica.getCategoria() == TipoTecnica.OFENSIVA) {
            Combatiente objetivo = elegirObjetivo(rival);
            propia.usarTecnica(c, tecnica);
            tecnica.ejecutar(c, objetivo);
            informar(asalto, c, tecnica.getNombre(), true, objetivo);
        } else {
            propia.usarTecnica(c, tecnica);
            tecnica.ejecutar(c, c);
            informar(asalto, c, tecnica.getNombre(), true, c);
        }
    }

    private Tecnica elegirTecnica(Combatiente c, Pareja propia) {
        if (c.getVidaActual() < c.getVidaMaxima()) {
            Tecnica t = primeraPosible(c, propia, TipoTecnica.RECUPERACION);
            if (t != null) return t;
        }
        if (!c.tieneDefensaActiva()) {
            Tecnica t = primeraPosible(c, propia, TipoTecnica.DEFENSIVA);
            if (t != null) return t;
        }
        return ofensivaConMasUsos(c, propia);
    }

    private Tecnica primeraPosible(Combatiente c, Pareja propia, TipoTecnica categoria) {
        for (Tecnica t : c.getTecnicas()) {
            if (t.getCategoria() == categoria && propia.puedeUsar(c, t)) {
                return t;
            }
        }
        return null;
    }

    private Tecnica ofensivaConMasUsos(Combatiente c, Pareja propia) {
        Tecnica mejor = null;
        for (Tecnica t : c.getTecnicas()) {
            if (t.getCategoria() == TipoTecnica.OFENSIVA && propia.puedeUsar(c, t) && (mejor == null || c.usosRestantes(t) > c.usosRestantes(mejor))) {
                mejor = t;
            }
        }
        return mejor;
    }

    private Combatiente elegirObjetivo(Pareja rival) {
        return rival.integrantesEnPie().stream()
                .min(Comparator.comparingInt(Combatiente::getVidaActual)
                        .thenComparingInt(Combatiente::getId))
                .orElseThrow();
    }

    private void informar(int asalto, Combatiente ejecutor, String accion,
                          boolean esTecnica, Combatiente objetivo) {
        emisor.publicar(new AccionRealizada(ronda, asalto,
                ejecutor.getId(), ejecutor.getNombre(), accion, esTecnica,
                objetivo.getId(), objetivo.getNombre(), objetivo.getVidaActual()));
        if (!objetivo.estaEnPie()) {
            emisor.publicar(new Eliminacion(ronda, objetivo.getId(), objetivo.getNombre()));
        }
    }

    // ==================== RESULTADO ====================

    private Pareja determinarGanadora() {
        if (!primera.sigueEnPie()) return segunda;
        if (!segunda.sigueEnPie()) return primera;
        if (primera.vidaTotal() != segunda.vidaTotal()) {
            return primera.vidaTotal() > segunda.vidaTotal() ? primera : segunda;
        }
        return primera;
    }

    public Pareja getGanadora() { return ganadora; }

    public int getAsaltosJugados() { return asaltosJugados; }
}