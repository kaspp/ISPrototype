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

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(FisheyeMenu.demo());
		frame.add(panel);

		JPanel panel2 = new JPanel(new BorderLayout());
		panel2.add(Congress.demoOnly());
		frame.add(panel2);

		//frame.pack();
		frame.setVisible(true);
	}
<<<<<<< HEAD
}
=======
}
>>>>>>> a095d7fbd0e558e08140b8ea11a471102e1b9a21
