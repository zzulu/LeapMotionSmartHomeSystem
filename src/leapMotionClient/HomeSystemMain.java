/*-- 
	Copyright (c) 2015, 황준우, 최종철, 원호정, 조영남
 
    This file is licensed under a Creative Commons license: 
    http://creativecommons.org/licenses/by/4.0/ 
--*/

package leapMotionClient;

import javax.swing.JFrame;

import com.leapmotion.leap.CircleGesture;
import com.leapmotion.leap.Controller;
import com.leapmotion.leap.Frame;
import com.leapmotion.leap.Gesture;
import com.leapmotion.leap.GestureList;
import com.leapmotion.leap.Listener;
import com.leapmotion.leap.SwipeGesture;

public class HomeSystemMain {

	// GUI
	public static TextFieldFrame textFieldFrame;

	// Home system state
	public static String DoorState = "-";
	public static String LightState = "-";

	// Leap Motion
	public static LeapUtil utilListener;
	public static Controller control;

	public static void main(String[] args) {

		// Leap Motion
		utilListener = new LeapUtil();
		control = new Controller();

		// GUI
		textFieldFrame = new TextFieldFrame();
		textFieldFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		textFieldFrame.setSize(300, 110);
		textFieldFrame.setVisible(true);

		textFieldFrame.setTfState("State : Stop");

	}

}

class LeapUtil extends Listener {

	private String openWindow = "Open Window";
	private String closeWindow = "Close Window";
	private String onLight = "Turn On Light";
	private String offLight = "Turn Off Light";

	private Frame frame;

	public void onInit(Controller controller) {
		System.out.println("Initialized");
	}

	public void onConnect(Controller controller) {
		System.out.println("Connected");
		controller.enableGesture(Gesture.Type.TYPE_SWIPE);
		controller.enableGesture(Gesture.Type.TYPE_CIRCLE);
	}

	public void onDisconnect(Controller controller) {
		System.out.println("Disconnected");
	}

	public void onExit(Controller controller) {
		System.out.println("Exited");
	}

	public void onFrame(Controller controller) {

		frame = controller.frame();
		GestureList gestures = frame.gestures();

		HomeSystemMain.textFieldFrame.setTfState("State : Ready");

		for (int i = 0; i < gestures.count(); i++) {
			Gesture gesture = gestures.get(i);

			switch (gesture.type()) {
			case TYPE_CIRCLE:
				CircleGesture circle = new CircleGesture(gesture);

				String clockwiseness = "-"; // clockwise or counterclockwise

				if (circle.pointable().direction().angleTo(circle.normal()) <= Math.PI / 2
						&& HomeSystemMain.LightState.equals("Off")) {
					// Clockwise if angle is less than 90 degrees
					clockwiseness = "clockwise";

					HomeSystemMain.textFieldFrame.setTfState("State : " + onLight);
					new TcpClient("Turn On Light");
					HomeSystemMain.textFieldFrame.setTfLight("Light : " + HomeSystemMain.LightState);

					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				} else if (circle.pointable().direction().angleTo(circle.normal()) > Math.PI / 2
						&& HomeSystemMain.LightState.equals("On")) {
					clockwiseness = "counterclockwise";

					HomeSystemMain.textFieldFrame.setTfState("State : " + offLight);
					new TcpClient("Turn Off Light");
					HomeSystemMain.textFieldFrame.setTfLight("Light : " + HomeSystemMain.LightState);

					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				// Display Circle Information
				/*
				 * System.out.println("  Circle id: " + circle.id() + ", " +
				 * circle.state() + ", progress: " + circle.progress() +
				 * ", radius: " + circle.radius() + ", angle: " +
				 * Math.toDegrees(sweptAngle) + ", " + clockwiseness);
				 */

				break;

			case TYPE_SWIPE:

				SwipeGesture swipe = new SwipeGesture(gesture);

				if (swipe.direction().getX() >= 0
						&& Math.abs(swipe.direction().getY()) <= 0.4
						&& Math.abs(swipe.direction().getZ()) <= 0.4
						&& swipe.speed() >= 150
						&& HomeSystemMain.DoorState.equals("Close")) {
					HomeSystemMain.textFieldFrame.setTfState("State : "
							+ openWindow);
					System.out.printf("%s\n", openWindow);
					new TcpClient(openWindow);
					HomeSystemMain.textFieldFrame.setTfDoor("Window : "
							+ HomeSystemMain.DoorState);

				} else if (swipe.direction().getX() <= 0
						&& Math.abs(swipe.direction().getY()) <= 0.4
						&& Math.abs(swipe.direction().getZ()) <= 0.4
						&& swipe.speed() >= 150
						&& HomeSystemMain.DoorState.equals("Open")) {
					HomeSystemMain.textFieldFrame.setTfState("State : "
							+ closeWindow);
					System.out.printf("%s\n", closeWindow);
					new TcpClient(closeWindow);
					HomeSystemMain.textFieldFrame.setTfDoor("Window : "
							+ HomeSystemMain.DoorState);

				} else {

				}

				// Display Swipe Information
				/*
				 * System.out.println("  Swipe id: " + swipe.id() + ", " +
				 * swipe.state() + ", position: " + swipe.position() +
				 * ", direction: " + swipe.direction() + ", speed: " +
				 * swipe.speed()); System.out.println("");
				 */

				break;

			default:
				System.out.println("Unknown gesture type.");
				break;
			}
		}
	}
}
