package TorneoInmortales;

import java.util.ArrayList;
import java.util.List;

public class TorneoDeLosInmortales {

    // ==================== ESCUELAS (solo configuración) ====================

    private static final Escuela ESCUELA_DE_LA_ESPADA = new Escuela(
            "Escuela de la Espada Celestial",
            List.of(new CorteDeLosMilAmaneceres(), new RugidoDeLaBestiaPrimordial()),
            List.of(new EspadaCelestial()));

    private static final Escuela PALACIO_DEL_VACIO = new Escuela(
            "Palacio del Vacío",
            List.of(new NieblaVenenosaDelVacio(), new DominioDeLaNada(), new MeditacionDelInmortal()),
            List.of(new AnilloEspiritual()));

    private static final Escuela MONASTERIO_DEL_CUERPO_DORADO = new Escuela(
            "Monasterio del Cuerpo Dorado",
            List.of(new MuroDelCuerpoDorado(), new LotoRegenerador(), new MeditacionDelInmortal()),
            List.of(new ArmaduraDorada(), new SelloDelFenix()));

    // ==================== EQUIPOS DEL TORNEO ====================

    private record Perfil(TipoCombatiente tipo, Escuela escuela) {}

    private static final List<String> NOMBRES = List.of(
            "Aurelio", "Nébula", "Kael", "Darian", "Selene", "Thorne", "Lyra", "Orión",
            "Vesper", "Ámbar", "Ciro", "Daena", "Elián", "Fénix", "Galen", "Hestia");


    private static final List<List<Perfil>> EQUIPOS = List.of(
            List.of(new Perfil(TipoCombatiente.CULTIVADOR_ESPADA, ESCUELA_DE_LA_ESPADA),
                    new Perfil(TipoCombatiente.HECHICERO_VACIO, PALACIO_DEL_VACIO)),
            List.of(new Perfil(TipoCombatiente.MONJE_CUERPO_CELESTIAL, MONASTERIO_DEL_CUERPO_DORADO),
                    new Perfil(TipoCombatiente.CULTIVADOR_ESPADA, ESCUELA_DE_LA_ESPADA)),
            List.of(new Perfil(TipoCombatiente.HECHICERO_VACIO, PALACIO_DEL_VACIO),
                    new Perfil(TipoCombatiente.MONJE_CUERPO_CELESTIAL, MONASTERIO_DEL_CUERPO_DORADO)),
            List.of(new Perfil(TipoCombatiente.CULTIVADOR_ESPADA, ESCUELA_DE_LA_ESPADA),
                    new Perfil(TipoCombatiente.CULTIVADOR_ESPADA, ESCUELA_DE_LA_ESPADA)),
            List.of(new Perfil(TipoCombatiente.HECHICERO_VACIO, PALACIO_DEL_VACIO),
                    new Perfil(TipoCombatiente.HECHICERO_VACIO, PALACIO_DEL_VACIO)),
            List.of(new Perfil(TipoCombatiente.MONJE_CUERPO_CELESTIAL, MONASTERIO_DEL_CUERPO_DORADO),
                    new Perfil(TipoCombatiente.MONJE_CUERPO_CELESTIAL, MONASTERIO_DEL_CUERPO_DORADO)),
            List.of(new Perfil(TipoCombatiente.CULTIVADOR_ESPADA, PALACIO_DEL_VACIO),
                    new Perfil(TipoCombatiente.MONJE_CUERPO_CELESTIAL, ESCUELA_DE_LA_ESPADA)),
            List.of(new Perfil(TipoCombatiente.HECHICERO_VACIO, MONASTERIO_DEL_CUERPO_DORADO),
                    new Perfil(TipoCombatiente.CULTIVADOR_ESPADA, MONASTERIO_DEL_CUERPO_DORADO)));

    // ==================== PROGRAMA ====================

    public static void main(String[] args) {
        HeraldoImperial heraldo = new HeraldoImperial();

        escenarioPolimorfismo(heraldo);
        escenarioReglasRechazadas(heraldo);
        escenarioCombateConEfectos(heraldo);
        escenarioTorneoDeCuatroParejas(heraldo);
        escenarioTorneoDeOchoParejasEnSilencio(heraldo);
    }

    // ==================== ESCENARIO 1: POLIMORFISMO ====================

    private record Muestra(String tipo, Combatiente combatiente) {}

    private static void escenarioPolimorfismo(HeraldoImperial heraldo) {
        titulo(heraldo, "ESCENARIO 1: la diferencia entre tipos surge del polimorfismo");
        CreadorCombatientes creador = new CreadorCombatientes();
        Escuela sinEscuela = new Escuela("Sin escuela", List.of(), List.of());
        Tecnica corte = new CorteDeLosMilAmaneceres();

        List<Muestra> muestras = List.of(
                new Muestra("Cultivador de la Espada",
                        creador.crear(TipoCombatiente.CULTIVADOR_ESPADA, "Aurelio", sinEscuela)),
                new Muestra("Hechicero del Vacío",
                        creador.crear(TipoCombatiente.HECHICERO_VACIO, "Nébula", sinEscuela)),
                new Muestra("Monje del Cuerpo Celestial",
                        creador.crear(TipoCombatiente.MONJE_CUERPO_CELESTIAL, "Kael", sinEscuela)));
        Combatiente maniqui = creador.crear(TipoCombatiente.CULTIVADOR_ESPADA, "Maniquí", sinEscuela);

        heraldo.anunciar("Daño de " + corte.getNombre() + " sobre el mismo objetivo, según quién la ejecuta:");
        for (Muestra m : muestras) {
            maniqui.prepararParaCombate();
            corte.ejecutar(m.combatiente(), maniqui);
            heraldo.anunciar(String.format("  %-28s causa %d de daño",
                    m.tipo(), maniqui.getVidaMaxima() - maniqui.getVidaActual()));
        }

        final int golpe = 30;
        heraldo.anunciar("Un mismo golpe de " + golpe + " de daño, recibido por cada tipo:");
        for (Muestra m : muestras) {
            Combatiente c = m.combatiente();
            c.prepararParaCombate();
            c.recibirDanio(golpe);
            heraldo.anunciar(String.format("  %-28s pierde %d de vida",
                    m.tipo(), c.getVidaMaxima() - c.getVidaActual()));
        }

        heraldo.anunciar("Costo de energía de " + corte.getNombre() + " según el tipo:");
        for (Muestra m : muestras) {
            heraldo.anunciar(String.format("  %-28s gasta %d de energía",
                    m.tipo(), m.combatiente().costoDe(corte)));
        }
    }

    // ==================== ESCENARIO 2: REGLAS QUE SE RECHAZAN ====================

    private static void escenarioReglasRechazadas(HeraldoImperial heraldo) {
        titulo(heraldo, "ESCENARIO 2: las reglas se hacen cumplir");
        CreadorCombatientes creador = new CreadorCombatientes();
        Tecnica corte = new CorteDeLosMilAmaneceres();

        Combatiente kael = creador.crear(TipoCombatiente.MONJE_CUERPO_CELESTIAL, "Kael", MONASTERIO_DEL_CUERPO_DORADO);
        Equipamiento espada = new EspadaCelestial();
        Equipamiento anillo = new AnilloEspiritual();
        kael.agregarAlInventario(espada);
        kael.agregarAlInventario(anillo);
        heraldo.anunciar("Kael sale del Monasterio llevando " + kael.getEquipamientos().size() + " objetos");
        intentar(heraldo, "equipar la Espada celestial (tercer objeto)", () -> kael.equipar(espada));
        intentar(heraldo, "equipar el Anillo espiritual (cuarto objeto)", () -> kael.equipar(anillo));
        heraldo.anunciar("  Kael sigue llevando " + kael.getEquipamientos().size() + " objetos");

        Combatiente aurelio = creador.crear(TipoCombatiente.CULTIVADOR_ESPADA, "Aurelio", ESCUELA_DE_LA_ESPADA);
        Equipamiento otraEspada = new EspadaCelestial();
        aurelio.agregarAlInventario(otraEspada);
        intentar(heraldo, "equipar una segunda arma", () -> aurelio.equipar(otraEspada));
        heraldo.anunciar("  Aurelio sigue llevando " + aurelio.getEquipamientos().size() + " objeto");

        Combatiente nebula = creador.crear(TipoCombatiente.HECHICERO_VACIO, "Nébula", PALACIO_DEL_VACIO);
        Equipamiento sello = new SelloDelFenix();
        nebula.agregarAlInventario(sello);
        heraldo.anunciar(String.format("Nébula antes del Sello: vida %d/%d",
                nebula.getVidaActual(), nebula.getVidaMaxima()));
        nebula.equipar(sello);
        heraldo.anunciar(String.format("  Con el Sello puesto: vida %d/%d (el valor actual no sube)",
                nebula.getVidaActual(), nebula.getVidaMaxima()));
        nebula.prepararParaCombate();
        heraldo.anunciar(String.format("  Al comenzar un combate: vida %d/%d",
                nebula.getVidaActual(), nebula.getVidaMaxima()));
        nebula.desequipar(sello);
        heraldo.anunciar(String.format("  Al quitar el Sello: vida %d/%d (se recorta al nuevo máximo)",
                nebula.getVidaActual(), nebula.getVidaMaxima()));

        // --- Técnicas: energía y usos ---
        Combatiente selene = creador.crear(TipoCombatiente.CULTIVADOR_ESPADA, "Selene", ESCUELA_DE_LA_ESPADA);
        selene.perderEnergia(1000);
        intentar(heraldo, "usar " + corte.getNombre() + " sin energía", () -> selene.usarTecnica(corte));
        for (int i = 0; i < 5; i++) {
            selene.prepararParaCombate();
            selene.usarTecnica(corte);
        }
        selene.prepararParaCombate();
        heraldo.anunciar("Selene usó " + corte.getNombre() + " cinco veces; usos restantes: "
                + selene.usosRestantes(corte));
        intentar(heraldo, "usar " + corte.getNombre() + " una sexta vez, con energía de sobra",
                () -> selene.usarTecnica(corte));

        // --- Torneo: inscripciones y cantidad de parejas ---
        Torneo torneo = new Torneo();
        Combatiente c1 = creador.crear(TipoCombatiente.CULTIVADOR_ESPADA, "Thorne", ESCUELA_DE_LA_ESPADA);
        Combatiente c2 = creador.crear(TipoCombatiente.HECHICERO_VACIO, "Lyra", PALACIO_DEL_VACIO);
        Combatiente c3 = creador.crear(TipoCombatiente.MONJE_CUERPO_CELESTIAL, "Orión", MONASTERIO_DEL_CUERPO_DORADO);
        Pareja primera = new Pareja(1, c1, c2);
        torneo.inscribir(primera);
        intentar(heraldo, "inscribir dos veces la misma pareja", () -> torneo.inscribir(primera));
        intentar(heraldo, "inscribir a Lyra en una segunda pareja", () -> torneo.inscribir(new Pareja(2, c2, c3)));
        intentar(heraldo, "iniciar el torneo con una sola pareja", torneo::iniciar);
    }

    private static void intentar(HeraldoImperial heraldo, String intento, Runnable accion) {
        try {
            accion.run();
            heraldo.anunciar("  Permitido: " + intento);
        } catch (RuntimeException e) {
            heraldo.anunciar("  Rechazado (" + e.getClass().getSimpleName() + "): " + intento
                    + " -> " + e.getMessage());
        }
    }

    // ==================== ESCENARIO 3: EFECTOS TEMPORALES ====================

    private static void escenarioCombateConEfectos(HeraldoImperial heraldo) {
        titulo(heraldo, "ESCENARIO 3: un combate con veneno y regeneración (bonus)");
        CreadorCombatientes creador = new CreadorCombatientes();
        Pareja vacioYMonasterio = new Pareja(1,
                creador.crear(TipoCombatiente.HECHICERO_VACIO, "Nébula", PALACIO_DEL_VACIO),
                creador.crear(TipoCombatiente.MONJE_CUERPO_CELESTIAL, "Selene", MONASTERIO_DEL_CUERPO_DORADO));
        Pareja espadas = new Pareja(2,
                creador.crear(TipoCombatiente.CULTIVADOR_ESPADA, "Aurelio", ESCUELA_DE_LA_ESPADA),
                creador.crear(TipoCombatiente.CULTIVADOR_ESPADA, "Darian", ESCUELA_DE_LA_ESPADA));

        Emisor emisor = new Emisor();
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        emisor.suscribir(heraldo);
        emisor.suscribir(cronista);

        new Combate(vacioYMonasterio, espadas, 1, emisor).jugar();

        long venenos = cronista.acontecimientosDe(EfectoAplicado.class).stream()
                .filter(e -> e.efecto().equals(new Veneno().getNombre())).count();
        long regeneraciones = cronista.acontecimientosDe(EfectoAplicado.class).stream()
                .filter(e -> e.efecto().equals(new Regeneracion().getNombre())).count();
        heraldo.anunciar("El Cronista registró " + venenos + " ticks de Veneno y "
                + regeneraciones + " de Regeneración en este combate");
    }

    // ==================== ESCENARIO 4: TORNEO DE 4 PAREJAS ====================

    private static void escenarioTorneoDeCuatroParejas(HeraldoImperial heraldo) {
        titulo(heraldo, "ESCENARIO 4: torneo completo de 4 parejas");
        CronistaDeLaSecta cronista = new CronistaDeLaSecta();
        Torneo torneo = armarTorneo(4, heraldo, cronista);

        heraldo.anunciar("Comienza el Torneo de los Inmortales");
        torneo.iniciar();

        heraldo.presentarTabla(cronista.tablaDePosiciones());
    }

    // ==================== ESCENARIO 5: TORNEO DE 8 PAREJAS, EN SILENCIO ====================

    private static void escenarioTorneoDeOchoParejasEnSilencio(HeraldoImperial heraldo) {
        titulo(heraldo, "ESCENARIO 5: torneo de 8 parejas sin narración, jugado dos veces");
        CronistaDeLaSecta primero = new CronistaDeLaSecta();
        CronistaDeLaSecta segundo = new CronistaDeLaSecta();
        Torneo torneo1 = armarTorneo(8, primero);
        Torneo torneo2 = armarTorneo(8, segundo);

        torneo1.iniciar();
        torneo2.iniciar();

        heraldo.alOcurrir(primero.acontecimientosDe(Proclamacion.class).get(0));
        heraldo.presentarTabla(primero.tablaDePosiciones());
        heraldo.anunciar("Acontecimientos registrados por el Cronista: " + primero.getHistorial().size());
        heraldo.anunciar("Dos torneos armados igual: mismo campeón = "
                + (torneo1.getCampeona().getId() == torneo2.getCampeona().getId())
                + ", mismo historial = " + primero.getHistorial().equals(segundo.getHistorial()));
    }

    // ==================== AUXILIARES ====================

    private static Torneo armarTorneo(int cantidadDeParejas, ObservadorDeAcontecimientos... interesados) {
        Torneo torneo = new Torneo();
        for (ObservadorDeAcontecimientos interesado : interesados) {
            torneo.suscribir(interesado);
        }
        CreadorCombatientes creador = new CreadorCombatientes();
        int siguienteNombre = 0;
        for (int i = 0; i < cantidadDeParejas; i++) {
            List<Perfil> equipo = EQUIPOS.get(i);
            List<Combatiente> integrantes = new ArrayList<>();
            for (Perfil perfil : equipo) {
                integrantes.add(creador.crear(perfil.tipo(), NOMBRES.get(siguienteNombre++), perfil.escuela()));
            }
            torneo.inscribir(new Pareja(i + 1, integrantes.get(0), integrantes.get(1)));
        }
        return torneo;
    }

    private static void titulo(HeraldoImperial heraldo, String texto) {
        heraldo.anunciar("");
        heraldo.anunciar("########## " + texto + " ##########");
    }
}