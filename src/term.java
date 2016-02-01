import java.util.ArrayList;

public class term {
	private String word;
	private ArrayList<termFreqInDoc> docList;
	public term(String w, termFreqInDoc i){
		docList = new ArrayList<>();
		this.word = w;
		this.docList.add(i);
	}
	public term(){
		//empty
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public ArrayList<termFreqInDoc> getDocList() {
		return docList;
	}
	public void setDocList(ArrayList<termFreqInDoc> docList) {
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
