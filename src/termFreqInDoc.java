
public class termFreqInDoc {
	private int docId;
	private int termFrequency;
	public termFreqInDoc(){
		//empty
	}
	public termFreqInDoc(int id, int freq){
		this.docId = id;
		this.termFrequency = freq;
	}
	public int getDocId() {
		return docId;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public int getTermFrequency() {
		return termFrequency;
	}
	public void setTermFrequency(int termFrequency) {
		this.termFrequency = termFrequency;
	}
	
	

}
