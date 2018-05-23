/*-- 
	Copyright (c) 2015, 황준우, 최종철, 원호정, 조영남
 
    This file is licensed under a Creative Commons license: 
    http://creativecommons.org/licenses/by/4.0/ 
--*/

package leapMotionClient;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class TextFieldFrame extends JFrame {

	private JTextField TfState;
	private JTextField TfDoor;
	private JTextField TfLight;
	private JButton btnStart;
	private JButton btnStop;

	public TextFieldFrame() {
		super("Home System Control Panel");
		setLayout(new FlowLayout());

		// Initial TextFields
		TfDoor = new JTextField("Uneditable text field", 9);
		TfDoor.setEditable(false);
		TfDoor.setText("Window : " + HomeSystemMain.DoorState);
		add(TfDoor);

		TfLight = new JTextField("Uneditable text field", 9);
		TfLight.setEditable(false);
		TfLight.setText("Light : " + HomeSystemMain.LightState);
		add(TfLight);

		TfState = new JTextField("Uneditable text field", 19);
		TfState.setEditable(false);
		add(TfState);

		// Initial Buttons
		btnStart = new JButton();
		btnStop = new JButton();
		btnStart.setText("Start");
		btnStop.setText("Stop");
		add(btnStart);
		add(btnStop);

		// Button's Action
		Handler handler = new Handler();
		btnStart.addActionListener(handler);
		btnStop.addActionListener(handler);

	}

	public void setTfState(String str) {
		TfState.setText(str);
	}

	public void setTfDoor(String str) {
		TfDoor.setText(str);
	}

	public void setTfLight(String str) {
		TfLight.setText(str);
	}

	private class Handler implements ActionListener {
		public void actionPerformed(ActionEvent event) {

			if (event.getSource() == btnStart) {
				HomeSystemMain.control.addListener(HomeSystemMain.utilListener);
				new TcpClient("State Check");
				HomeSystemMain.textFieldFrame.setTfState("State : Ready");

			} else if (event.getSource() == btnStop) {
				HomeSystemMain.control
						.removeListener(HomeSystemMain.utilListener);
				HomeSystemMain.textFieldFrame.setTfState("State : Stop");
				HomeSystemMain.textFieldFrame.setTfDoor("Window : -");
				HomeSystemMain.textFieldFrame.setTfLight("Light : -");

			}

		}
	}
}
