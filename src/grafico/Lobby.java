package grafico;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import paquete.Cliente;
import paquete.MensajeACliente;
import paquete.MensajeAServidor;
import paquete.Sala;


public class Lobby extends JFrame {

	private static final long serialVersionUID = -4698413876318966275L;
	private JPanel contentPane;
	private JButton btnCrearSala;
	private JList<String> listaSalas;
	private DefaultListModel<String> listModel = new DefaultListModel<String>();
	private Cliente cliente;
	private JMenuItem menuItemConectarse;
	private JPanel panelBordeDerecho;
	private JButton btnBorrarSala;
	private JButton btnUnirseASala;
	private JPanel panelBotones;
	private JPanel panelInfoSala;
	JMenuItem btnDesconexion;

	private JTextPane textAreaInfoSala;
	private List<SalaChat> salasAbiertas;

	private List<Sala> salas;

	/**
	 * Create the frame.
	 */
	public Lobby() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				desconectarse();
			}
		});
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		salas = new ArrayList<Sala>();
		salasAbiertas = new ArrayList<SalaChat>();

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("Conexion");
		menuBar.add(mnNewMenu);

		menuItemConectarse = new JMenuItem("Conectarse");
		menuItemConectarse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				conectarse();
			}
		});
		mnNewMenu.add(menuItemConectarse);

		btnDesconexion = new JMenuItem("Salir");
		btnDesconexion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				desconectarse();
				dispose();
			}
		});
		mnNewMenu.add(btnDesconexion);
		btnDesconexion.setEnabled(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		btnCrearSala = new JButton("\uD83D\uDCBB Crear Sala ");
		btnCrearSala.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ingresarNombreSalaACrear();
			}
		});
		btnCrearSala.setEnabled(false);
		contentPane.add(btnCrearSala, BorderLayout.SOUTH);

		listaSalas = new JList<String>();
		listaSalas.setBorder(new LineBorder(new Color(0, 0, 0)));
		listaSalas.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!listaSalas.getValueIsAdjusting() && !listaSalas.isSelectionEmpty())
					mostrarInfoSala();
			}

		});
		listaSalas.setEnabled(false);
		listaSalas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		contentPane.add(listaSalas, BorderLayout.CENTER);

		listaSalas.setModel(listModel);

		panelBordeDerecho = new JPanel();
		panelBordeDerecho.setBorder(new LineBorder(new Color(0, 0, 0)));
		contentPane.add(panelBordeDerecho, BorderLayout.EAST);
		panelBordeDerecho.setLayout(new GridLayout(0, 1, 0, 15));

		panelBotones = new JPanel();
		panelBordeDerecho.add(panelBotones);
		panelBotones.setLayout(new GridLayout(0, 1, 0, 0));

		btnUnirseASala = new JButton("\uD83D\uDEEB Unirse a sala");
		panelBotones.add(btnUnirseASala);
		btnUnirseASala.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				unirseASala();
			}
		});

		btnUnirseASala.setEnabled(false);

		btnBorrarSala = new JButton("\u274C Borrar Sala");
		panelBotones.add(btnBorrarSala);
		btnBorrarSala.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				borrarSala();
			}
		});
		btnBorrarSala.setEnabled(false);

		panelInfoSala = new JPanel();
		panelBordeDerecho.add(panelInfoSala);
		panelInfoSala.setLayout(new BorderLayout(0, 0));

		textAreaInfoSala = new JTextPane();
		textAreaInfoSala.setBackground(Color.WHITE);
		textAreaInfoSala.setEnabled(false);
		textAreaInfoSala.setEditable(false);
		panelInfoSala.add(textAreaInfoSala);
		setVisible(true);
	}

	protected void borrarSala() {
		String nombreSalaElegida = listaSalas.getSelectedValue();
		Sala salaAEliminar = null;

		if (nombreSalaElegida != null) {
			int i = 0;
			salaAEliminar = salas.get(i);

			while (!salaAEliminar.getNombreSala().equals(nombreSalaElegida)) {
				i++;
				salaAEliminar = salas.get(i);
			}
			if (salaAEliminar.getCantUsuarios() == 0) {
				MensajeAServidor msj = new MensajeAServidor(null, salaAEliminar, 3);
				cliente.enviarMensaje(msj);
			} else {
				JOptionPane.showMessageDialog(this, "Tiene que haber 0 usuarios en la sala para eliminarla", "No se puede borrar la sala", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void conectarse() {
		String respuesta=JOptionPane.showInputDialog(this, "Ingrese nombre de usuario:", "");
		if(respuesta!=null && !respuesta.equals("")) {
			crearUsuario(respuesta);			
		}
	}

	public void crearUsuario(String nombreCliente) {
		cliente = new Cliente(nombreCliente, "localhost", 50000);
		cliente.inicializarHiloCliente(this);
	}

	public void activarBotones() {
		setTitle("Lobby - " + cliente.getNombre());
		btnCrearSala.setEnabled(true);
		listaSalas.setEnabled(true);
		menuItemConectarse.setEnabled(false);

		btnUnirseASala.setEnabled(true);
		btnBorrarSala.setEnabled(true);
		btnDesconexion.setEnabled(true);
	}

	protected void desconectarse() {
		MensajeAServidor msj = new MensajeAServidor(cliente.getNombre(), null, 0);
		cliente.enviarMensaje(msj);
	}

	public void cerrarSala(Sala sala) {
		SalaChat salaARemover = null;
		int i = 0;
		if (salasAbiertas.size() > 0) {
			salaARemover = salasAbiertas.get(i);
			while (!salaARemover.getNombreSala().equals(sala.getNombreSala())) {
				i++;
				salaARemover = salasAbiertas.get(i);
			}
			salasAbiertas.remove(salaARemover);
		}

	}



	public void enviarMensaje(MensajeAServidor mensaje) {
		cliente.enviarMensaje(mensaje);
	}

	protected void ingresarNombreSalaACrear() {
		String respuesta=JOptionPane.showInputDialog(this, "Ingrese nombre de sala:", "");
		if(respuesta!=null && !respuesta.equals("")) {
			Sala sala=new Sala(respuesta);
			MensajeAServidor msj=new MensajeAServidor(null,sala,2);
			cliente.enviarMensaje(msj);
		}
	}

	public void mostrarInfoSala() {
		int cantUsuarios = 0;
		String salaElegida = listaSalas.getSelectedValue();
		int i = 0;
		Sala sala = null;
		while (!(sala = salas.get(i)).getNombreSala().equals(salaElegida)) {
			i++;
		}
		cantUsuarios = sala.getCantUsuarios();
		textAreaInfoSala.setText("Informacion de la sala:\n Conectados:" + cantUsuarios);
	}

	protected void unirseASala() {
		String salaElegida = listaSalas.getSelectedValue();

		if (salaElegida != null) {
			int cantSalasAbiertas = salasAbiertas.size();
			if (cantSalasAbiertas < 3) {
				boolean abierta = false;
				for (int i = 0; i < cantSalasAbiertas; i++) {
					SalaChat salaAbierta = salasAbiertas.get(i);
					if (salaAbierta.getNombreSala().equals(salaElegida)) {
						abierta = true;
					}
				}
				if (abierta == false) {
					int i = 0;
					Sala sala = null;
					while (i < salas.size() && !(sala = salas.get(i)).getNombreSala().equals(salaElegida)) {
						i++;
					}
					MensajeAServidor msj = new MensajeAServidor(cliente.getNombre(), sala, 4);
					cliente.enviarMensaje(msj);
				} else {
					JOptionPane.showMessageDialog(this, "Esa sala ya esta abierta", "Sala abierta", JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(this, "Se pueden abrir 3 salas como maximo", "Cantidad de salas abiertas maximas", JOptionPane.WARNING_MESSAGE);
			}
		}
	}

	public void abrirSala(Sala sala) {
		salasAbiertas.add(new SalaChat(sala,cliente));
		mostrarInfoSala();
	}

	public void actualizarSalas(List<Sala> listaSalasActualizada) {
		salas = listaSalasActualizada;
		listModel.clear();
		for (Sala s : listaSalasActualizada) {
			listModel.addElement(s.getNombreSala());
		}
	}

	public void enviarDatosAlServidor() {
		MensajeAServidor msj=new MensajeAServidor(cliente.getNombre(),null,1);
		cliente.enviarMensaje(msj);
	}

	public void recibirMensaje(MensajeACliente mensaje) {
		Sala salaMensaje=mensaje.getSala();
		int i = 0;
		if (salasAbiertas.size() > 0) {
			SalaChat salaAbiertaActual;
			salaAbiertaActual = salasAbiertas.get(i);
			while (!salaAbiertaActual.getNombreSala().equals(salaMensaje.getNombreSala())) {
				salaAbiertaActual = salasAbiertas.get(i);
				i++;
			}
			salaAbiertaActual.mostrarMensaje(mensaje.getMensaje());
		}
		
	}
//
//	public void verTiempoSesion(SalaChat salaChat) {
//		int i=0;
//		Sala sala=salas.get(i);
//		while(true) {
//			
//		}
//		
//	}

	public void recibirTiempos(MensajeACliente mensaje) {
		Sala sala=mensaje.getSala();
		int i=0;
		SalaChat salaChat=salasAbiertas.get(i);
		while(!salaChat.getNombreSala().equals(sala.getNombreSala())) {
			i++;
			salaChat=salasAbiertas.get(i);
		}
		salaChat.mostrarTiempos(mensaje.getMensaje());
	}

}
