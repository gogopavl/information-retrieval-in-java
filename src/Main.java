import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		
		final ExecutorService service;
		ArrayList<Future<TreeMap<Integer, DocInfo>>> taskList = new ArrayList<>();
		
		int numberOfThreads = (Runtime.getRuntime().availableProcessors())/2;
		if( Runtime.getRuntime().availableProcessors()%2 != 0){
			numberOfThreads +=1;
		}
		// Creating pool with the number of threads wanted
		service = Executors.newFixedThreadPool(numberOfThreads);
		
		// Creating tree map to keep information about each document
		// The tree map has the entries sorted by the key, which here is the id of the doc 
		TreeMap<Integer, DocInfo> docInfoList = new TreeMap<>();
		
		// Get list of files from the specified folder

//		File folder = new File("C:\\Users\\aintzevi\\git\\IRAssignment\\catalogue");
		File folder = new File("C:\\Users\\gogopavl\\git\\IRAssignment\\catalogue");
		File[] listOfFiles = folder.listFiles();
		
		int numOfFilesPerThread = listOfFiles.length/numberOfThreads;

		if(listOfFiles.length%numberOfThreads != 0){
			numOfFilesPerThread += (numberOfThreads -listOfFiles.length%numberOfThreads);
		}
		
		
		// Storing the filenames to be send to a thread in a string array list
		String [] filesToSend = new String[numOfFilesPerThread];
		
		// For each file (Fit in main memory)
		int count = 0;
		int outputFileNameCounter = 0;
		
		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile() && file.getName().endsWith(".txt") && count < numOfFilesPerThread) {
				// Add the filename to the Sting array
				filesToSend[count] = file.getName();
				count++;
			}
			if(count >= numOfFilesPerThread){
				taskList.add(service.submit(new InvertedIndexThread(filesToSend, docInfoList, "out"+outputFileNameCounter+".txt")));
				filesToSend = new String[numOfFilesPerThread];
				count = 0;
				outputFileNameCounter++;
			}
		}
		if(filesToSend[0] != null)//tempName != 0 vale allo ena task)
		{
			taskList.add(service.submit(new InvertedIndexThread(filesToSend, docInfoList, "out"+outputFileNameCounter+".txt")));
		}
		// Allocating to a thread the files to be processed by it and the tree map with info about the documents
		for(Future<TreeMap<Integer, DocInfo>> t : taskList){
			t.get();
		}
		docMagnitudeCalculator(docInfoList);
		for(Map.Entry<Integer, DocInfo> entry : docInfoList.entrySet()) {
			System.out.println(" Key : " + entry.getKey() + " Num of Words : " + entry.getValue().getNumOfWords() + 
					" Most Frequent Word : " + entry.getValue().getMostFreqWord() + 
					" Most Frequent Word Frequency : " + entry.getValue().getMostFreqWordFrequency() +
					" Document magnitude: " + entry.getValue().getDocMagnitude());
		}
		
		// Shutting down the executor service
		service.shutdownNow();
		
//		File outFolder = new File("C:\\Users\\aintzevi\\git\\IRAssignment\\output");
		File outFolder = new File("C:\\Users\\gogopavl\\git\\IRAssignment\\output");

		twoWayMerge(new File( outFolder + "\\out0.txt"), new File( outFolder + "\\out1.txt"), outFolder + "\\merged.txt");
	}
	public static TreeMap<Integer, DocInfo> docMagnitudeCalculator (TreeMap<Integer, DocInfo> docList) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader("uniqueTermsPerDoc.txt"));
		String line;
		String [] termList;
		String lineFetched;
		String [] temp = null;
		String [] tempDocPair;
		Double docMagnitude = 0.0;
		Double currentTermFrequency = 0.0;
		Double tf;
		Double idf;
		
		
		while((line = br.readLine()) != null){
			termList = line.split(" ");
			for(int i = 1 ; i < termList.length ; ++i){
				lineFetched = binarySearch(new RandomAccessFile("output\\merged.txt","r"), termList[i]);
				temp = lineFetched.split(" ");
				for(int j = 2 ; j < temp.length ; ++j ){
					tempDocPair = temp[j].split(",");
					if(Integer.parseInt(tempDocPair[0]) == Integer.parseInt(termList[0])){
						 currentTermFrequency = Double.parseDouble(tempDocPair[1]);		
						 break;
					}
				}
				
				tf = currentTermFrequency / (docList.get(Integer.parseInt(termList[0])).getMostFreqWordFrequency());
				idf = (Math.log(docList.size() / Double.parseDouble(temp[1]))) / Math.log(2);
				docMagnitude += Math.pow((tf * idf), 2.0) ; 
				System.out.println("tf: " +tf+ " idf: "+idf+ " mag: ");
				System.out.printf("%.5f", docMagnitude);
				currentTermFrequency = 0.0;
			}
			System.out.println("break");
			docList.get(Integer.parseInt(termList[0])).setDocMagnitude(Math.sqrt(docMagnitude));
			docMagnitude = 0.0;
		}
		return docList;
		
	}
	
	public void externalMergeFunction(File folder){
		// List with output files
		File [] filesToBeSorted = folder.listFiles();
		
		// diagrafh arxeiwn inpout ths twoWayMerge
		// pairs of files from filesToBeSorted (check if odd first)
		// call twoWayMerge for each pair
		// Give proper merged files names
		// Rename left out files properly
		
	}
	
	public static void twoWayMerge(File one, File two, String outname) throws IOException {
		BufferedReader brOne = new BufferedReader(new FileReader(one));
		BufferedReader brTwo = new BufferedReader(new FileReader(two));
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outname)));
		
		String lineOne = brOne.readLine();
		String lineTwo = brTwo.readLine();
		
		String[] tokenizedLineOne = lineOne.split(" ");
		String[] tokenizedLineTwo = lineTwo.split(" ");
		
		String mergedLine = null;
		
		while ((lineOne != null) && (lineTwo != null )) {
//			System.out.println("Inside while ");

			// The string from line one is lexicographically smaller
			if(tokenizedLineOne[0].compareTo(tokenizedLineTwo[0]) < 0) {
				// Write the first line in out file
				bw.write(lineOne);
				bw.newLine();
				// Write line in file
				lineOne = brOne.readLine();
				tokenizedLineOne = lineOne.split(" ");
			}
			// The string from line two is lexicographically smaller
			else if(tokenizedLineOne[0].compareTo(tokenizedLineTwo[0]) > 0) {
				// Write the second line in out file
				bw.write(lineTwo);
				bw.newLine();
				// Write line in file
				lineTwo = brTwo.readLine();
				tokenizedLineTwo = lineTwo.split(" ");
			}
			// Same strings
			else {
//				System.out.println("Here!");
				// Write term and combined frequency in the string to be written in file
				mergedLine = tokenizedLineOne[0] + " " + (Integer.parseInt(tokenizedLineOne[1]) + Integer.parseInt(tokenizedLineTwo[1]));
				int i = 2; 
				int j = 2;
				
				while(i < tokenizedLineOne.length && j < tokenizedLineTwo.length) {
					if(Integer.parseInt(tokenizedLineOne[i].split(",")[0]) <= (Integer.parseInt(tokenizedLineTwo[j].split(",")[0]))) {
						mergedLine =  mergedLine + " " + tokenizedLineOne[i];
						i++;
					}
					else {
						mergedLine = mergedLine + " " + tokenizedLineTwo[j];
						j++;
					}
				}
				if(i < tokenizedLineOne.length) {
					for(int k = i ; k < tokenizedLineOne.length ; ++k) {
						mergedLine = mergedLine + " " + tokenizedLineOne[k];
						i++;
					}
					bw.write(mergedLine);
					bw.newLine();
					
					mergedLine = null;
				}
				if(j < tokenizedLineTwo.length) {
					for(int k = j ; k < tokenizedLineTwo.length ; ++k) {
						
						mergedLine = mergedLine + " " + tokenizedLineTwo[k];
						j++;
					}
					bw.write(mergedLine);
					bw.newLine();
					
					mergedLine = null;
				}
				
				lineOne = brOne.readLine();
				lineTwo = brTwo.readLine();
				
				if(lineOne != null)
					tokenizedLineOne = lineOne.split(" ");
				if(lineTwo != null)
					tokenizedLineTwo = lineTwo.split(" ");
			}
			
		} // End of reading files while-loop
		brOne.close();
		brTwo.close();
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
	    if (line == null){
	    	System.out.println("SHOULD BE HERE" + seekingTerm);
	    	
	    	// Return the term the user seeks with 0 in every other field
	    	return seekingTerm+" 0 0,0";
	    }
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
	    
	    String result = file.readLine();
	    if(result == null){
	    	return seekingTerm+" 0 0,0";
	    }
	    else

		    // Return the line containing the term
	    	return result;
	} // End of binary search function
}
