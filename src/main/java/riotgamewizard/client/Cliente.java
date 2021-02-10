package riotgamewizard.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import riotgamewizard.server.Partida;
import riotgamewizard.server.Servidor;

public class Cliente implements Runnable {

    /**
     * Inicializar cliente con 10 peticiones simultaneas.
     */
    public void logica() {
        for (int i = 0; i < 10; i++) {
            Thread usuario = new Thread(this);
            usuario.setName("Jugador".concat(String.valueOf(i)));
            usuario.start(); 
        }
    }

    @Override
    public void run() {

        try (Socket clientSocket = new Socket()) {
            System.out.println("Estableciendo la conexión");
            InetSocketAddress addr = new InetSocketAddress("localhost", 3333);
            clientSocket.connect(addr);
            enviarDatos(clientSocket);
            recibirDatos(clientSocket);
            System.out.println("Terminado");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


	private void recibirDatos(Socket clientSocket) {
		
		try(ObjectInputStream obIn = new ObjectInputStream(clientSocket.getInputStream())){
			Servidor.mutex.acquire();
			Partida partida =  (Partida) obIn.readObject();
			System.out.println(Thread.currentThread().getName()+" este es el hilo que recibo y es 0");//este es el jugador que llega el primero, con el que tendras que operar para saber si es anfitrion o no.
			System.out.println("Nombre: "+partida.getJugadores().get(0).getNombre()+" es anfitrión : "+partida.getJugadores().get(0).isEsAnfitrion()+" vs Nombre: "+partida.getJugadores().get(1).getNombre()+" es anfitrión: "+partida.getJugadores().get(1).isEsAnfitrion());
			Servidor.mutex.release();
			
		} catch (IOException | ClassNotFoundException | InterruptedException e ) {
			e.printStackTrace();
		}
		
		
	}


	private void enviarDatos(Socket clientSocket) throws IOException {
		try {
			DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());
		    
		    System.out.println("Enviando mensaje");
		    
		    String mensaje = "NewGame,"+Thread.currentThread().getName()+",DADOS";
		    dOut.writeUTF(mensaje);
		    dOut.flush();
		    System.out.println("Mensaje enviado");

		}catch (IOException e) {
			e.printStackTrace();
		}
	}


}