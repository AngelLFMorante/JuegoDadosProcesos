package riotgamewizard.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Servidor {
	
	//algunas variables estáticas
	public static List<Jugador> jugadores = new ArrayList<>();
	public static List<Partida> partidas = new ArrayList<>();
	public static Semaphore mutex = new Semaphore(1);
	public static Semaphore mutexProcesarPartida = new Semaphore(1);
	
	public Servidor() {

	}
	
	//inicio del servidor
	private void start() {
		try (ServerSocket socketServer = new ServerSocket()) {
			
			 InetSocketAddress addres = new InetSocketAddress("localhost", 3333);
			 socketServer.bind(addres);
			 
			while (true) {
				System.out.println("Esperando jugador...");
				// aceptamos conexiones
				Socket miSocket = socketServer.accept();
				System.out.println("Aceptado peticion");
				Conexion conexion = new Conexion(miSocket);
				conexion.start();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Servidor().start();
	}

}
