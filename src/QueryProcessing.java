
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

public class QueryProcessing implements Callable<String[]>{

	private TreeMap<Integer, DocInfo> docInfoList;
	private RandomAccessFile invertedIndex;
	private String [] queriesToProcess;

	public QueryProcessing(){
		//empty ctor
	}
	public QueryProcessing(TreeMap<Integer,DocInfo> docInfoList, String invertedIndexPath, String[] queriesToProcess) throws FileNotFoundException{
		this.docInfoList =  docInfoList;
		this.invertedIndex = new RandomAccessFile(invertedIndexPath, "r");
		this.queriesToProcess = queriesToProcess;
	}
	@Override
	public String[] call() throws Exception {
		// TODO Auto-generated method stub
		int queryId;
		int topKToReturn;
		String [] returned = new String [queriesToProcess.length];
		HashMap<String, Double> queryDocWeights = new HashMap<>();
		String [] currentQuerySplitted;


		String lineFetched;
		String [] lineFetchedSplitted;
		String [] tempDocPair;
		Double queryTF;
		Double queryIDF;
		Double docTF;
		Double docIDF;
		
		int counter = 0;
		for(String s : queriesToProcess){
			currentQuerySplitted = s.split(" ");
			HashMap<String, Integer> tempMostFrequentWordFrequency = new HashMap<>();
			int mostFrequentFrequencyWithinQuery=0;
			//Find most frequent word frequency within the query
			for(int index = 2 ; index < currentQuerySplitted.length ; ++index ){
				if(!tempMostFrequentWordFrequency.containsKey(currentQuerySplitted[index])){
					tempMostFrequentWordFrequency.put(currentQuerySplitted[index], 1);
				}
				else {
					System.out.println("term: "+currentQuerySplitted[index]);
					tempMostFrequentWordFrequency.put(currentQuerySplitted[index],tempMostFrequentWordFrequency.get(currentQuerySplitted[index]).intValue()+1);
				}
				
			}
			mostFrequentFrequencyWithinQuery = Collections.max(tempMostFrequentWordFrequency.values());
			
			queryId = Integer.parseInt(currentQuerySplitted[0]);
			topKToReturn = Integer.parseInt(currentQuerySplitted[1]);
			

			for(int i = 2 ; i < currentQuerySplitted.length ; ++i){
				// Binary search in the inverted index for the term
				lineFetched = Main.binarySearch(invertedIndex, currentQuerySplitted[i]);
				// Split the fetched line on space
				lineFetchedSplitted = lineFetched.split(" ");
				// For each docID, frequency pair
				
				for(int j = 2 ; j < lineFetchedSplitted.length ; ++j ){
					// Split pair on comma
					tempDocPair = lineFetchedSplitted[j].split(",");
					
					queryTF = Double.parseDouble(Integer.toString(tempMostFrequentWordFrequency.get(currentQuerySplitted[i]))) / mostFrequentFrequencyWithinQuery;
					queryIDF = (Math.log(docInfoList.size() / new Double(tempDocPair[1])));
					
					
					docTF = (Double.parseDouble(tempDocPair[1]))/(double)(docInfoList.get(Integer.parseInt(tempDocPair[0])).getMostFreqWordFrequency());
					docIDF = Math.log(new Double(docInfoList.size()) / new Double(tempDocPair[1]));
					// If the docId of the pair is the same with the docID read from the file with docIDs and unique terms
					if(!queryDocWeights.containsKey(tempDocPair[0])){
						queryDocWeights.put(tempDocPair[0], (queryTF*queryIDF*docTF*docIDF));
					}
					else {
						queryDocWeights.put(tempDocPair[0],queryDocWeights.get(tempDocPair[0]).doubleValue() + (queryTF*queryIDF*docTF*docIDF));
					}
				}
			}
			for(Map.Entry<String, Double> entry : queryDocWeights.entrySet()){
				queryDocWeights.put(entry.getKey(),queryDocWeights.get(entry.getKey())/docInfoList.get(Integer.parseInt(entry.getKey())).getDocMagnitude());
			}
			
			Map<String, Double> temp = sortByComparator(queryDocWeights);
			
			
			int i = 0 ;
			returned[counter] = String.valueOf(queryId);
			for(Map.Entry<String, Double> entry : temp.entrySet()) {
				if(i >= topKToReturn){
					break;
				}
				returned[counter] = returned[counter] + " "+entry.getKey() +":" +entry.getValue() ;
				i++;
			}
			counter++;
		}
		
		return returned;
	}
	private static Map<String, Double> sortByComparator(Map<String, Double> unsortMap) {

		// Convert Map to List
		List<Map.Entry<String, Double>> list = 
			new LinkedList<Map.Entry<String, Double>>(unsortMap.entrySet());
		
		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			
			@Override
			public int compare(Entry<String, Double> o1, Entry<String, Double> o2) {
				if(o1.getValue().compareTo(o2.getValue()) < 0 ){
					return +2;
				}
				else return -2;
			}
		});

		// Convert sorted map back to a Map
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Iterator<Map.Entry<String, Double>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Double> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}


}

