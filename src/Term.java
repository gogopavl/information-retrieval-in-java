import java.util.ArrayList;

public class Term {
	private String word;
	private ArrayList<TermFreqInDoc> docList;
	public Term(String w, TermFreqInDoc i){
		docList = new ArrayList<>();
		this.word = w;
		this.docList.add(i);
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
	public ArrayList<TermFreqInDoc> getDocList() {
		return docList;
	}
	public void setDocList(ArrayList<TermFreqInDoc> docList) {
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
