import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

		File folder = new File("C:\\Users\\aintzevi\\git\\IRAssignment\\catalogue");
//		File folder = new File("C:\\Users\\gogopavl\\git\\IRAssignment\\catalogue");
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
		
		for(Map.Entry<Integer, DocInfo> entry : docInfoList.entrySet()) {
			System.out.println(" Key : " + entry.getKey() + " Num of Words : " + entry.getValue().getNumOfWords() + 
					" Most Frequent Word : " + entry.getValue().getMostFreqWord() + 
					" Most Frequent Word Frequency : " + entry.getValue().getMostFreqWordFrequency());
		}
		
		// Shutting down the executor service
		service.shutdownNow();
		
		File outFolder = new File("C:\\Users\\aintzevi\\git\\IRAssignment\\output");

		twoWayMerge(new File( outFolder + "\\out0.txt"), new File( outFolder + "\\out1.txt"), outFolder + "\\merged.txt");
	}
	
	public void externalMergeFunction(File folder){
		// List with output files
		File [] filesToBeSorted = folder.listFiles();
		
		
		
		
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
				System.out.println("Comparison " + tokenizedLineOne[0] + " to " + tokenizedLineTwo[0] + "will write " + lineOne);
				// Write the first line in out file
				bw.write(lineOne);
				bw.newLine();
				// Write line in file
				lineOne = brOne.readLine();
				tokenizedLineOne = lineOne.split(" ");
			}
			// The string from line two is lexicographically smaller
			else if(tokenizedLineOne[0].compareTo(tokenizedLineTwo[0]) > 0) {
				System.out.println("Comparison " + tokenizedLineOne[0] + " to " + tokenizedLineTwo[0] + "will write " + lineTwo);
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
				System.out.println("First: " + mergedLine);
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
				System.out.println("i = " + i + " length + " + tokenizedLineOne.length);
				System.out.println("j = " + j + " length + " + tokenizedLineTwo.length);
				if(i < tokenizedLineOne.length) {
					System.out.println("inside i");
					for(int k = i ; k < tokenizedLineOne.length ; ++k) {
						mergedLine = mergedLine + " " + tokenizedLineOne[k];
						i++;
					}
					bw.write(mergedLine);
					bw.newLine();
					
					mergedLine = null;
				}
				if(j < tokenizedLineTwo.length) {
					System.out.println("inside j");
					for(int k = j ; k < tokenizedLineTwo.length ; ++k) {
						
						mergedLine = mergedLine + " " + tokenizedLineTwo[k];
						j++;
						System.out.println("for j" + j);
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
}
