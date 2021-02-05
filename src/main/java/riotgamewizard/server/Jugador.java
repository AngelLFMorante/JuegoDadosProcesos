package riotgamewizard.server;

public class Jugador {

    private String nombre;
    private String host;
    private int puerto;
    private boolean anfitrion;

    public Jugador(){

    }

    public Jugador (String nombre, int puerto, String host, boolean anfitrion){
        this.nombre = nombre;
        this.anfitrion = anfitrion;
        this.host = host;
        this.puerto = puerto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPuerto() {
        return puerto;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    public boolean isAnfitrion() {
        return anfitrion;
    }

    public void setAnfitrion(boolean anfitrion) {
        this.anfitrion = anfitrion;
    }
}
