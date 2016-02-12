import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class tester {
	
	public static void main(String[] args) throws IOException{
		
		// Buffered Writer to write the index in file
				String outname = "temp.txt";
				String [] one = {"example","hello","world"};
				String [] two = {"example2","hello2","world2"};
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outname)));
				String line = Integer.toString(5);
				// Iterating through the map of terms
				for(String entry : one) {
					// Creating a string with all terms
					line +=  " " + entry;					
				}
				// Write line in file
				bw.write(line);
				bw.newLine();
				// Close writer
				bw.close();
				
				line = Integer.toString(15);
				bw = new BufferedWriter(new FileWriter(new File(outname),true));
				for(String entry : two) {
					// Creating a string with all terms
					line +=  " " + entry;					
				}
				// Write line in file
				bw.write(line);
				bw.newLine();
				// Close writer
				bw.close();
			
	}
	
	
	
}
