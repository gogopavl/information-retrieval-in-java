import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.Callable;


public class InvertedIndexThread implements Callable<TreeMap<Integer, DocInfo>> {
	private String [] listOfFilesToProcess;
	private TreeMap<Integer, DocInfo> docInfoList;
	
	public InvertedIndexThread(){
		//empty ctor
	}
	
	/**
	 * Constructor of the InvertedIndexThread
	 * @param filenames String Array with the filenames of the files to be processed by an InvertedIndexThread object
	 * @param docInfoList tree map with entries for each doc and it's information
	 */
	InvertedIndexThread(String [] filenames, TreeMap<Integer, DocInfo> docInfoList){
		this.listOfFilesToProcess = filenames;
		this.docInfoList = docInfoList;
	};

	
	/**
	 * Method executed in a thread
	 * Creating a mini inverted index for specified files and saving it in file
	 * 
	 * Returns a tree map with the info about the docs it processed
	 * @Override 
	 */
	public TreeMap<Integer, DocInfo> call() {
		// Hash map that implements the mini inverted index
		Map<String, Term> miniInvertedIndex = new HashMap<>();
		
		// Creating list with the filenames of the files to create the index for
		File [] listOfFiles = new File[listOfFilesToProcess.length];
		
		for(int i = 0 ; i < listOfFiles.length ; ++i) {
			listOfFiles[i] = new File("C:\\Users\\aintzevi\\git\\IRAssignment\\catalogue\\" + listOfFilesToProcess[i]);			
		}
		
		// Object to keep the document info
		DocInfo currentDocInfo = null;
		
		// Iterating through the list of files to be processed
		for(File f : listOfFiles){
			// File-doc level
			
			// Initiating the object
			currentDocInfo = new DocInfo();
			
			// Reading the file
	    	try (BufferedReader br = new BufferedReader(new FileReader(f))) {
			    // String to store each line
	    		String line;
	    		// Get the docID, which is the name of the current file
	    		String docID = f.getName().replaceFirst(".txt", "");
	    		// Store the docID as an integer in the DocInfo object
	    		currentDocInfo.setDocID(Integer.parseInt(docID));
	    		
	    		// Variables to keep track of the most frequent term of the current doc (used for tf-idf calculations)
			    String mostFreqTerm = null;
			    int mostFreqTermFrequency = 0;
			    
			    // Reading every line of the file
			    while ((line = br.readLine()) != null) {
			    	// Tokenizer for the line, spliting on spaces and punctuation
			    	StringTokenizer tokenizer = new StringTokenizer(line, " .,;:!*^/");
			    	String currentToken;
		    		
			    	// Going through the tokens of the line
			    	while(tokenizer.hasMoreTokens()){
			    		currentToken = tokenizer.nextToken().toLowerCase();
			    		// Increment the number of words in the current doc - add info in object
			    		currentDocInfo.setNumOfWords(currentDocInfo.getNumOfWords() + 1);

			    		// If the current term is not inside the hash map already
			    		if (!miniInvertedIndex.containsKey(currentToken)) {
			    			
			    			// Adding the term with the docID and frequency in the inverted index
			    			// Current doc ID and frequency equal to 1 - It is the first time this term is found
			    			TermFreqInDoc tempListElement = new TermFreqInDoc(Integer.parseInt(docID),1);
			    			Term currentTerm = new Term(currentToken, tempListElement);
			    			miniInvertedIndex.put(currentTerm.getWord() , currentTerm);
			    		}
			    		// If the current term exists in mini inverted index hash map
			    		else {
			    			// Index of the docId, freq arraylist, for current doc
			    			int tempFreq = miniInvertedIndex.get(currentToken).getIndexForGivenDocID(Integer.parseInt(docID));
			    			// If the doc is already in the frequency list of this term - update the frequency value
			    			if(tempFreq >= 0 ) {
		    					// Get the current frequency for this doc ID
			    				int currentFreq = miniInvertedIndex.get(currentToken).getDocList().get(tempFreq).getTermFrequency();
			    				// Increment the term frequency for current term for this doc
			    				miniInvertedIndex.get(currentToken).getDocList().get(tempFreq).setTermFrequency(currentFreq + 1);
		    					
			    				// Getting the term with max frequency for the doc
			    				// If the current term frequency is greater or equal to current max
			    				if(miniInvertedIndex.get(currentToken).getDocList().get(tempFreq).getTermFrequency() >= mostFreqTermFrequency) {
			    					// Update the values of current most frequent word, and its frequency to those of the current term
			    					mostFreqTermFrequency = miniInvertedIndex.get(currentToken).getDocList().get(tempFreq).getTermFrequency();
			    					mostFreqTerm  = miniInvertedIndex.get(currentToken).getWord();
			    					System.out.println("Inside if" + mostFreqTerm + mostFreqTermFrequency);
			    				}
			    			}
			    			// If the doc is not in the frequency list of this term - add it with frequency equal to 1
			    			else {
			    				TermFreqInDoc tempListElement = new TermFreqInDoc(Integer.parseInt(docID),1);
			    				miniInvertedIndex.get(currentToken).getDocList().add(tempListElement);
			    			}
			    		}
			    	} // End of tokenizer while loop
			    } // End of read line loop
			    
			    // Updating the object with the most frequent term in the doc, and its frequency
	    		currentDocInfo.setMostFreqWord(mostFreqTerm);
				currentDocInfo.setMostFreqWordFrequency(mostFreqTermFrequency);
				
			}
			catch(IOException e){ System.out.println("File not found");};	
			
			// Adding the doc info object in the tree map (to have it sorted by docID)
			docInfoList.put(currentDocInfo.getDocID(), new DocInfo(currentDocInfo.getDocID(), 
					currentDocInfo.getNumOfWords(), currentDocInfo.getMostFreqWord(), 
					currentDocInfo.getMostFreqWordFrequency()));
	    } // End of for-loop
		
		/*
		// Printing tree map entries
		for(Map.Entry<String, Term> entry : treeMap.entrySet()) {
			System.out.println(" Key : " + entry.getKey());
		}
		*/	
		/*
		// To find a term in the index, use a binary search function
		try {
			System.out.println(binarySearch(new RandomAccessFile(new File("output\\outFile.txt"), "r"), "00"));
		} catch (IOException e) {
			System.out.println("IO Exception on binary search");
			e.printStackTrace();
		}*/
		
		System.out.println("Mini inverted index works");
		
		/*docInfoList.put(55, new DocInfo(55, 100, "example", 10));
		docInfoList.put(new Random().nextInt(100), new DocInfo(11, 100, "example", 10));
		*/
		
		// TODO 1. multithreading 
		// TODO 2. merge mini inverted indexes 
		// TODO 3. queries
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
		// Buffered Writer to write the index in file
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("output\\outFile.txt")));
		
		// Iterating through the map of terms
		for(Map.Entry<String, Term> entry : map.entrySet()) {
			// Creating a string with key and number of docs in which the term is found
			String line = entry.getKey() + " " + entry.getValue().getDocList().size();
			
			// Iterating through the array list with each docID and the frequency of the term in it
			for( int i = 0 ; i < entry.getValue().getDocList().size() ; ++i) {				
				// Add it to the string to be saved in file
				line = line + " " + entry.getValue().getDocList().get(i).getDocId() + "," + entry.getValue().getDocList().get(i).getTermFrequency();
			}
			
			// Write line in file
			bw.write(line);
			bw.newLine();
		}
		// Close writer
		bw.close();
	}
	
	/**
	 * Function that implements binary search in a file
	 * 
	 * @param file File to do the binary search on
	 * @param seekingTerm The term to be seeked inside the file
	 * @return The line in which the term is found inside the file as a string
	 * @throws IOException
	 */
	public static String binarySearch(RandomAccessFile file, String seekingTerm) throws IOException {
	    // Check the first line of the file in case the word we are looking for is lexicographically before that
		// Means it is not in this index
	    file.seek(0);
	    // Read the line
	    String line = file.readLine();
	    
	    // If the line is empty - the term wasn't found
	    if (line == null) 
	    	// Return the term the user seeks with 0 in every other field
	    	return seekingTerm + " 0 0,0"; 
	    
	    // If it is greater or equal than the term, this is the line we are searching for - return it
	    if (line.compareTo(seekingTerm) >= 0)
	        return line;

	    // Binary Search
	    
	    // Beginning and end of file
	    long beg = 0;
	    long end = file.length();
	    
	    while (beg <= end) {
	        // Get the mid point of the file
	        long mid = beg + (end - beg) / 2;
	        
	        // Get to the middle of the file
	        file.seek(mid);
	        // Read line in case the the middle of the file is in the middle of the line
	        file.readLine();
	        
	        // Get the next full line
	        line = file.readLine();

	        // If the term found is lexicographically greater than or equal to the word we are seeking
	        if (line == null || line.compareTo(seekingTerm) >= 0)
	            // Update the end of binary search space to the place before the current middle
	            end = mid - 1;
	        else
	        	// Update the beginning of binary search space to the place after the current middle
	            beg = mid + 1;
	    } // End of binary search while-loop

	    // The search falls through when the range is narrowed to nothing.
	    file.seek(beg);
	    file.readLine();
	    
	    // Return the line containing the term
	    return file.readLine();
	} // End of binary search function
}


