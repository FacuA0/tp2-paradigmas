package TorneoInmortales;
public class TecnicaNoDisponibleException extends RuntimeException {

    public TecnicaNoDisponibleException(String mensaje) {
        super(mensaje);
    }

    public static TecnicaNoDisponibleException noLaConoce(String combatiente, String tecnica) {
        return new TecnicaNoDisponibleException(
            combatiente + " no conoce la técnica " + tecnica);
    }

    public static TecnicaNoDisponibleException sinEnergia(String combatiente, String tecnica) {
        return new TecnicaNoDisponibleException(
            combatiente + " no tiene energía suficiente para " + tecnica);
    }

    public static TecnicaNoDisponibleException sinUsos(String combatiente, String tecnica) {
        return new TecnicaNoDisponibleException(
            combatiente + " no tiene usos restantes de " + tecnica);
    }
    
    public static TecnicaNoDisponibleException usadaPorCompanero(String combatiente, String tecnica) {
        return new TecnicaNoDisponibleException(
            combatiente + " no puede usar " + tecnica + ": su compañero la está usando");
    }
}