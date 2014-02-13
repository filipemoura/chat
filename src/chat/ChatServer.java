package chat;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChatServer {
	
	List<PrintWriter> escritores = new ArrayList<>();

	public ChatServer() {
		ServerSocket servidor;
		try {
			servidor = new ServerSocket(5000);
			while (true) {
				Socket socket = servidor.accept();
				new Thread(new EscutaCliente(socket)).start();
				PrintWriter print = new PrintWriter(socket.getOutputStream());
				escritores.add(print);
			}
		} catch (Exception e) {
		}
	}
	
	private void encaminharParaTodos(String texto) {
		for (PrintWriter pw : escritores) {
			try {
				pw.println(texto);
				pw.flush();
			} catch (Exception e) {
			}
		}
	}

	private class EscutaCliente implements Runnable {
		Scanner leitor;

		public EscutaCliente(Socket socket) {
			try {
				leitor = new Scanner(socket.getInputStream());
			} catch (Exception e) {
			}
		}

		@Override
		public void run() {
			try {
				String texto;
				while ((texto = leitor.nextLine()) != null) {
					System.out.println(texto);
					encaminharParaTodos(texto);
				}
			} catch (Exception e) {
			}
		}

	}

	public static void main(String[] args) {
		new ChatServer();
	}

}
