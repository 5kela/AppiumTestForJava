package socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import config.Config;

public class CreateSocket {

	private static CreateSocket instance = null;

	private static int port = 8000;

	private static String serverName = "Appium";

	private static String objectType = "app";
	
	private final static String END = "end";

	private final static String EXIT = "exit";

	private final static String WEB = "web";

	private final static String APP = "app";

	/**
	 * 実例を作成する
	 */
	public static CreateSocket getInstance(String[] args) {
		if (null == instance) {
			instance = new CreateSocket();
		}
		if (args != null && args.length > 2) {
			port = Integer.parseInt(args[0]);
			serverName = args[1];
			objectType = args[2];
		}
		return instance;
	}

	/**
	 * Create Socket
	 */
	public void CreateSockets() throws IOException {
		Socket socket = null;
		String[] argsSocket = {"","","","",""};
		ServerSocket server = null;
		try {
			server = new ServerSocket(port);
			socket = server.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}

		InputStream in = null;
		OutputStream out = null;
		try {
			in = socket.getInputStream();
			out = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Buffered Reader
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		// Buffered Writer
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(out));

		//server_nameの設定
		argsSocket[4] = serverName;

		int i = 0;
		String content = "";
		while (true) {
			try {
				if ((content = br.readLine()) != null) {
					argsSocket[i] = content;
					i++;
					if (CreateSocket.END.equals(content)) {
						if (APP.equals(objectType)) {
							Config.config().executeApp(argsSocket);
						} else if(WEB.equals(objectType)) {
							Config.config().executeWeb(argsSocket);
						}
						 bw.write(argsSocket[0]);
						 bw.flush();
						 i = 0;
					}
					if (CreateSocket.EXIT.equals(content)) {
						if (null != socket) {
							socket.close();
						}
						if(null != server)
						{
							server.close();
						}
						break;
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
