import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.print.Doc;

import org.apache.commons.io.FileUtils;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		
		final ExecutorService service;
		final Future<TreeMap<Integer, DocInfo>> task;
		final Future<TreeMap<Integer, DocInfo>> task2;
		

		TreeMap<Integer, DocInfo> docInfoList = new TreeMap<>();
//		ArrayList<DocInfo> docInfoList = new ArrayList<>();
		
		System.out.println("Available processors (cores): " + 
		Runtime.getRuntime().availableProcessors());
		
		  /* Total amount of free memory available to the JVM */
		System.out.println("Free memory (bytes): " + 
		Runtime.getRuntime().freeMemory());
		
		File folder = new File("C:\\Users\\aintzevi\\git\\IRAssignment\\catalogue");
		File[] listOfFiles = folder.listFiles();
		String [] filesToSend = new String[listOfFiles.length];
		//osa xwrane stin mnimi
		for (int i = 0; i < listOfFiles.length; i++) {
			File file = listOfFiles[i];
		    if (file.isFile() && file.getName().endsWith(".txt")) {
		  /* do somthing with content */
		//		    	System.out.println(file.getName());
		    	filesToSend[i] = file.getName();
		    } 
		  }
		  //invertedIndex i = new invertedIndex(filesToSend);
		  service = Executors.newFixedThreadPool(1);
		  task = service.submit(new InvertedIndexThread(filesToSend, docInfoList));
//		  task2 = service.submit(new InvertedIndexThread(filesToSend, docInfoList));
//		  task2.get();
		  task.get();
		  
		  for(Map.Entry<Integer, DocInfo> entry : docInfoList.entrySet()) {
				System.out.println(" Key : " + entry.getKey() + " Num of Words : " + entry.getValue().getNumOfWords() + 
						" Most Frequent Word : " + entry.getValue().getMostFreqWord() + 
						" Most Frequent Word Frequency : " + entry.getValue().getMostFreqWordFrequency());
		  }
		  
//		  ArrayList<DocInfo> temp = task.get();
//		  System.out.println("Temp size: " + temp.size());
		  
//		  docInfoList.addAll(temp);
		  
		  System.out.println("Ended");
		  service.shutdownNow();
	}

}
