import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
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
		
		for(Map.Entry<Integer, DocInfo> entry : docInfoList.entrySet()) {
			System.out.println(" Key : " + entry.getKey() + " Num of Words : " + entry.getValue().getNumOfWords() + 
					" Most Frequent Word : " + entry.getValue().getMostFreqWord() + 
					" Most Frequent Word Frequency : " + entry.getValue().getMostFreqWordFrequency());
		}
		
		// Shutting down the executor service
		service.shutdownNow();
	}

}
