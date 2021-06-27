package paquete;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import grafico.Lobby;


public class Cliente{

	String nombre;
	private Socket socket;
	private ObjectInputStream entrada;
	private ObjectOutputStream salida;
	private Lobby lobby;
	

	public Cliente(String nombre,String ip, int puerto) {
		this.nombre=nombre;
		try {
			socket = new Socket(ip, puerto);
			salida=new ObjectOutputStream(socket.getOutputStream());

		} catch (UnknownHostException e) {
			System.out.println("Error host desconocido");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Error en conexion con el servidor");
			e.printStackTrace();
		}
	}

	public void enviarMensaje(MensajeAServidor mensaje) {
		try {
			salida.reset();
			salida.writeObject(mensaje);
		} catch (IOException e) {
			System.out.println("Error en envio de mensaje cliente");
			e.printStackTrace();
		}
	}

	public void inicializarHiloCliente(Lobby ventana) {
		new HiloCliente(socket,entrada, ventana).start();
	}

	public static void main(String[] args) {
		new Lobby();
	}
	
	public String getNombre() {
		return nombre;
	}

}
