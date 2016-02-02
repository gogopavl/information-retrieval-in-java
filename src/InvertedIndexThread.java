import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;


public class InvertedIndexThread implements Runnable{
	
	private String [] listOfFilesToProcess;
	public InvertedIndexThread(){
		//empty ctor
	}
	public InvertedIndexThread(String [] filenames){
		this.listOfFilesToProcess = filenames;
	};
	/*
	public invertedIndex(String [] filenames) throws FileNotFoundException, IOException{
		this.listOfFilesToProcess = filenames;
		//best sto directory klp
		//while !eyof
		File folder = new File("C:\\Users\\gogopavl\\git\\IRAssignment\\catalogue");
		File[] listOfFiles = folder.listFiles();
		//osa xwrane stin mnimi
		for (int i = 0; i < listOfFiles.length; i++) {
			
			File file = listOfFiles[i];
			//System.out.println("Reading file: " + file.getName());
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
				    				
				    				//System.out.println(file.getName() + " " + docNumber);
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
		
		//System.out.println(invertedIndex.get("a"));
	}
	*/

	@Override
	public void run() {
		
		HashMap<String, Term> miniInvertedIndex = new HashMap<>();
		
		//File folder = new File("C:\\Users\\gogopavl\\git\\IRAssignment\\catalogue");
		File [] listOfFiles = new File[listOfFilesToProcess.length];
		for(int i = 0 ; i < listOfFiles.length ; ++i){
			listOfFiles[i] = new File("C:\\Users\\gogopavl\\git\\IRAssignment\\catalogue\\"+listOfFilesToProcess[i]);			
		}
		for(File f : listOfFiles){
			
	    	try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			    
	    		String line;
	    		String docID = f.getName().replaceFirst(".txt", "");
			    while ((line = br.readLine()) != null) {
			       
			    	StringTokenizer tokenizer = new StringTokenizer(line, " .,;:!*^/");
			    	String currentToken;
			    	
			    	while(tokenizer.hasMoreTokens()){
			    		currentToken = tokenizer.nextToken().toLowerCase();
			    		
			    		// If the word is not in the hash table
			    		if (!miniInvertedIndex.containsKey(currentToken)) {
			    			TermFreqInDoc tempListElement = new TermFreqInDoc(Integer.parseInt(docID),1);
			    			Term t = new Term(currentToken, tempListElement);
			    			miniInvertedIndex.put(t.getWord() , t);
			    		}
			    		else {
		    				int tempFreq = miniInvertedIndex.get(currentToken).getIndexForGivenDocID(Integer.parseInt(docID));
		    				if(tempFreq >= 0 ){//Periptwsi pou uparxei to doc kai thelei enimerwsi to frequency tou orou
		    					int currentFreq = miniInvertedIndex.get(currentToken).getDocList().get(tempFreq).getTermFrequency();
		    					miniInvertedIndex.get(currentToken).getDocList().get(tempFreq).setTermFrequency(currentFreq + 1);		    				}
		    				else{//periptwsi pou uparxei to term, exei lista apo docs, alla oxi to sugkekrimeno doc
		    					TermFreqInDoc tempListElement = new TermFreqInDoc(Integer.parseInt(docID),1);
		    					miniInvertedIndex.get(currentToken).getDocList().add(tempListElement);
		    				}	    				
		    			
			    		}
			    	} // End of tokenizer while loop
			    } // End of read line loop
			}
			catch(IOException e){ System.out.println("File not found");};	
	    }	
		System.out.println("Mini inverted index works");
		//TODO 1. multithreading 2. write mini inverted indexes to files & merge 3. queries
	}//end of Run method
}


