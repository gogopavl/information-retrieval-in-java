
public class DocInfo {
	
	private int docID;
	private int numOfWords;
	private String mostFreqWord;
	private int mostFreqWordFrequency;
	private double docMagnitude;
	
	public DocInfo () {
		docID = -1;
		numOfWords = 0;
		mostFreqWord = null;
		mostFreqWordFrequency = 0;
		docMagnitude = 0.0;
	}
	
	public DocInfo (int docID, int numOfUniqueWords, String mostFreqWord, int mostFreqWordFrequency) {
		this.docID = docID;
		this.numOfWords = numOfUniqueWords;
		this.mostFreqWord = mostFreqWord;
		this.mostFreqWordFrequency = mostFreqWordFrequency;
	}
	
	public int getDocID() {
		return docID;
	}
	public void setDocID(int docID) {
		this.docID = docID;
	}
	public int getNumOfWords() {
		return numOfWords;
	}
	public void setNumOfWords(int numOfUniqueWords) {
		this.numOfWords = numOfUniqueWords;
	}
	public String getMostFreqWord() {
		return mostFreqWord;
	}
	public void setMostFreqWord(String mostFreqWord) {
		this.mostFreqWord = mostFreqWord;
	}
	public int getMostFreqWordFrequency() {
		return mostFreqWordFrequency;
	}
	public void setMostFreqWordFrequency(int mostFreqWordFrequency) {
		this.mostFreqWordFrequency = mostFreqWordFrequency;
	}
	public double getDocMagnitude() {
		return docMagnitude;
	}
	public void setDocMagnitude(double docMagnitude) {
		this.docMagnitude = docMagnitude;
	}
}
