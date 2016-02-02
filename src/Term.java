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
	public int getIndexForGivenDocID (int docID){
		for(int i = 0 ; i < docList.size() ; ++i){
			if(docID == docList.get(i).getDocId()){
				return i;
			}
		}
		return -1;
	}
	

}
