package paquete;

import java.io.Serializable;
import java.util.List;

public class MensajeACliente implements Serializable{




	private static final long serialVersionUID = -3270450619107272291L;
	String mensaje;
	List<Sala> listaSalas;
	Sala sala;
	int tipo;
	
	public MensajeACliente(String mensaje, List<Sala> salas, int tipo) {
		this.mensaje = mensaje;
		this.listaSalas = salas;
		this.tipo = tipo;
	}
	public MensajeACliente(String mensaje, int tipo,Sala sala) {
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
	
	public List<Sala> getSalas() {
		return listaSalas;
	}
	
	public int getTipo() {
		return tipo;
	}
	
	@Override
	public String toString() {
		return "MensajeACliente [mensaje=" + mensaje + ", sala=" + sala + ", tipo=" + tipo + "]";
	}
	

}
