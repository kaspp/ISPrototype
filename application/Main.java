import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(950, 535);
		frame.setLayout(new BorderLayout());

		//
		JPanel congress = CongressIE.demoOnly();
		frame.add(congress, BorderLayout.LINE_END);
		frame.add(FisheyeMenu.demo(frame, congress), BorderLayout.LINE_START);
		//

		//frame.pack();
		frame.setVisible(true);
	}
}