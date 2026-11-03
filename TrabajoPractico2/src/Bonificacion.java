public class Bonificacion {

    private TipoBonificacion tipo;
    private int valor;

    public TipoBonificacion getTipo() {
		return tipo;
	}

	public int getValor() {
		return valor;
	}

	public Bonificacion(TipoBonificacion tipo, int valor) {
        this.tipo = tipo;
        this.valor = valor;
    }
    
    @Override
    public String toString() {
        return "{" + tipo +
                ":" + valor
                ;
    }
}