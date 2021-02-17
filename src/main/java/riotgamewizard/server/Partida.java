package riotgamewizard.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Partida implements Serializable{

	private static final long serialVersionUID = 1L;

	private String id;
	private Jugador anfitrion;
	private List<Jugador> jugadores;
	private String tipoPartida;

	public Partida(String id, Jugador anfitrion, String tipoPartida) {
		this.anfitrion  = anfitrion;
		this.id = id;
		this.tipoPartida = tipoPartida;
		this.jugadores = new ArrayList<>();
		this.jugadores.add(anfitrion);
	}

	//jugador invitado
	public void annadirJugador(Jugador jugador) {
		this.jugadores.add(jugador);
	}

	public void annadirTipoPartida(TipoPartida tipo) {
		this.tipoPartida = tipo.getTipoPartida();
	}

	public String getTipoPartida() {
		return tipoPartida;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Jugador getAnfitrion() {
		return anfitrion;
	}

	public void setAnfitrion(Jugador anfitrion) {
		this.anfitrion = anfitrion;
	}

	public List<Jugador> getJugadores() {
		return jugadores;
	}

	public void setJugadores(List<Jugador> jugadores) {
		this.jugadores = jugadores;
	}



}
