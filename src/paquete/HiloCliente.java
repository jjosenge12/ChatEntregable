package paquete;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import grafico.Lobby;

public class HiloCliente extends Thread {
	ObjectInputStream entrada;
	Lobby ventana;
	private Socket socket;

	public HiloCliente(Socket socket, ObjectInputStream entrada, Lobby ventana) {
		this.socket = socket;
		this.entrada = entrada;
		this.ventana = ventana;
	}

	public void run() {
		MensajeACliente mensaje;
		try {
			entrada = new ObjectInputStream(socket.getInputStream());
			int tipoMensaje=0;
			while (tipoMensaje != -1 && tipoMensaje!=-2) {// Se cierra el hilo con un mensaje del servidor de tipo 1
				mensaje = (MensajeACliente) entrada.readObject();
				tipoMensaje = mensaje.getTipo();
				switch (tipoMensaje) {
				case 0:// 0:el cliente se conecto correctamente
					clienteAceptado();
					break;
				case 1:// 1: el servidor pidio los datos del cliente
					enviarDatos();
					break;
				case 2:// 2:actualizar salas
					actualizarSalas(mensaje);
					break;
				case 3:// 3: el cliente se unio a una sala
					unirseASala(mensaje);
					break;
				case 4:// 4: el cliente salio de una sala
					salirDeSala(mensaje);
					break;
				case 5:// 5: el cliente recibio un mensaje en alguna de sus salas abiertas
					recibirMensaje(mensaje);
					break;
				case 6:
					// 6: recibe tiempos de usuarios en la sala
					recibirTiempos(mensaje);
					break;
				case 7:
					//7: recibe la lista de usuarios en la sala
					recibirListaUsuarios(mensaje);
					break;
				case 8:
					//8: creacion y apertura de sala privada
					salaPrivadaCreada(mensaje);
					break;
				case 9:
					//9: ya existe la sala privada
					mostrarErrorPorPantalla("Sala privada existente", "Error en creacion de sala privada");
					break;
				case 10:
					//10: ya existe una sala con ese nombre.
					mostrarErrorPorPantalla("Elija otro nombre de sala", "Error en creacion de sala");
					break;
				}
				if(tipoMensaje==-2) {
					mostrarErrorPorPantalla("Elija otro nombre de usuario", "Desconexion del servidor");
					socket.close();
				}
				
			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error en lectura de mensaje en hiloCliente");
			e.printStackTrace();
		}
	}

	private void mostrarErrorPorPantalla(String descripcion, String titulo) {
		ventana.mostrarErrorPorPantalla(descripcion, titulo);
	}

	private void salaPrivadaCreada(MensajeACliente mensaje) {
		ventana.salaPrivadaCreada(mensaje);
	}

	private void recibirListaUsuarios(MensajeACliente mensaje) {
		ventana.recibirListaUsuarios(mensaje);
	}

	private void recibirTiempos(MensajeACliente mensaje) {
		ventana.recibirTiempos(mensaje);
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
