/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidormultihilo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Victor
 */
public class ServidorSocketStreamMultiHilo {
    
    public static void main(String[]args) throws IOException{
        System.out.println("Creando el socket del servidor");
        Socket newSocket=null;
        
        try(ServerSocket serverSocket= new ServerSocket();){
            System.out.println("Realizando el bind");
            InetSocketAddress addr= new InetSocketAddress("localhost",5555);
            serverSocket.bind(addr);
            System.out.println("Aceptando las conexiones");
            
            while(true){
                newSocket=serverSocket.accept();
                System.out.println("Conexión establecida");
                Peticion p= new Peticion(newSocket);
                Thread hilo=new Thread();
                hilo.start();
                System.out.println("Esperando una nueva conexión");
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            if(newSocket!=null){
                newSocket.close();
            }
        }
    }
    
}
