import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String> keys = new ArrayList<String>();
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
		}

	}

}
