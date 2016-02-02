import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import javax.print.Doc;

import org.apache.commons.io.FileUtils;


public class InvertedIndexThread implements Callable<TreeMap<Integer, DocInfo>> {
	private String [] listOfFilesToProcess;
	private TreeMap<Integer, DocInfo> docInfoList;
	
	public InvertedIndexThread(){
		//empty ctor
	}
	
	public InvertedIndexThread(String [] filenames, TreeMap<Integer, DocInfo> docInfoList){
		this.listOfFilesToProcess = filenames;
		this.docInfoList = docInfoList;
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
	public TreeMap<Integer, DocInfo> call() {
		System.out.println("call");
		Map<String, Term> miniInvertedIndex = new HashMap<>();
		
		//File folder = new File("C:\\Users\\gogopavl\\git\\IRAssignment\\catalogue");
		File [] listOfFiles = new File[listOfFilesToProcess.length];
		
		for(int i = 0 ; i < listOfFiles.length ; ++i){
			listOfFiles[i] = new File("C:\\Users\\aintzevi\\git\\IRAssignment\\catalogue\\"+listOfFilesToProcess[i]);			
		}
		DocInfo currentDocInfo = null;
		
		for(File f : listOfFiles){
			
			currentDocInfo = new DocInfo();
			
	    	try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			    
	    		String line;
	    		// Get the docID, which is the name of the current file
	    		String docID = f.getName().replaceFirst(".txt", "");
	    		// Store the docID as an integer in the DocInfo object
	    		currentDocInfo.setDocID(Integer.parseInt(docID));
			    String mostFreqTerm = null;
			    int mostFreqTermFrequency = 0;
			    
	    		while ((line = br.readLine()) != null) {
			       
			    	StringTokenizer tokenizer = new StringTokenizer(line, " .,;:!*^/");
			    	String currentToken;
			    	
			    	while(tokenizer.hasMoreTokens()){
			    		currentToken = tokenizer.nextToken().toLowerCase();
			    		// Update the number of words
			    		currentDocInfo.setNumOfWords(currentDocInfo.getNumOfWords() + 1);
			    		
			    		// If the word is not in the hash table
			    		if (!miniInvertedIndex.containsKey(currentToken)) {
			    			TermFreqInDoc tempListElement = new TermFreqInDoc(Integer.parseInt(docID),1);
			    			Term t = new Term(currentToken, tempListElement);
			    			miniInvertedIndex.put(t.getWord() , t);
			    		}
			    		else {
			    			// index of the docId, freq arraylist, for current doc
		    				int tempFreq = miniInvertedIndex.get(currentToken).getIndexForGivenDocID(Integer.parseInt(docID));
		    				if(tempFreq >= 0 ){//Periptwsi pou uparxei to doc kai thelei enimerwsi to frequency tou orou
		    					
		    					int currentFreq = miniInvertedIndex.get(currentToken).getDocList().get(tempFreq).getTermFrequency();
		    					// Increment the term frequency for current term for this doc
		    					miniInvertedIndex.get(currentToken).getDocList().get(tempFreq).setTermFrequency(currentFreq + 1);
		    					
		    					if(miniInvertedIndex.get(currentToken).getDocList().get(tempFreq).getTermFrequency() >= mostFreqTermFrequency) {
		    						// Set the current most frequent word and its frequency to these of the current term
		    						System.out.println("Inside if" + mostFreqTerm + mostFreqTermFrequency);
		    						mostFreqTermFrequency = miniInvertedIndex.get(currentToken).getDocList().get(tempFreq).getTermFrequency();
		    						mostFreqTerm  = miniInvertedIndex.get(currentToken).getWord();
		    						System.out.println("Inside if" + mostFreqTerm + mostFreqTermFrequency);
		    					}
	    					}
		    				else{//periptwsi pou uparxei to term, exei lista apo docs, alla oxi to sugkekrimeno doc
		    					TermFreqInDoc tempListElement = new TermFreqInDoc(Integer.parseInt(docID),1);
		    					miniInvertedIndex.get(currentToken).getDocList().add(tempListElement);
		    				}
			    		}
			    	} // End of tokenizer while loop
			    } // End of read line loop
	    		currentDocInfo.setMostFreqWord(mostFreqTerm);
				currentDocInfo.setMostFreqWordFrequency(mostFreqTermFrequency);
				
			}
			catch(IOException e){ System.out.println("File not found");};	
			
			docInfoList.put(currentDocInfo.getDocID(), new DocInfo(currentDocInfo.getDocID(), 
					currentDocInfo.getNumOfWords(), currentDocInfo.getMostFreqWord(), 
					currentDocInfo.getMostFreqWordFrequency()));
	    } // End of for-loop
		
		/*
		for(Map.Entry<String, Term> entry : treeMap.entrySet()) {
			System.out.println(" Key : " + entry.getKey());
		}
		*/	
		
		try {
			System.out.println(binarySearch(new RandomAccessFile(new File("output\\outFile.txt"), "r"), "00"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Mini inverted index works");
		
		/*docInfoList.put(55, new DocInfo(55, 100, "example", 10));
		docInfoList.put(new Random().nextInt(100), new DocInfo(11, 100, "example", 10));
		*/
		
		//TODO 1. multithreading 2. merge mini inverted indexes 3. queries
		return docInfoList;
		
	}//end of call method
	
	
	/**
	 * Function to write the mini inverted index to file.
	 * Format : term numberOfDocuments docID,freq docID,freq
	 * 
	 * @param map Sorted map containing the terms, the number of docs in which it appears, and the pairs of docIDs and the frequency of the term
	 * in this specific doc
	 * @throws IOException
	 */
	public void writeInvertedIndexToFile (Map<String, Term> map) throws IOException {
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("output\\outFile.txt")));
		
		// Iterating through the map of terms
		for(Map.Entry<String, Term> entry : map.entrySet()) {
			String line = entry.getKey() + " " + entry.getValue().getDocList().size();
			
			for( int i = 0 ; i < entry.getValue().getDocList().size() ; ++i) {				
				line = line + " " + entry.getValue().getDocList().get(i).getDocId() + "," + entry.getValue().getDocList().get(i).getTermFrequency();
			}
			
			// Write line in file
			bw.write(line);
			bw.newLine();
		}
		// Close writer
		bw.close();
	}
	
	public static String binarySearch(RandomAccessFile file, String seekingTerm) throws IOException {
	    /*
	     * because we read the second line after each seek there is no way the
	     * binary search will find the first line, so check it first.
	     */
	    file.seek(0);
	    String line = file.readLine();
	    if (line == null) {
	    	return seekingTerm + " 0 0,0"; 
	    }
	    if (line.compareTo(seekingTerm) >= 0) {
	        /*
	         * the start is greater than or equal to the target, so it is what
	         * we are looking for.
	         */
	        return line;
	    }

	    /*
	     * set up the binary search.
	     */
	    long beg = 0;
	    long end = file.length();
	    while (beg <= end) {
	        /*
	         * find the mid point.
	         */
	        long mid = beg + (end - beg) / 2;
	        file.seek(mid);
	        file.readLine();
	        line = file.readLine();

	        if (line == null || line.compareTo(seekingTerm) >= 0) {
	            /*
	             * what we found is greater than or equal to the target, so look
	             * before it.
	             */
	            end = mid - 1;
	        } else {
	            /*
	             * otherwise, look after it.
	             */
	            beg = mid + 1;
	        }
	    }

	    /*
	     * The search falls through when the range is narrowed to nothing.
	     */
	    file.seek(beg);
	    file.readLine();
	    return file.readLine();
	    //return file.getFilePointer();
	    
	}
}


