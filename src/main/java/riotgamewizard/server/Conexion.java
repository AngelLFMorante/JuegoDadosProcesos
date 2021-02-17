package riotgamewizard.client;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class Conexion extends Thread {

    private Socket socket;

    public Conexion(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        // recibimos datos: "newGame,nicknombre,tipoPartida"
        try (DataInputStream dIn = new DataInputStream(socket.getInputStream())) {
            String linea = dIn.readUTF();
            String[] datos = linea.split(",");
            if (datos.length == 3) {
                procesarPartida(datos);
            } else {
                finalizarPartida(datos[1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private synchronized void procesarPartida(String[] datos) {
        try {
            Servidor.mutexProcesarPartida.acquire();

            Jugador jugador = new Jugador();
            jugador.setNombre(datos[1]);
            jugador.setDireccion(socket.getInetAddress().getHostAddress());
            jugador.setPuerto(socket.getPort());
            System.out.println(Thread.currentThread().getName());

            TipoPartida tipoPartida = obtenerTipoPartida(datos);

            createOrJoinPartida(jugador, tipoPartida);
            //mantengo el hilo en espera hasta que se llene la sala para mandar la informacion
            while(Servidor.partidas.get(Servidor.partidas.size() - 1).getJugadores().size() < tipoPartida
                    .getMaxJugadores()) {
                System.out.println("Jugadores en la sala: "+Servidor.partidas.get(Servidor.partidas.size() - 1).getJugadores().size());
                wait();
            }

            mandarInfoPartida(Servidor.partidas.get(Servidor.partidas.size() - 1));
            Servidor.mutexProcesarPartida.release();
            Servidor.mutex.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }



    private  void createOrJoinPartida(Jugador jugador, TipoPartida tipoPartida) throws InterruptedException {


        if (Servidor.partidas.isEmpty()) {

            jugador.setEsAnfitrion(true);
            crearPartida(jugador, tipoPartida.getTipoPartida());

        } else if (Servidor.partidas.get(Servidor.partidas.size() - 1).getJugadores().size() < tipoPartida
                .getMaxJugadores()
                && tipoPartida.getTipoPartida()
                .equals(Servidor.partidas.get(Servidor.partidas.size() - 1).getTipoPartida())) {

            annadirJugador(jugador, tipoPartida);

        } else {

            jugador.setEsAnfitrion(true);
            crearPartida(jugador, tipoPartida.getTipoPartida());

        }

    }

    private void crearPartida(Jugador jugador, String tipoPartida) {
        try {
            Servidor.mutex.acquire();
            Servidor.partidas
                    .add(new Partida(jugador.getNombre().concat(jugador.getDireccion()), jugador, tipoPartida));
            Servidor.mutexProcesarPartida.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private synchronized void annadirJugador(Jugador jugador, TipoPartida tipoPartida) throws InterruptedException {

        jugador.setEsAnfitrion(false);
        Servidor.partidas.get(Servidor.partidas.size() - 1).annadirJugador(jugador);
        if(Servidor.partidas.get(Servidor.partidas.size() - 1).getJugadores().size() == tipoPartida
                .getMaxJugadores()) {
            System.out.println("Jugadores en la sala (if) : "+Servidor.partidas.get(Servidor.partidas.size() - 1).getJugadores().size()+" aviso notify");
            notifyAll();
        }
    }


    // obtengo el tipo de partida especÃ­fico
    private TipoPartida obtenerTipoPartida(String[] datos) {
        TipoPartida[] tipos = TipoPartida.values();
        for (int i = 0; i < tipos.length; i++) {
            if (tipos[i].getTipoPartida().equals(datos[2].toUpperCase())) {
                return tipos[i];
            }
        }
        return null;
    }

    // mandar partida, informacion de los jugadores.
    private void mandarInfoPartida(Partida partida) {
        try {
            ObjectOutputStream flujoDatos = new ObjectOutputStream(socket.getOutputStream());
            flujoDatos.writeObject(partida);
        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    // elimina partida y jugadores
    private void finalizarPartida(String id) {
        for (Partida partida : Servidor.partidas) {
            if (partida.getId() == id) {
                List<Jugador> jugadores = partida.getJugadores();
                for (Jugador jugador : jugadores) {
                    Servidor.jugadores.remove(jugador);
                }
                break;
            }
        }
    }
}
