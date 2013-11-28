import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import prefuse.data.Table;
import prefuse.data.io.DelimitedTextTableReader;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*ArrayList<String> keys = new ArrayList<String>();
		Map <String, ArrayList<String>> details = new LinkedHashMap<String, ArrayList<String>>();
		BufferedReader br = null;
		 
		try {
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader("WHO.csv"));
			br.readLine();
			
			while ((sCurrentLine = br.readLine()) != null) {
				String[] all = sCurrentLine.split(",");
				ArrayList<String> stuff = new ArrayList<String>();
				for (int i = 1; i < all.length; i++) {
					stuff.add(all[i]);
				}
				keys.add(all[0]);
				details.put(all[0], stuff);
				
			}
			
			br.close();
 
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		for (String key : keys) {
			System.out.println("Country: " + key);
		}*/
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());

		JPanel panel = new JPanel(new BorderLayout());

		FisheyeMenu fem = FisheyeMenu.demo();
		panel.add(fem);

		JPanel panel2 = new JPanel(new BorderLayout());
		panel2.add(getCongress());

		frame.add(panel);
		frame.add(panel2);

		frame.pack();
		frame.setVisible(true);
	}

	public static Congress getCongress() {
		Table t = null;
		try {
			t = new DelimitedTextTableReader().readTable("fec.txt");
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(1);
		}

		return new Congress(t);
	}
}
