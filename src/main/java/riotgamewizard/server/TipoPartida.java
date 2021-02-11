package pasardatos;

public enum TipoPartida {

	DADOS("DADOS", 2),
	PARCHIS("PARCHIS", 4);

	private String tipoPartida;
	private int maxJugadores;

	private TipoPartida(String tipoPartida, int maxJugadores){
		this.tipoPartida = tipoPartida;
		this.maxJugadores = maxJugadores;
	}

	public String getTipoPartida(){
		return tipoPartida;
	}

	public int getMaxJugadores(){
		return  maxJugadores;
	}

}
