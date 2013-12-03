import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1300, 600);
		frame.setLayout(new FlowLayout());

		JPanel congressPanel = new JPanel(new BorderLayout());
		congressPanel.add(CongressIE.demoOnly());

		JPanel femPanel = new JPanel(new BorderLayout());
		femPanel.add(FisheyeMenu.demo(frame, congressPanel));

		frame.add(femPanel);
		frame.add(congressPanel);

		//frame.pack();
		frame.setVisible(true);
	}
}