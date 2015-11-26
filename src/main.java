import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		  System.out.println("Available processors (cores): " + 
		  Runtime.getRuntime().availableProcessors());
		
		  /* Total amount of free memory available to the JVM */
		  System.out.println("Free memory (bytes): " + 
		  Runtime.getRuntime().freeMemory());
		
		  File folder = new File("/home/gogopavl/workspace2/IRAssignment/catalogue");
		  File[] listOfFiles = folder.listFiles();
		  //osa xwrane stin mnimi
		  for (int i = 0; i < listOfFiles.length; i++) {
		    File file = listOfFiles[i];
		    if (file.isFile() && file.getName().endsWith(".txt")) {
		      String content = FileUtils.readFileToString(file);
		      /* do somthing with content */
		      
		      
		    } 
		  }
		  
		  invertedIndex i = new invertedIndex();
		  
		  System.out.println("Ended");
		  
	}

}
