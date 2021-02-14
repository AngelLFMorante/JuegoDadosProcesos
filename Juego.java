/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juego;

/**
 *
 * @author Victor
 */
public class Juego {

    /**
     * @param args the command line arguments
     */
        
    public static void main(String[] args) {
            // TODO code application logic here
            
            int jugadorServidor;
            int jugadorCliente;

           
            
            do{
            jugadorServidor=(int)(Math.random()*6)+1;
            jugadorCliente=(int)(Math.random()*6)+1;
            if(jugadorServidor > jugadorCliente){
                System.out.println("El jugador 1 ha obtenido un resultado de "+jugadorServidor);
                System.out.println("El jugador 2 ha obtenido un resultado de "+jugadorCliente);
                System.out.println("Por lo tanto, el ganador es el Jugador 1");
            }
            
            else if(jugadorServidor < jugadorCliente){
                System.out.println("El jugador 1 ha obtenido un resultado de "+jugadorServidor);
                System.out.println("El jugador 2 ha obtenido un resultado de "+jugadorCliente);
                System.out.println("Por lo tanto, el ganador es el Jugador 2");
            }
            else{
                System.out.println("El jugador 1 ha obtenido un resultado de "+jugadorServidor);
                System.out.println("El jugador 2 ha obtenido un resultado de "+jugadorCliente);
                System.out.println("Por lo tanto, el resultado es un empate");
            }
            }while(jugadorServidor==jugadorCliente);
        
    }
    
//    public int tiradaServidor(){
//        jugadorServidor=(int)(Math.random()*6)+1;
//        
//        return jugadorServidor;
//        
//    }
//    
//    public int tiradaCliente(){
//        jugadorCliente=(int)(Math.random()*6)+1;
//            return jugadorCliente;
//        
//    }
}
