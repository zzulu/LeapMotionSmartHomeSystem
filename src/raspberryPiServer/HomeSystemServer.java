/*-- 
Copyright (c) 2015, 황준우, 최종철, 원호정, 조영남

This file is licensed under a Creative Commons license: 
http://creativecommons.org/licenses/by/4.0/ 
--*/

package raspberryPiServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class HomeSystemServer {

	public static String DoorState = "Close";
	public static String LightState = "Off";

	@SuppressWarnings("resource")
	public static void main(String[] args) {

		// Initialize
		final GpioController gpio = GpioFactory.getInstance();
		GpioPinDigitalOutput dir = gpio.provisionDigitalOutputPin(
				RaspiPin.GPIO_00, "Dir");
		GpioPinDigitalOutput step = gpio.provisionDigitalOutputPin(
				RaspiPin.GPIO_01, "Step");
		GpioPinDigitalOutput led = gpio.provisionDigitalOutputPin(
				RaspiPin.GPIO_05, "LED");

		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(5000);
			System.out.println(getTime() + " Server is ready.");
		} catch (IOException e) {
			e.printStackTrace();
		}

		while (true) {
			try {
				System.out.println(getTime() + " Wait for connection request\n");

				Socket socket = serverSocket.accept();
				System.out.println(getTime() + " Get request from "
						+ socket.getInetAddress());

				InputStream in = socket.getInputStream();
				DataInputStream dis = new DataInputStream(in);
				OutputStream out = socket.getOutputStream();
				DataOutputStream dos = new DataOutputStream(out);

				String msg = dis.readUTF();

				System.out.printf("%s\n", getTime() + " Message from client : "
						+ msg);

				if (msg.equals("Open Window")) {
					new Motor().rotate(90, 0.03, dir, step);
					DoorState = "Open";
				} else if (msg.equals("Close Window")) {
					new Motor().rotate(-90, 0.03, dir, step);
					DoorState = "Close";
				} else if (msg.equals("Turn On Light")) {
					led.high();
					System.out.println(getTime() + " Turn On Light");
					LightState = "On";
				} else if (msg.equals("Turn Off Light")) {
					led.low();
					System.out.println(getTime() + " Turn Off Light");
					LightState = "Off";
				} else if (msg.equals("State Check")) {
					System.out.println(getTime() + " State Check");
				}

				dos.writeUTF(DoorState + " " + LightState); // Send message to client

				System.out.println(getTime() + " Sending Data Compelete");

				dos.close();
				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
				gpio.shutdown();
			}

		}

	} 

	public static String getTime() {
		SimpleDateFormat f = new SimpleDateFormat("[hh:mm:ss]");
		return f.format(new Date());
	}

}

class Motor {
	public void rotate(int deg, double speed, GpioPinDigitalOutput mDir,
			GpioPinDigitalOutput mStep) {

		PinState pinstate = (deg > 0) ? PinState.HIGH : PinState.LOW;
		mDir.setState(pinstate);

		double steps = Math.abs(deg) * (1 / 0.225);
		long usDelay = (long) (1 / speed) * 70;

		usDelay /= 1000;

		if (pinstate == PinState.HIGH) {
			System.out.println(HomeSystemServer.getTime() + " Open Window...");
		} else if (pinstate == PinState.LOW) {
			System.out.println(HomeSystemServer.getTime() + " Close Window...");
		}

		for (int i = 0; i < (int) steps; i++) {

			mStep.toggle();
			try {
				Thread.sleep(usDelay);
			} catch (InterruptedException e) {

			}

			mStep.toggle();
			try {
				Thread.sleep(usDelay);
			} catch (InterruptedException e) {

			}

		}
		System.out.println(HomeSystemServer.getTime() + " Complete");
	}
}
