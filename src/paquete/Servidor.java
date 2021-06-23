package paquete;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Servidor {
	int puerto;
	ServerSocket server;
	List<Socket> sockets;
	List<Sala> salas;
	Map<Socket,ObjectOutputStream> mapaSocketsObjectOuput;
	Map<String,Socket> mapaNombreSocket;
	Map<String, Long> mapaNombresTiempos;

	public Servidor(int puerto) {
		this.puerto = puerto;
		this.sockets = new ArrayList<Socket>();
		this.salas=new ArrayList<Sala>();
		this.mapaSocketsObjectOuput=new HashMap<Socket,ObjectOutputStream>();
		this.mapaNombreSocket=new HashMap<String,Socket>();
		this.mapaNombresTiempos=new HashMap<String,Long>();

		try {
			server = new ServerSocket(puerto);
		} catch (IOException e) {
			System.out.println("Error en creacion de puertos");
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			while (true) {
				Socket socket = server.accept();
				System.out.println("Cliente conectado");
				sockets.add(socket);
				new HiloServidor(socket, sockets,mapaSocketsObjectOuput,salas,mapaNombreSocket,mapaNombresTiempos).start();
			}

		} catch (IOException e) {
			System.out.println("Error en conexion con el cliente");
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		System.out.println("Servidor ejecutandose");
		new Servidor(50000).run();
	}

}
