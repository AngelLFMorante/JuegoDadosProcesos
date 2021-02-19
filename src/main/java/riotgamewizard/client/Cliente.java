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

    public static void main(String[] args) {
        new Cliente().logica();
    }

    /**
     * Creamos 10 clientes.
     */
    public void logica() {
        for (int i = 0; i < 10; i++) {
            Thread usuario = new Thread(this);
            usuario.setName("Jugador".concat(String.valueOf(i)));
            usuario.start();
        }
    }
    //Creamos el socket cliente y llamamos a los metodos de la clase

    @Override
    public void run() {

        try ( Socket clientSocket = new Socket()) {
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

    /**
     * El metodo recibirDatos recibe los datos de la partida que ya contiene los
     * jugadores y extrae estos datos asignandolos a jugadorAnf quien creara el
     * servidor donde jugaran.
     *
     * @param clientSocket
     */
    private void recibirDatos(Socket clientSocket) {

        try ( ObjectInputStream obIn = new ObjectInputStream(clientSocket.getInputStream())) {
            Servidor.mutex.acquire();
            Partida partida = (Partida) obIn.readObject();
            Jugador jugadorAnf = new Jugador();

            //Recorremos la partida sacando si es Anfitrion o no, en ek primer paso creara el servidor 
            //donde se conectara el juagador 2 en el segundo caso
            for (Jugador jugador : partida.getJugadores()) {
                if (jugador.isEsAnfitrion()) {
                    jugadorAnf = jugador;
                    System.out.println(jugador.getNombre() + " " + partida.getId());
                    //crearSocket1vs1(jugador);
                    Servidor.mutex.release();

                } else {
                    System.out.println(jugador.getNombre() + " " + partida.getId());
                    //unirseSocket1vs1(clientSocket, jugadorAnf, partida);
                    Servidor.mutex.release();

                }

            }
            String resultado = null;
            enviarResultado(resultado, partida);

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

    /**
     * Este metodo crea el socket servidor que leera la informacion pasada por
     * el jugador no anfitrion y comparara las tiradas decidiendo un ganador.
     *
     * @param jugadorAnf Jugador anfitrion de la partida
     * @throws IOException
     */
    private void crearSocket1vs1(Jugador jugadorAnf) throws IOException {
        ServerSocket socket = new ServerSocket();
        InetSocketAddress inetAdr = new InetSocketAddress(jugadorAnf.getDireccion(), jugadorAnf.getPuerto());
        socket.bind(inetAdr);
        //llamamos al simulador para que el anfitrion realice su tirada
        //SimuladorJuego juego = new SimuladorJuego();
        //int tirada = juego.getTirada();
        String resultado;

        try ( Socket newSocket = socket.accept();  InputStream is = newSocket.getInputStream();  OutputStream os = newSocket.getOutputStream(); // Flujos que manejan caracteres
                  InputStreamReader isr = new InputStreamReader(is);  OutputStreamWriter osw = new OutputStreamWriter(os); // Flujos de líneas
                  BufferedReader bReader = new BufferedReader(isr);  PrintWriter pWriter = new PrintWriter(osw);) {
            String mensaje = bReader.readLine();
            //Como aun no tenemos la clase Simulador comento los condicionales que
            //guardarian el resultado victoria,empate o derrota
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

    private void unirseSocket1vs1(Socket clientSocket, Jugador jugadorAnf, Partida partida) throws IOException {
        InetSocketAddress inetAdr = new InetSocketAddress(jugadorAnf.getDireccion(), jugadorAnf.getPuerto());
        clientSocket.connect(inetAdr);
        String resultado;
        //llamamos al simulador para obtener la primera tirada
        //SimuladorJuego juego = new SimuladorJuego();
        //int tirada = juego.getTirada()

        try ( InputStream is = clientSocket.getInputStream();  OutputStream os = clientSocket.getOutputStream(); // Flujos que manejan caracteres
                  InputStreamReader isr = new InputStreamReader(is);  OutputStreamWriter osw = new OutputStreamWriter(os); // Flujos de líneas
                  BufferedReader bReader = new BufferedReader(isr);  PrintWriter pWriter = new PrintWriter(osw)) {

            //enviar resultado de la tiradas
            String mensaje = "Aqui va lo que le ha salido en la tirada al jugador no anfitrion";
            pWriter.print(mensaje);
            pWriter.flush();
            resultado = bReader.readLine();

            if (resultado.equals("V") || resultado.equals("D")) {
                //enviarResultado(resultado, partida);
            } else if (resultado.equals("E")) {
                unirseSocket1vs1(clientSocket, jugadorAnf, partida);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Le enviamos los resultados de cada partida 1vs1 al servidor, que se
     * encargara de procesarlos.
     *
     * @param resultado Es un string que contiene V(victoria) o D(derrota).
     * @param partida Le pasamos la partida para poder obtener la id.
     */
    private void enviarResultado(String resultado, Partida partida) {
        //enviar resultado a servidor
        try ( Socket clientSocket = new Socket()) {
            InetSocketAddress addr = new InetSocketAddress("localhost", 3333);
            clientSocket.connect(addr);
            DataOutputStream dOut = new DataOutputStream(clientSocket.getOutputStream());
            String mensaje = "V" + "," + partida.getId();
            dOut.writeUTF(mensaje);
            dOut.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
