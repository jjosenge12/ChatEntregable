package paquete;

import java.io.Serializable;

public class MensajeAServidor implements Serializable{

	private static final long serialVersionUID = -5905903694983224221L;
	String mensaje;
	Sala sala;
	int tipo;
	
	public MensajeAServidor(String mensaje, Sala sala, int tipo) {
		this.mensaje = mensaje;
		this.sala = sala;
		this.tipo = tipo;
	}
	public String getMensaje() {
		return mensaje;
	}
	
	public Sala getSala() {
		return sala;
	}
	
	public int getTipo() {
		return tipo;
	}
	@Override
	public String toString() {
		return "MensajeAServidor [mensaje=" + mensaje + ", sala=" + sala + ", tipo=" + tipo + "]";
	}
	
	
	

}
