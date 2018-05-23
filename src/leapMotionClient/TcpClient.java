/*-- 
	Copyright (c) 2015, 황준우, 최종철, 원호정, 조영남
 
    This file is licensed under a Creative Commons license: 
    http://creativecommons.org/licenses/by/4.0/ 
--*/

package leapMotionClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;

public class TcpClient {

	public TcpClient(String msg) {
		try {
			String serverIP = "192.168.150.14"; // raspberry-pi IP
			System.out.println("Send to : " + serverIP);

			Socket socket = new Socket(serverIP, 5000);

			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();
			DataInputStream dis = new DataInputStream(in);
			DataOutputStream dos = new DataOutputStream(out);

			dos.writeUTF(msg);
			String serverMsg = dis.readUTF();
			HomeSystemMain.DoorState = serverMsg.substring(0,
					serverMsg.indexOf(" "));
			HomeSystemMain.LightState = serverMsg.substring(serverMsg
					.indexOf(" ") + 1);

			if (msg.equals("State Check")) {
				HomeSystemMain.textFieldFrame.setTfDoor("Window : "
						+ HomeSystemMain.DoorState);
				HomeSystemMain.textFieldFrame.setTfLight("Light : "
						+ HomeSystemMain.LightState);
			}

			System.out.printf("%s\n", "Get message : " + serverMsg);

			dis.close();
			dos.close();
			socket.close();

		} catch (ConnectException ce) {
			ce.printStackTrace();
		} catch (IOException ie) {
			ie.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
