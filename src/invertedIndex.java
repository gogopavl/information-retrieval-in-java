import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;


public class invertedIndex {
	
	private   Hashtable<String, ArrayList<Integer>> invertedIndex = new Hashtable<>();
	

	public invertedIndex() throws FileNotFoundException, IOException{
		//best sto directory klp
		//while !eyof
		File folder = new File("/home/gogopavl/workspace2/IRAssignment/catalogue");
		File[] listOfFiles = folder.listFiles();
		//osa xwrane stin mnimi
		for (int i = 0; i < listOfFiles.length; i++) {
			
			File file = listOfFiles[i];
			System.out.println("Reading file: " + file.getName());
			if (file.isFile() && file.getName().endsWith(".txt")) {
		    	try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				    String line;
				    int docNumber = Integer.parseInt(file.getName().replace(".txt", ""));
				    
				    while ((line = br.readLine()) != null) {
				       
				    	StringTokenizer tokenizer = new StringTokenizer(line, " .,;:!*^/");
				    	String temp;
				    	while(tokenizer.hasMoreTokens()){
				    		temp = tokenizer.nextToken();
				    		
				    		// If the word is not in the hash table
				    		if (!invertedIndex.containsKey(temp)) {
				    			ArrayList<Integer> docs = new ArrayList<>();
				    			if (temp.equals("sister")) {
				    				
				    				System.out.println(file.getName() + " " + docNumber);
				    			}
				    			docs.add(docNumber);
				    			invertedIndex.put(temp, docs);
				    		}
				    		else {
				    			// If the current document is not in the array list, add it
				    			if (!invertedIndex.get(temp).contains(docNumber)) {
				    				invertedIndex.get(temp).add(docNumber);
				    			}
				    			else {
				    				
				    			}	
				    		}
				    	} // End of tokenizer while loop
				    } // End of read line loop
				}
				catch(FileNotFoundException e){ System.out.println("File not found");};
				
		      
		    } 
		}
		
		System.out.println(invertedIndex.get("a"));
	}
	/*
	
	@Override
	public int hashCode(){
 
        long hash = 5381;

        for (int i = 0; i < str.length(); i++) {
            hash = ((hash << 5) + hash) + str.charAt(i);
        }

        return hash;

	}
	*/
}


