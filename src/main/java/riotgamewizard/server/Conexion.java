package riotgamewizard.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class Conexion extends Thread {

	private Socket socket;
	
	public Conexion (Socket socket) {
		this.socket = socket;
	}
	
	public void run() {
		//recibimos datos:  "newGame,nicknombre,tipoPartida"
		try(DataInputStream dIn = new DataInputStream(socket.getInputStream())){
			String linea = dIn.readUTF();
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

	
	private void procesarPartida(String[] datos) {
			try {
				Servidor.mutexProcesarPartida.acquire();
				Jugador jugador = new Jugador();
				jugador.setNombre(datos[1]);
				jugador.setDireccion(socket.getInetAddress().getHostAddress());
				jugador.setPuerto(socket.getPort());
				if(Servidor.partidas.isEmpty()) {
					System.out.println(jugador.getNombre()+" anfitrion");
					jugador.setEsAnfitrion(true);
					crearPartida(jugador);
					Servidor.mutexProcesarPartida.release();
				}else if(Servidor.partidas.get(Servidor.partidas.size()-1).getJugadores().size() == 1) {
					//añadir jugador invitado
					jugador.setEsAnfitrion(false);
					Servidor.partidas.get(Servidor.partidas.size()-1).annadirJugador(jugador);
					Servidor.mutex.release();
					Servidor.mutexProcesarPartida.release();
				}else {
					jugador.setEsAnfitrion(true);
					crearPartida(jugador);
					Servidor.mutexProcesarPartida.release();
				}
				//mandamos información a cada uno de los jugadores, cliente.
				mandarInfoPartida(Servidor.partidas.get(Servidor.partidas.size()-1));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		
	}
	
	//mandar partida, informacion de los jugadores.
	private void mandarInfoPartida(Partida partida) {
		try{
			ObjectOutputStream flujoDatos = new ObjectOutputStream(socket.getOutputStream());
			flujoDatos.writeObject(partida);
		}catch(IOException e) {
			e.printStackTrace();
			
		}
		
	}
	
	
	private void crearPartida(Jugador jugador) {
		try {
			Servidor.mutex.acquire(); //semaforo para controlar la creación de la partida y no entren todos los hilos.
			Servidor.partidas.add(new Partida(jugador.getNombre().concat(jugador.getDireccion()), jugador));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}
}
