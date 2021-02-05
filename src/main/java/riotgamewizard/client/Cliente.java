package riotgamewizard.client;

public class Cliente implements Runnable{

    /**
     * Inicializar cliente con 10 peticiones simultaneas.
     */
    public void start(){
        for (int i = 0; i < 10; i++){
            Thread usuario = new Thread();
            usuario.start();
        }
    }

    @Override
    public void run() {

    }



}
