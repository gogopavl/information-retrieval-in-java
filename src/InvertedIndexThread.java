import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;


public class InvertedIndexThread implements Callable<TreeMap<Integer, DocInfo>> {
	private String [] listOfFilesToProcess;
	private TreeMap<Integer, DocInfo> docInfoList;
	private ArrayList<String> docTermsForMagnitudeComputation = new ArrayList<String>();
	private String outname;
	
	public InvertedIndexThread(){
		//empty ctor
	}
	
	/**
	 * Constructor of the InvertedIndexThread
	 * @param filenames String Array with the filenames of the files to be processed by an InvertedIndexThread object
	 * @param docInfoList tree map with entries for each doc and it's information
	 */
	public InvertedIndexThread(String [] filenames, TreeMap<Integer, DocInfo> docInfoList, String outname){
		this.listOfFilesToProcess = filenames;
		this.docInfoList = docInfoList;
		this.outname = outname;
	};

	//comment
	/**
	 * Method executed in a thread
	 * Creating a mini inverted index for specified files and saving it in file
	 * 
	 * Returns a tree map with the info about the docs it processed
	 * @throws IOException 
	 * @Override 
	 */
	
	public TreeMap<Integer, DocInfo> call() throws IOException {
		// Hash map that implements the mini inverted index
		Map<String, Term> miniInvertedIndex = new TreeMap<>();
		
		//TODO Find way to ignore null entries - count list length without nulls
		int numOfLegitFiles = 0;
		for(int i = 0 ; i < listOfFilesToProcess.length ; ++i) {
			if(listOfFilesToProcess[i] != null){
				numOfLegitFiles++;
			}
		}
		// Creating list with the filenames of the files to create the index for
		File [] listOfFiles = new File[numOfLegitFiles];
		
		for(int i = 0 ; i < listOfFiles.length ; ++i) {
			if(listOfFilesToProcess[i] == null){
				System.out.println("NULL");
			}
			if(listOfFilesToProcess[i] != null){
				listOfFiles[i] = new File("C:\\Users\\gogopavl\\git\\IRAssignment\\catalogue\\" + listOfFilesToProcess[i]);			
			}
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
			    		
			    		if(!docTermsForMagnitudeComputation.contains(currentToken)){
			    			docTermsForMagnitudeComputation.add(currentToken);
			    		}
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
			    			// If the doc is already in the frequency list of this term - update the frequency value
			    			if(miniInvertedIndex.get(currentToken).getDocList().containsKey(Integer.parseInt(docID))) {
		    					// Get the current frequency for this doc ID
			    				int currentFreq = miniInvertedIndex.get(currentToken).getDocList().get(Integer.parseInt(docID)).getTermFrequency();
//			    				System.out.println("term freq: " + currentFreq);
			    				// Increment the term frequency for current term for this doc
			    				miniInvertedIndex.get(currentToken).getDocList().get(Integer.parseInt(docID)).setTermFrequency(currentFreq + 1);
//			    				System.out.println("Should be + 1: " + miniInvertedIndex.get(currentToken).getDocList().get(docID).getTermFrequency());
			    				// Getting the term with max frequency for the doc
			    				// If the current term frequency is greater or equal to current max
			    				if(miniInvertedIndex.get(currentToken).getDocList().get(Integer.parseInt(docID)).getTermFrequency() >= mostFreqTermFrequency) {
			    					// Update the values of current most frequent word, and its frequency to those of the current term
			    					mostFreqTermFrequency = miniInvertedIndex.get(currentToken).getDocList().get(Integer.parseInt(docID)).getTermFrequency();
			    					mostFreqTerm  = miniInvertedIndex.get(currentToken).getWord();
			    				}
			    			}
			    			// If the doc is not in the frequency list of this term - add it with frequency equal to 1
			    			else {
			    				TermFreqInDoc tempListElement = new TermFreqInDoc(Integer.parseInt(docID),1);
			    				miniInvertedIndex.get(currentToken).getDocList().put(tempListElement.getDocId(), tempListElement);
			    			}
			    		}
			    	} // End of tokenizer while loop
			    } // End of read line loop
			    
			    // Updating the object with the most frequent term in the doc, and its frequency
	    		currentDocInfo.setMostFreqWord(mostFreqTerm);
				currentDocInfo.setMostFreqWordFrequency(mostFreqTermFrequency);
				
			}
			catch(IOException e){ System.out.println("File not found");};	
			
			//call file writer
			writeDocUniqueWordsToFile(currentDocInfo.getDocID(), docTermsForMagnitudeComputation, "uniqueTermsPerDoc.txt");
			docTermsForMagnitudeComputation.clear();
			
			// Adding the doc info object in the tree map (to have it sorted by docID)
			docInfoList.put(currentDocInfo.getDocID(), new DocInfo(currentDocInfo.getDocID(), 
					currentDocInfo.getNumOfWords(), currentDocInfo.getMostFreqWord(), 
					currentDocInfo.getMostFreqWordFrequency()));
	    } // End of for-loop
		
		/*
		// To find a term in the index, use a binary search function
		try {
			System.out.println(binarySearch(new RandomAccessFile(new File("output\\outFile.txt"), "r"), "00"));
		} catch (IOException e) {
			System.out.println("IO Exception on binary search");
			e.printStackTrace();
		}*/
		writeInvertedIndexToFile(miniInvertedIndex, outname);
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
	public void writeInvertedIndexToFile (Map<String, Term> map, String outname) throws IOException {
		// Buffered Writer to write the index in file
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("output\\"+outname)));
		String line; 
		// Iterating through the map of terms
		for(Map.Entry<String, Term> entry : map.entrySet()) {
			// Creating a string with key and number of docs in which the term is found
			line = entry.getKey() + " " + entry.getValue().getDocList().size();
			
			// Iterating through the array list with each docID and the frequency of the term in it
			for(Map.Entry<Integer, TermFreqInDoc> pair : entry.getValue().getDocList().entrySet()){
				
				line = line + " " + pair.getValue().getDocId() + "," + pair.getValue().getTermFrequency();
			}
			
			// Write line in file
			bw.write(line);
			bw.newLine();
			
		}
		// Close writer
		bw.close();
	}
	public void writeDocUniqueWordsToFile(int currentDocumentId, ArrayList<String> uniqueTerms, String outname) throws IOException{
		// Buffered Writer to write the index in file
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outname),true));
		String line = Integer.toString(currentDocumentId);
		// Iterating through the map of terms
		for(String entry : uniqueTerms) {
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


