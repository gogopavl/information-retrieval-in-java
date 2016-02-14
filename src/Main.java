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

import org.apache.commons.io.FileUtils;
import org.omg.CORBA.Current;

import org.omg.CORBA.Current;

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

		File folder = new File("C:\\Users\\aintzevi\\git\\IRAssignment\\catalogue");
//		File folder = new File("C:\\Users\\gogopavl\\git\\IRAssignment\\catalogue");
		File[] listOfFiles = folder.listFiles();
		
		int numOfFilesPerThread = listOfFiles.length/numberOfThreads;

		// Getting number of files per thread
		if(listOfFiles.length%numberOfThreads != 0){
			// Round up the number of files so that no file is left out of a thread
			numOfFilesPerThread += (numberOfThreads - listOfFiles.length%numberOfThreads);
		}
		
		// Storing the filenames to be send to a thread in a string array list
		String [] filesToSend = new String[numOfFilesPerThread];
		
		// For each file (Fit in main memory)
		// Counter to keep track of the number of files per thread
		int count = 0;
		// Counter to keep track of the file naming
		int outputFileNameCounter = 0;
		
		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile() && file.getName().endsWith(".txt") && count < numOfFilesPerThread) {
				// Add the filename to the Sting array
				filesToSend[count] = file.getName();
				count++;
			}
			// When the number of files per thread is reached
			if(count >= numOfFilesPerThread){
				// Submit task with the files gathered
				taskList.add(service.submit(new InvertedIndexThread(filesToSend, docInfoList, "out"+outputFileNameCounter+".txt")));
				filesToSend = new String[numOfFilesPerThread];
				
				// Resetting counter to zero
				count = 0;
				// Incrementing number in out file name
				outputFileNameCounter++;
			}
		}
		// For files left, create another task
		if(filesToSend[0] != null)
			taskList.add(service.submit(new InvertedIndexThread(filesToSend, docInfoList, "out"+outputFileNameCounter+".txt")));
		
		// Allocating to a thread the files to be processed by it and the tree map with info about the documents
		for(Future<TreeMap<Integer, DocInfo>> t : taskList){
			t.get();
		}
		
		System.out.println("****************" + taskList.size());
		
		for(Map.Entry<Integer, DocInfo> entry : docInfoList.entrySet()) {
			System.out.println(" Key : " + entry.getKey() + " Num of Words : " + entry.getValue().getNumOfWords() + 
					" Most Frequent Word : " + entry.getValue().getMostFreqWord() + 
					" Most Frequent Word Frequency : " + entry.getValue().getMostFreqWordFrequency() +
					" Document magnitude: " + entry.getValue().getDocMagnitude());
		}
		// Compute magnitude of docs
//		docMagnitudeCalculator(docInfoList);
		
		// Shutting down the executor service
		service.shutdownNow();
		
		
		File outFolder = new File("C:\\Users\\aintzevi\\git\\IRAssignment\\output");
//		File outFolder = new File("C:\\Users\\gogopavl\\git\\IRAssignment\\output");
		
		externalMergeFunction(outFolder, 1);
//		twoWayMerge(new File( outFolder + "\\out0.txt"), new File( outFolder + "\\out1.txt"), outFolder + "\\merged.txt");
	}
	
	/**
	 * Function that calculates the magnitude of a vector in tree map form
	 * @param docList DocInfo objects in a tree map form
	 * @return the same tree map, in which object field doc magnitude will be changed
	 * @throws IOException
	 */
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
		
		// Read file with docID and unique terms
		while((line = br.readLine()) != null){
			// Split read line on space
			termList = line.split(" ");
			// For each unique term that exists in the doc
			for(int i = 1 ; i < termList.length ; ++i){
				// Binary search in the inverted index for the term
				lineFetched = binarySearch(new RandomAccessFile("output\\merged.txt","r"), termList[i]);
				// Split the fetched line on space
				temp = lineFetched.split(" ");
				// For each docID, frequency pair
				for(int j = 2 ; j < temp.length ; ++j ){
					// Split pair on comma
					tempDocPair = temp[j].split(",");
					// If the docId of the pair is the same with the docID read from the file with docIDs and unique terms
					if(Integer.parseInt(tempDocPair[0]) == Integer.parseInt(termList[0])){
						// Keep the frequency of the term in this doc
						currentTermFrequency = Double.parseDouble(tempDocPair[1]);		
						break;
					}
				}
				System.out.println("DocID: " + termList[0]);
				System.out.println("CurrentTermFrequency / MaxFreqInDoc: " + currentTermFrequency + "/" + (docList.get(Integer.parseInt(termList[0])).getMostFreqWordFrequency()) );
				
				System.out.println("DocID: " + termList[0]);
				
				System.out.println("CurrentTermFrequency / MaxFreqInDoc: " + currentTermFrequency + "/" + (docList.get(Integer.parseInt(termList[0])).getMostFreqWordFrequency()) );
				// Tf computation - frequency of the term in this doc to frequency of the most frequent word in the doc
				tf = currentTermFrequency / (docList.get(Integer.parseInt(termList[0])).getMostFreqWordFrequency());
				
				System.out.println("tf: " + tf);
				
				System.out.println("N / Nt: " + docList.size() + "/" + Double.parseDouble(temp[1]));
				// Idf computation - number of all docs to number of docs in which thte term exists
				idf = (Math.log(docList.size() / Double.parseDouble(temp[1]))) / Math.log(2);
				
				System.out.println("idf: " + idf);	
				// for each unique term of doc, get the sum of square weights
				docMagnitude += Math.pow((tf * idf), 2.0) ; 
				System.out.println("mag: " + docMagnitude);
				System.out.println("tf: " + tf);
				
				currentTermFrequency = 0.0;
			}
			System.out.println("break");
			
			// Set magnitude value inside the respective DocInfo object
			docList.get(Integer.parseInt(termList[0])).setDocMagnitude(Math.sqrt(docMagnitude));
			docMagnitude = 0.0;
		}
		return docList;
		
	}
	/**
	 * Function to merge all mini inverted index files
	 * @param folder The folder that contains the files to be merged
	 * @throws IOException 
	 */
	public static void externalMergeFunction(File folder, int mergePhase) throws IOException{
		// List with output files
		File [] filesToBeSorted = folder.listFiles();
		
  		String outName;
		
  		System.out.println(mergePhase);
  		if( filesToBeSorted.length == 2) {
  			twoWayMerge(filesToBeSorted[0], filesToBeSorted[1], "invertedIndex.txt");
  			return;
  		}
  		
		// If the number of files is even
		if(filesToBeSorted.length%2 == 0) {
			for( int i = 0 ; i < filesToBeSorted.length ; i += 2) {
				outName = "mg" + mergePhase + "out" + i + ".txt";

				// Merge the two files, add file to output folder for merged files
				twoWayMerge(filesToBeSorted[i], filesToBeSorted[i + 1], folder + "\\" + outName);
			}
		}
		// If the number of files is odd
		else {
			for( int i = 0 ; i < filesToBeSorted.length - 1 ; i += 2) {
				outName = "mg" + mergePhase + "out" + i + ".txt";

				// Merge the two files, add file to output folder for merged files
				twoWayMerge(filesToBeSorted[i], filesToBeSorted[i + 1], folder + "\\" + outName);
			}
			
			outName = filesToBeSorted[filesToBeSorted.length-1].toString();
			
			String newFileName = "mg" + mergePhase + "out" + (filesToBeSorted.length-1) + ".txt";
			
			
			File oldfile =new File(outName);
			File newfile =new File(newFileName);
			
			if(oldfile.renameTo(newfile))
				System.out.println("Rename succesful");
			else
				System.out.println("Rename failed");
		}
		// Delete output folder
		
		externalMergeFunction(folder, mergePhase + 1);
		
	}
	
	/**
	 * Function that implements two way merge between two files
	 * @param one The first file 
	 * @param two The second file
	 * @param outname The name of the merged file
	 * @throws IOException
	 */
	public static void twoWayMerge(File one, File two, String outname) throws IOException {
		// Buffered readers for the two files to be merged
		BufferedReader brOne = new BufferedReader(new FileReader(one));
		BufferedReader brTwo = new BufferedReader(new FileReader(two));
		
		// Buffer writer for the output merged file
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outname)));
		
		// Read first line of both files
		String lineOne = brOne.readLine();
		String lineTwo = brTwo.readLine();
		
		
		// Split lines on space
		String[] tokenizedLineOne = lineOne.split(" ");
		String[] tokenizedLineTwo = lineTwo.split(" ");
		
		String mergedLine = null;
		
		// Read through both files (Both still have lines)
		while ((lineOne != null) && (lineTwo != null )) {

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
				// Write term and combined frequency in the string to be written in file
				mergedLine = tokenizedLineOne[0] + " " + (Integer.parseInt(tokenizedLineOne[1]) + Integer.parseInt(tokenizedLineTwo[1]));
				
				int i = 2; 
				int j = 2;
				
				// Going through the docID, frequency part
				while(i < tokenizedLineOne.length && j < tokenizedLineTwo.length) {
					// If the docID of the first file is smaller than the other
					if(Integer.parseInt(tokenizedLineOne[i].split(",")[0]) <= (Integer.parseInt(tokenizedLineTwo[j].split(",")[0]))) {
						// Add it to the merged line
						mergedLine =  mergedLine + " " + tokenizedLineOne[i];
						// Get next docID, frquency of the first file
						i++;
					}
					// If the docID of the first file is bigger than or equal with the other
					else {
						// Add it to the merged line
						mergedLine = mergedLine + " " + tokenizedLineTwo[j];
						// Get next docID, frquency of the second file
						j++;
					}
				} // End of while - loop
				
				// If there are any pairs left in the first line
				if(i < tokenizedLineOne.length) {
					// Go through every pair
					for(int k = i ; k < tokenizedLineOne.length ; ++k) {
						// Add it to the merged line as it is
						mergedLine = mergedLine + " " + tokenizedLineOne[k];
						i++;
					}
					// Write merged line in file
					bw.write(mergedLine);
					bw.newLine();
					
					// Clear merged line
					mergedLine = null;
				}
				// If there are any pairs left in the second line
				if(j < tokenizedLineTwo.length) {
					// Go through every pair
					for(int k = j ; k < tokenizedLineTwo.length ; ++k) {
						// Add it to the merged line as it is
						mergedLine = mergedLine + " " + tokenizedLineTwo[k];
						j++;
					}
					// Write merged line in file
					bw.write(mergedLine);
					bw.newLine();
					
					// Clear merged line
					mergedLine = null;
				}
				
				// Read next line of both files
				lineOne = brOne.readLine();
				lineTwo = brTwo.readLine();
				
				// If the file hasn't finished, split line on space
				if(lineOne != null)
					tokenizedLineOne = lineOne.split(" ");
				if(lineTwo != null)
					tokenizedLineTwo = lineTwo.split(" ");
			}
						
		} // End of reading files while-loop
		
		// If the file hasn't finished, split line on space
		if(lineOne != null)
			bw.write(lineOne);
		if(lineTwo != null)
			bw.write(lineTwo);
		
		// Close readers and writer
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

	    	System.out.println("SHOULD BE HERE" + seekingTerm);
	    	return seekingTerm+" 0 0,0";
	    }
	    else

		    // Return the line containing the term
	    	return result;
	} // End of binary search function
}
