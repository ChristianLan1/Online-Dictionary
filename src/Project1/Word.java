package Project1;

public class Word {
	private String word;
	private int id;
	private String meaning;
	public Word(int id, String word, String meaning) {
		super();
		this.word = word;
		this.id = id;
		this.meaning = meaning;
	}
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMeaning() {
		return meaning;
	}
	public void setMeaning(String meaning) {
		this.meaning = meaning;
	}
	

}
