package riotgamewizard.server;

import java.net.Socket;

public class Peticion extends Thread{
    private Socket socket;

    public Peticion(Socket socket){
        this.socket = socket;
    }
    /**
     *   Coger la petición de los jugadores
     *   comprobarUsuario()
     *   creaPartida()
     *   finalizaPartida()
     */
    public void run(){

    }
}
