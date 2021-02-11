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

    private void procesarPartida(String[] datos) {
        try {
            Servidor.mutexProcesarPartida.acquire();
            // recoger datos del jugador.
            Jugador jugador = new Jugador();
            jugador.setNombre(datos[1]);
            jugador.setDireccion(socket.getInetAddress().getHostAddress());
            jugador.setPuerto(socket.getPort());

            // quiero saber como hacer el tipoEnumerado, para que lo que reciba sea igual a
            // ese tipo e inicializarlo.
            TipoPartida tipoPartida = obtenerTipoPartida(datos);
            System.out.printf("Tipo de partida elegida: %s%n Máximo de jugadores: %d%n.", tipoPartida.getTipoPartida(),
                    tipoPartida.getMaxJugadores());

            // crear o unirse a partida ya creada.
            createOrJoinPartida(jugador, tipoPartida);

            // levantamos al hilo para que pueda crear partida, en el caso de que no sea la
            // misma partida que ha elegido otro cliente.
            Servidor.mutex.release();
            // mandamos informaci�n a cada uno de los jugadores, cliente.
            mandarInfoPartida(Servidor.partidas.get(Servidor.partidas.size() - 1));

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void createOrJoinPartida(Jugador jugador, TipoPartida tipoPartida) {
        // nos llevamos este condicional a un método, para simplificar código.
        if (Servidor.partidas.isEmpty()) {
            System.out.println(jugador.getNombre() + " anfitrion");
            jugador.setEsAnfitrion(true);
            crearPartida(jugador);
            Servidor.mutexProcesarPartida.release();

        } else if (Servidor.partidas.get(Servidor.partidas.size() - 1).getJugadores().size() < tipoPartida
                .getMaxJugadores()) {
            // aniadir jugador invitado
            jugador.setEsAnfitrion(false);
            Servidor.partidas.get(Servidor.partidas.size() - 1).annadirJugador(jugador);
            Servidor.mutexProcesarPartida.release();

        } else {
            System.out.println(jugador.getNombre() + " anfitrion");
            jugador.setEsAnfitrion(true);
            crearPartida(jugador);
            Servidor.mutexProcesarPartida.release();
        }
    }

    // obtengo el tipo de partida específico
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

    private void crearPartida(Jugador jugador) {
        try {
            Servidor.mutex.acquire(); // semaforo para controlar la creaci�n de la partida y no entren todos los
            // hilos.
            //el id de partida se corresponde con el nombre+direccion
            Servidor.partidas.add(new Partida(jugador.getNombre().concat(jugador.getDireccion()), jugador));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
