package riotgamewizard.server;

import java.util.ArrayList;
import java.util.List;

public class Partida {
	
	private String id;
	private Jugador anfitrion;
	private List<Jugador> jugadores;

	public Partida(String id, Jugador anfitrion) {
		this.anfitrion  = anfitrion;
		this.id = id;
		this.jugadores = new ArrayList<>();
		this.jugadores.add(anfitrion);
	}
	
	//jugador invitado
	public void annadirJugador(Jugador jugador) {
		this.jugadores.add(jugador);
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
