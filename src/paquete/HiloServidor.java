package paquete;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class HiloServidor extends Thread {
	private Socket socket;
	private List<Socket> sockets;
	private Map<Socket, ObjectOutputStream> mapaSocketsObjectOuput;
	private List<Sala> salas = new ArrayList<Sala>();
	private Map<String, Socket> mapaNombreSocket;

	public HiloServidor(Socket socket, List<Socket> sockets, Map<Socket, ObjectOutputStream> mapa, List<Sala> salas,
			Map<String, Socket> mapaNombreSocket) {
		this.socket = socket;
		this.sockets = sockets;
		this.mapaSocketsObjectOuput = mapa;
		this.salas = salas;
		this.mapaNombreSocket = mapaNombreSocket;
	}

	public void run() {
		ObjectInputStream entrada = null;
		ObjectOutputStream salida = null;
		MensajeAServidor mensaje = null;

		try {
			salida = new ObjectOutputStream(socket.getOutputStream());
			mapaSocketsObjectOuput.put(socket, salida);
			/*
			 * A cada socket le pertenece un ObjectOutputStream ya que si se crean nuevos ObjectOutputStream, 
			 * estos insertan un header en el OutputStream y dificulta la recepcion de mensajes por parte del cliente.
			 */
			MensajeACliente msj = new MensajeACliente(null, null, 0);//Se le envia un mensaje 0 al cliente para habilitar su interfaz.
			salida.writeObject(msj);
			salida.flush();
			salida.reset();
			msj = new MensajeACliente(null, null, 1);//Se le piden datos de usuario al cliente.
			salida.writeObject(msj);
			salida.flush();
			salida.reset();

			entrada = new ObjectInputStream(socket.getInputStream());
			int tipoMensaje=1;

			while (tipoMensaje!=0) {//El cliente envia un 0 para desconectarse
				mensaje = (MensajeAServidor) entrada.readObject();
				tipoMensaje = mensaje.getTipo();

				switch (tipoMensaje) {
				case 1://El cliente envia sus datos por un mensaje tipo 1
					mapaNombreSocket.put(mensaje.getMensaje(), socket);
					break;
				case 2://El cliente crea una sala por un mensaje tipo 2
					agregarSala(mensaje);
					break;
				case 3://El cliente borra una sala por un mensaje tipo 3
					quitarSala(mensaje);
					break;
				case 4://El cliente se une a una sala por un mensaje tipo 4
					unirseASala(mensaje);
					break;
				case 5://El cliente sale de una sala por un mensaje tipo 5
					salirDeSala(mensaje);
					break;
				case 6://El cliente envia un mensaje a una sala por un mensaje tipo 6
					recibirMensaje(mensaje);
					break;
				}
				if(tipoMensaje!=0) {
					actualizarSalas();//Al final de leer un mensaje se actualizan las salas de todos los clientes para evitar desincronizacion 					
				}

			}
		} catch (IOException | ClassNotFoundException e) {
			System.out.println("Error en lectura de mensaje en HiloServidor");
			e.printStackTrace();
		}
		try {//Desconexion del cliente del servidor
			MensajeACliente msj=new MensajeACliente(null,null,-1);
			salida.writeObject(msj);
			entrada.close();
			salida.close();
			mapaSocketsObjectOuput.remove(socket);
			mapaNombreSocket.remove(mensaje.getMensaje());
			sockets.remove(socket);
			socket.close();
		} catch (IOException e) {
			System.out.println("Error desconectando cliente");
			e.printStackTrace();
		}
		
	}

	private void recibirMensaje(MensajeAServidor mensajeServidor) {
		ObjectOutputStream salida;
		String nombreSala = mensajeServidor.getSala().getNombreSala();
		Sala salaAux = null;
		int i = 0;
		
		
		//Se busca el nombre de la sala en la lista de salas
		salaAux = salas.get(i);
		while (!salaAux.getNombreSala().equals(nombreSala)) {
			i++;
			salaAux = salas.get(i);
		}
		
		//Se concatena la hora con el mensaje enviado por el cliente.
		Date tiempo=new Date();
		@SuppressWarnings("deprecation")
		String hora="("+tiempo.getHours()+":"+tiempo.getMinutes()+")";
		String mensaje=hora+mensajeServidor.getMensaje();

		MensajeACliente msjCliente = new MensajeACliente(mensaje, 5,salaAux);
		List<Socket> socketsEnSala = new ArrayList<Socket>();
		List<String> usuariosEnSala = new ArrayList<String>();
		usuariosEnSala = salaAux.getUsuariosConectados();

		
		//Se agregan los sockets pertenecientes a los usuarios conectados a la sala.
		for (String usuario : usuariosEnSala) {
			socketsEnSala.add(mapaNombreSocket.get(usuario));
		}

		try {
			for (Socket envio : socketsEnSala) {
				salida = mapaSocketsObjectOuput.get(envio);
				salida.writeObject(msjCliente);
				salida.flush();
				salida.reset();

			}
		} catch (IOException e) {
			System.out.println("Error en envio de mensaje HiloServidor");
			e.printStackTrace();
		}
	}

	private void salirDeSala(MensajeAServidor mensajeServidor) {
		ObjectOutputStream salida = mapaSocketsObjectOuput.get(socket);
		Sala sala = mensajeServidor.getSala();
		Sala salaAActualizar = null;
		int i = 0;

		//Se busca el nombre de la sala en la lista de salas
		while (!(salaAActualizar = salas.get(i)).getNombreSala().equals(sala.getNombreSala())) {
			i++;
		}
		salaAActualizar.eliminarUsuario(mensajeServidor.getMensaje());
		
		
		MensajeACliente msj = new MensajeACliente(null, 4, salaAActualizar);
		try {
			salida.writeObject(msj);
			salida.flush();
			salida.reset();
		} catch (IOException e) {
			System.out.println("Error envio mensaje salirDeSala");
			e.printStackTrace();
		}

	}

	private void unirseASala(MensajeAServidor mensajeServidor) {
		ObjectOutputStream salida = mapaSocketsObjectOuput.get(socket);
		Sala sala = mensajeServidor.getSala();
		Sala salaAActualizar = null;
		int i = 0;

		//Se busca el nombre de la sala en la lista de salas
		while (!(salaAActualizar = salas.get(i)).getNombreSala().equals(sala.getNombreSala())) {
			i++;
		}
		salaAActualizar.agregarUsuario(mensajeServidor.getMensaje());
		
		MensajeACliente msj = new MensajeACliente(null, 3, salaAActualizar);
		try {
			salida.writeObject(msj);
			salida.flush();
			salida.reset();
		} catch (IOException e) {
			System.out.println("Error envio mensaje unirseASala");
			e.printStackTrace();
		}

	}

	private void quitarSala(MensajeAServidor mensaje) {
		Sala sala = mensaje.getSala();
		salas.remove(sala);
	}

	private void agregarSala(MensajeAServidor mensaje) {
		Sala sala = mensaje.getSala();
		salas.add(sala);
	}

	private void actualizarSalas() {

		MensajeACliente msj = new MensajeACliente(null, salas, 2);
		enviarMensajeATodosLosSockets(msj);

	}

	private void enviarMensajeATodosLosSockets(MensajeACliente msj) {
		ObjectOutputStream salida;

		try {
			for (Socket envio : sockets) {
				salida = mapaSocketsObjectOuput.get(envio);
				salida.writeObject(msj);
				salida.flush();
				salida.reset();

			}
		} catch (IOException e) {
			System.out.println("Error envio mensaje actualizando sala");
			e.printStackTrace();
		}

	}
}
