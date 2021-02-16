package riotgamewizard.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import riotgamewizard.server.Jugador;
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
            Jugador jugadorAnf = new Jugador();
            System.out.println("Estableciendo la conexiï¿½n");
            InetSocketAddress addr = new InetSocketAddress("localhost", 3333);
            clientSocket.connect(addr);
            enviarDatos(clientSocket);
            recibirDatos(clientSocket);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void recibirDatos(Socket clientSocket) {

        try (ObjectInputStream obIn = new ObjectInputStream(clientSocket.getInputStream())) {
            Servidor.mutex.acquire();
            Partida partida = (Partida) obIn.readObject();
            Jugador jugadorAnf = new Jugador();
            //Asignamos los datos de la partida al jugador anfitrion que la jugaran.

            if (partida.getJugadores().get(0).isEsAnfitrion() == true) {
                jugadorAnf.setNombre(partida.getJugadores().get(0).getNombre());
                jugadorAnf.setDireccion(partida.getJugadores().get(0).getDireccion());
                jugadorAnf.setPuerto(partida.getJugadores().get(0).getPuerto());
                crearSocket1vs1(jugadorAnf);
                unirseSocket1vs1(clientSocket, jugadorAnf);
            } else {
                jugadorAnf.setNombre(partida.getJugadores().get(1).getNombre());
                jugadorAnf.setDireccion(partida.getJugadores().get(1).getDireccion());
                jugadorAnf.setPuerto(partida.getJugadores().get(1).getPuerto());
                crearSocket1vs1(jugadorAnf);
                unirseSocket1vs1(clientSocket, jugadorAnf);
            }

            if (partida.getJugadores().get(0).isEsAnfitrion() == true) {
                Servidor.mutex.release();
            }

        } catch (IOException | ClassNotFoundException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void enviarDatos(Socket clientSocket) throws IOException {
        try {
            DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());

            String mensaje = "NewGame," + Thread.currentThread().getName() + ",DADOS";
            dOut.writeUTF(mensaje);
            dOut.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Cliente().logica();
    }

    private void crearSocket1vs1(Jugador jugadorAnf) throws IOException {
        ServerSocket socket = new ServerSocket();
        InetSocketAddress inetAdr = new InetSocketAddress(jugadorAnf.getDireccion(), jugadorAnf.getPuerto());
        socket.bind(inetAdr);

        //SimuladorJuego juego = new SimuladorJuego();
        //int tirada = juego.getTirada();
        String resultado;

        try (Socket newSocket = socket.accept();
                InputStream is = newSocket.getInputStream();
                OutputStream os = newSocket.getOutputStream();
                // Flujos que manejan caracteres
                InputStreamReader isr = new InputStreamReader(is);
                OutputStreamWriter osw = new OutputStreamWriter(os);
                // Flujos de líneas
                BufferedReader bReader = new BufferedReader(isr);
                PrintWriter pWriter = new PrintWriter(osw);) {
            String mensaje = bReader.readLine();

            //if (mensaje > tirada){
            System.out.println("El jugador no anfitrion ha ganado con un: " + mensaje);
            resultado = "V";

            //else if (mensaje < tirada){
            System.out.println("El jugador anfitrion ha ganado con un: " //+ tirada
            );
            resultado = "D";
            //else if ( mensaje == tirada){
            System.out.println("Han empatado con un: " + mensaje);
            resultado = "E";

            pWriter.print(resultado);
            pWriter.flush();
        }
    }

    private void unirseSocket1vs1(Socket clientSocket, Jugador jugadorAnf) throws IOException {
        InetSocketAddress inetAdr = new InetSocketAddress(jugadorAnf.getDireccion(), jugadorAnf.getPuerto());
        clientSocket.connect(inetAdr);
        String resultado;

        //SimuladorJuego juego = new SimuladorJuego();
        //int tirada = juego.getTirada()
        try (InputStream is = clientSocket.getInputStream();
                OutputStream os = clientSocket.getOutputStream();
                // Flujos que manejan caracteres
                InputStreamReader isr = new InputStreamReader(is);
                OutputStreamWriter osw = new OutputStreamWriter(os);
                // Flujos de líneas
                BufferedReader bReader = new BufferedReader(isr);
                PrintWriter pWriter = new PrintWriter(osw)) {

            //enviar resultado de la partida
            String mensaje = "Aqui va lo que le ha salido en la tirada al jugador no anfitrion";
            pWriter.print(mensaje);
            pWriter.flush();
            resultado = bReader.readLine();

            if (resultado.equals("V") || resultado.equals("D")) {
                enviarResultado(clientSocket, resultado);
            } else if (resultado.equals("E")) {
                unirseSocket1vs1(clientSocket, jugadorAnf);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void enviarResultado(Socket clientSocket, String resultado) {
        //enviar resultado a servidor
        try {
            DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());

            dOut.writeUTF(resultado);
            dOut.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

