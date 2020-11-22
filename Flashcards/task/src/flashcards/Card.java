package flashcards;

import java.util.Objects;

public class Card {
	private String word;
	private String definition;

	Card(String word, String definition){
		this.word = word;
		this.definition = definition;
	}

	public boolean check(String guess){
		if (guess == null) return false;
		return guess.equalsIgnoreCase(definition);
	}

	public String getWord(){
		return this.word;
	}

	public String getDefinition(){
		return this.definition;
	}

}
