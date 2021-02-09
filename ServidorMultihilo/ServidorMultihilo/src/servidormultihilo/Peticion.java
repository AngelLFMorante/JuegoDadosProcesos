/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidormultihilo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Victor
 */
public class Peticion implements Runnable{
    Socket socket;
    
    public Peticion (Socket socket){
        this.socket=socket;
    }
    
    public void run(){
        try(InputStream is= socket.getInputStream();
                InputStreamReader isr=new InputStreamReader(is);
                BufferedReader bfr= new BufferedReader(isr);
                OutputStream os= socket.getOutputStream();
                PrintWriter pw= new PrintWriter(os);){
            
                String mensaje=bfr.readLine();
                System.out.println("El mensaje recibido es: "+mensaje);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
