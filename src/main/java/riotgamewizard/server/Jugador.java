package riotgamewizard.server;

public class Jugador {
	
	private String nombre;
	private String direccion;
	private int puerto;
	private boolean esAnfitrion;

	
	public Jugador() {
		
	}
	

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public int getPuerto() {
		return puerto;
	}

	public void setPuerto(int puerto) {
		this.puerto = puerto;
	}

	public boolean isEsAnfitrion() {
		return esAnfitrion;
	}

	public void setEsAnfitrion(boolean esAnfitrion) {
		this.esAnfitrion = esAnfitrion;
	}
	
	
	
}
