package chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatCliente extends JFrame {

	private static final long serialVersionUID = 1L;
	JTextField textoParaEnviar;
	Socket socket;
	PrintWriter escritor;
	String nome;
	JTextArea textoRecebido;
	Scanner leitor;
	JScrollPane scroll;
	JScrollBar auto;

	public ChatCliente(String nome) {
		super("Chat: " + nome);
		this.nome = nome;

		Font fonteBotao = new Font("Serif", Font.BOLD, 20);
		Font fonteTexto = new Font("Serif", Font.PLAIN, 18);
		textoParaEnviar = new JTextField();
		textoParaEnviar.setFont(fonteTexto);
		JButton botao = new JButton("Enviar");
		botao.setFont(fonteBotao);
		getRootPane().setDefaultButton(botao);
		botao.addActionListener(new EnviarListener());

		Container envio = new JPanel();
		envio.setLayout(new BorderLayout());
		envio.add(BorderLayout.CENTER, textoParaEnviar);
		envio.add(BorderLayout.EAST, botao);

		textoRecebido = new JTextArea();
		textoRecebido.setBackground(Color.BLACK);
		textoRecebido.setFont(fonteTexto);
		textoRecebido.setEnabled(false);
		scroll = new JScrollPane(textoRecebido);
		auto = new JScrollBar();
		auto = scroll.getVerticalScrollBar();

		getContentPane().add(BorderLayout.CENTER, scroll);
		getContentPane().add(BorderLayout.SOUTH, envio);

		configurarRede();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 500);
		setVisible(true);

	}

	private class EscutaServidor implements Runnable {

		@Override
		public void run() {
			try {
				String texto;
				while ((texto = leitor.nextLine()) != null) {
					textoRecebido.append(texto + "\n");
					auto.setValue(auto.getMaximum());
					
				}
			} catch (Exception e) {
			}
		}

	}

	private class EnviarListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (!(textoParaEnviar.getText()).isEmpty()) {
				escritor.println(nome + " : " + textoParaEnviar.getText());
				escritor.flush();
				textoParaEnviar.setText("");
				textoParaEnviar.requestFocus();
			}
		}

	}

	private void configurarRede() {
		try {
			socket = new Socket("127.0.0.1", 5000);
			escritor = new PrintWriter(socket.getOutputStream());
			leitor = new Scanner(socket.getInputStream());
			new Thread(new EscutaServidor()).start();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null,
					"Servidor desligado. Consulte o administrator do sistema.",
					"Chat", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		boolean loginAtivo = false;
		String nickName = null;

		while (!loginAtivo) {
			nickName = JOptionPane.showInputDialog(null, "Digite seu nome: ",
					"Chat", JOptionPane.QUESTION_MESSAGE);
			if (!nickName.isEmpty() && nickName != null) {
				loginAtivo = true;
			} else {
				JOptionPane.showMessageDialog(null,
						"Nome inválido. É necessário digitar um nome.", "Chat",
						JOptionPane.ERROR_MESSAGE);
			}
		}

		String[] primeiroNome = nickName.split(" ");
		nickName = primeiroNome[0];
		nickName = nickName.toUpperCase();

		new ChatCliente(nickName);
	}

}
