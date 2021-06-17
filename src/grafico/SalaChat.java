
package grafico;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import paquete.Cliente;
import paquete.MensajeAServidor;
import paquete.Sala;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class SalaChat extends JFrame {

	private static final long serialVersionUID = -4289720049025252601L;
	private JPanel contentPane;
	private JTextField textFieldEscrituraMensaje;
	JTextArea textArea;
	Cliente cliente;
	String nombre;
	JButton btnEnviar;
	private Sala sala;

	/**
	 * Create the frame.
	 * 
	 * @param sala
	 */
	public SalaChat(Sala sala, Cliente cliente) {
		this.sala = sala;
		this.cliente = cliente;
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				cerrarSala();
			}
		});
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		this.nombre = sala.getNombreSala();
		this.setTitle(nombre + " - " + cliente.getNombre());

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menuListaConexion = new JMenu("Conexion");
		menuBar.add(menuListaConexion);

		JMenuItem menuItemSalirSala = new JMenuItem("Salir de la sala");
		menuItemSalirSala.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}

		});
		menuListaConexion.add(menuItemSalirSala);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BorderLayout(0, 0));

		textFieldEscrituraMensaje = new JTextField();
		textFieldEscrituraMensaje.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==10) {
					enviarMensaje();
				}
			}
		});
		panel.add(textFieldEscrituraMensaje);
		textFieldEscrituraMensaje.setColumns(10);

		btnEnviar = new JButton("Enviar");
		btnEnviar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enviarMensaje();
				
			}

		});
		panel.add(btnEnviar, BorderLayout.EAST);

		textArea = new JTextArea();
		textArea.setEditable(false);
		contentPane.add(textArea, BorderLayout.CENTER);
		setVisible(true);
	}

	protected void cerrarSala() {
		MensajeAServidor msj = new MensajeAServidor(cliente.getNombre(), sala, 5);
		cliente.enviarMensaje(msj);
	}

	private void enviarMensaje() {
		String msj =textFieldEscrituraMensaje.getText();
		if(!msj.equals("")) {
			msj = cliente.getNombre() + ":" + textFieldEscrituraMensaje.getText();
			MensajeAServidor msjAServidor = new MensajeAServidor(msj, sala, 6);
			cliente.enviarMensaje(msjAServidor);
			textFieldEscrituraMensaje.setText("");			
		}

	}

	public String getNombreSala() {
		return nombre;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((nombre == null) ? 0 : nombre.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SalaChat other = (SalaChat) obj;
		if (nombre == null) {
			if (other.nombre != null)
				return false;
		} else if (!nombre.equals(other.nombre))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SalaChat [ nombre=" + nombre + "]";
	}

	public void mostrarMensaje(String mensaje) {
		textArea.setText(textArea.getText() + "\n" + mensaje);
	}

}