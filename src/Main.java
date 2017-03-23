import java.io.IOException;

import socket.CreateSocket;

public class Main {
	/**
	 * 実行メインクラス
	 *
	 * @author FXS
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
		//Create Socket
		System.out.println("Socket Start");
		CreateSocket.getInstance(args).CreateSockets();
	}
}
