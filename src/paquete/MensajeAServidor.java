package paquete;

import java.io.Serializable;

public class MensajeAServidor implements Serializable{

	private static final long serialVersionUID = -5905903694983224221L;
	String texto;
	Sala sala;
	int tipo;
	
	public MensajeAServidor(String texto, Sala sala, int tipo) {
		this.texto = texto;
		this.sala = sala;
		this.tipo = tipo;
	}
	public String getTexto() {
		return texto;
	}
	
	public Sala getSala() {
		return sala;
	}
	
	public int getTipo() {
		return tipo;
	}
	@Override
	public String toString() {
		return "MensajeAServidor [mensaje=" + texto + ", sala=" + sala + ", tipo=" + tipo + "]";
	}
	
	
	

}
