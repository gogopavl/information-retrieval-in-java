import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		
		final ExecutorService service;
		final Future<TreeMap<Integer, DocInfo>> task;
//		final Future<TreeMap<Integer, DocInfo>> task2;
		
		// Creating tree map to keep information about each document
		// The tree map has the entries sorted by the key, which here is the id of the doc 
		TreeMap<Integer, DocInfo> docInfoList = new TreeMap<>();
//		ArrayList<DocInfo> docInfoList = new ArrayList<>();
		
		// Get number of available cores
		System.out.println("Available processors (cores): " + 
		Runtime.getRuntime().availableProcessors());
		
		// Total amount of free memory available to the JVM
		System.out.println("Free memory (bytes): " + 
		Runtime.getRuntime().freeMemory());
		
		// Get list of files from the specified folder
		File folder = new File("C:\\Users\\aintzevi\\git\\IRAssignment\\catalogue");
		File[] listOfFiles = folder.listFiles();
		
		// Storing the filenames to be send to a thread in a string array list
		String [] filesToSend = new String[listOfFiles.length];
		
		// For each file (Fit in main memory)
		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
			if (file.isFile() && file.getName().endsWith(".txt")) {
				// Add the filename to the Sting array
				filesToSend[i] = file.getName();
			} 
		}

		// Creating pool with the number of threads wanted
		service = Executors.newFixedThreadPool(1);
		// Allocating to a thread the files to be processed by it and the tree map with info about the documents
		task = service.submit(new InvertedIndexThread(filesToSend, docInfoList));
//		  task2 = service.submit(new InvertedIndexThread(filesToSend, docInfoList));
//		  task2.get();
		task.get();
		
		for(Map.Entry<Integer, DocInfo> entry : docInfoList.entrySet()) {
			System.out.println(" Key : " + entry.getKey() + " Num of Words : " + entry.getValue().getNumOfWords() + 
					" Most Frequent Word : " + entry.getValue().getMostFreqWord() + 
					" Most Frequent Word Frequency : " + entry.getValue().getMostFreqWordFrequency());
		}
		
		System.out.println("Ended");
		
		// Shutting down the executor service
		service.shutdownNow();
	}

}
