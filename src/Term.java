import java.util.ArrayList;
import java.util.TreeMap;

public class Term {
	private String word;
	private TreeMap<Integer, TermFreqInDoc> docList;
	public Term(String w, TermFreqInDoc i){
		docList = new TreeMap<>();
		this.word = w;
		this.docList.put(i.getDocId(),i);
	}
	public Term(){
		//empty
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public TreeMap<Integer, TermFreqInDoc> getDocList() {
		return docList;
	}
	public void setDocList(TreeMap<Integer, TermFreqInDoc> docList) {
		this.docList = docList;
	}
	
	/**
	 * Function to get the index of a doc in the array list of docs and frequencies, using the docID
	 * @param docID The docID which index we want to get
	 * @return The index of the docId in the array list of docs and frequencies
	 */
	public int getIndexForGivenDocID (int docID){
		// Iterate through the the array list of docs and frequencies
		for(int i = 0 ; i < docList.size() ; ++i){
			// If the docID exists, return its index
			if(docID == docList.get(i).getDocId()){
				return i;
			}
		}
		return -1;
	}
}
