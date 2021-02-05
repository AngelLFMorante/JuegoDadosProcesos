package riotgamewizard.server;

public enum TipoPartida {

    DADOS("dados", 2);

    private String nombre;
    private int maxJugadores;

    private TipoPartida(String nombre, int maxJugadores){
        this.nombre= nombre;
        this.maxJugadores = maxJugadores;
    }

}
