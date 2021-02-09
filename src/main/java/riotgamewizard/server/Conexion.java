package riotgamewizard.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class Conexion extends Thread {

	private Socket socket;
	
	public Conexion (Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		//recibir datos en csv "newGame, nombre, tipoPartida"
		try(InputStream input = socket.getInputStream();
				InputStreamReader inputReader = new InputStreamReader(input);
				BufferedReader br = new BufferedReader(inputReader)){
			String linea = br.readLine();
			String[] datos = linea.split(",");
			if(datos.length == 3) {
				procesarPartida(datos);
			}else {
				finalizarPartida(datos[1]);
			}
	
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	//elimina partida y jugadores
	private void finalizarPartida(String id) {
		for (Partida partida : Servidor.partidas) {
			if(partida.getId() == id) {
				List<Jugador> jugadores = partida.getJugadores();
				for (Jugador jugador : jugadores) {
					Servidor.jugadores.remove(jugador);
				}
				break;
			}
		}	
	}

	
	private synchronized void procesarPartida(String[] datos) {
		Jugador jugador = new Jugador();
		jugador.setNombre(datos[1]);
		jugador.setDireccion(socket.getInetAddress().getHostAddress());
		jugador.setPuerto(socket.getPort());
		if(Servidor.partidas.isEmpty()) {
			jugador.setEsAnfitrion(true);
			crearPartida(jugador);
		}else if(Servidor.partidas.get(Servidor.partidas.size()-1).getJugadores().size() == 1) {
			jugador.setEsAnfitrion(false);
			//a�adir jugador invitado
			Servidor.partidas.get(Servidor.partidas.size()-1).annadirJugador(jugador);
			mandarInfoPartida(Servidor.partidas.get(Servidor.partidas.size()-1));
		}else {
			jugador.setEsAnfitrion(true);
			crearPartida(jugador);
		}
		
	}
	
	//mandar partida, informacion de los jugadores.
	private void mandarInfoPartida(Partida partida) {
		try(ObjectOutputStream flujoDatos = new ObjectOutputStream(socket.getOutputStream());){
			flujoDatos.writeObject(partida);
			Socket socketAnfitrion = new Socket(partida.getAnfitrion().getDireccion(), partida.getAnfitrion().getPuerto());
			try(ObjectOutputStream flujoDatosAnfitrion = new ObjectOutputStream(socketAnfitrion.getOutputStream())){
				flujoDatosAnfitrion.writeObject(partida);
			}catch(IOException e) {
				e.printStackTrace();
			}
		}catch(IOException e) {
			e.printStackTrace();
			
		}
		
	}

	private void crearPartida(Jugador jugador) {
		
		Servidor.partidas.add(new Partida(jugador.getNombre().concat(jugador.getDireccion()), jugador));
		
	}
}
