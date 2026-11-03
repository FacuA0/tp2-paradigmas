import java.util.List;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
	    // La creación de combatientes está centralizada.
		Escuela escuelaDeLaEspada = new Escuela(

			      "Escuela de la Espada Celestial",

			      List.of(new CorteDeLosMilAmaneceres()),

			      List.of(new EspadaCelestial()));
		
		Escuela palacioDelVacio = new Escuela("Palacio del Vacío",

			      List.of(new DominioDeLaNada(), new MeditacionDelInmortal()),

			      List.of(new AnilloEspiritual()));
		
		Escuela monasterioDelCuerpoDorado = new Escuela("Monasterio del Cuerpo Dorado",

			      List.of(new MuroDelCuerpoDorado(), new MeditacionDelInmortal()),

			      List.of(new SelloDelFenix(),new ArmaduraDorada()));
		TipoCombatiente tipo = null;		
	    CreadorCombatientes creador = new CreadorCombatientes();

	    Combatiente aurelio = creador.crear( tipo.CULTIVADOR_ESPADA, "Aurelio", escuelaDeLaEspada);

	    Combatiente nebula = creador.crear(tipo.HECHICERO_VACIO, "Nébula", palacioDelVacio);

	    Combatiente kael = creador.crear(tipo.MONJE_CUERPO_CELESTIAL, "Kael", palacioDelVacio);

	    Combatiente darian = creador.crear(tipo.CULTIVADOR_ESPADA, "Darian", monasterioDelCuerpoDorado);
	   
	    System.out.println(aurelio);
	    aurelio.equipar("Espada celestial");
	    System.out.println(aurelio);
	    System.out.println(nebula);
	    nebula.equipar("Anillo espiritual");
	    System.out.println(nebula);
	    System.out.println(kael);
	    System.out.println(darian);
	    darian.equipar("Sello del fenix");
	    darian.equipar("Armadura dorada");
	    System.out.println(darian);
	}
}
