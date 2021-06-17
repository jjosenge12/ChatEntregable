package paquete;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;


import grafico.Lobby;


public class HiloCliente extends Thread{
	ObjectInputStream entrada;
	Lobby ventana;
	private Socket socket;

	public HiloCliente(Socket socket, ObjectInputStream entrada, Lobby ventana) {
		this.socket=socket;
		this.entrada = entrada;
		this.ventana = ventana;
	}
	
	public void run() {
		MensajeACliente mensaje;
		try {
			entrada = new ObjectInputStream(socket.getInputStream());
			int tipoMensaje=1;
			while(tipoMensaje!=-1) {//Se cierra el hilo con un mensaje del servidor de tipo 1
				mensaje=(MensajeACliente) entrada.readObject();
				tipoMensaje=mensaje.getTipo();
				switch (tipoMensaje) {
				case 0://0:el cliente se conecto correctamente
					clienteAceptado();
					break;
				case 1://1: el servidor pidio los datos del cliente
					enviarDatos();
					break;
				case 2://2:actualizar salas
					actualizarSalas(mensaje);
					break;
				case 3://3: el cliente se unio a una sala
					unirseASala(mensaje);
					break;
				case 4://4: el cliente salio de una sala
					salirDeSala(mensaje);
					break;
				case 5://5: el cliente recibio un mensaje en alguna de sus salas abiertas
					recibirMensaje(mensaje);
					break;
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error en lectura de mensaje en hiloCliente");	
			e.printStackTrace();
		}
	}

	private void recibirMensaje(MensajeACliente mensaje) {
		ventana.recibirMensaje(mensaje);
	}

	private void enviarDatos() {
		ventana.enviarDatosAlServidor();
	}

	private void clienteAceptado() {
		ventana.activarBotones();
	}

	private void salirDeSala(MensajeACliente mensaje) {
		ventana.cerrarSala(mensaje.getSala());
	}

	private void unirseASala(MensajeACliente mensaje) {
		ventana.abrirSala(mensaje.getSala());
	}

	private void actualizarSalas(MensajeACliente mensaje) {
		ventana.actualizarSalas(mensaje.getSalas());
	}
	

}
