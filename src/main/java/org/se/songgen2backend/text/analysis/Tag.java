package org.se.songgen2backend.text.analysis;

import java.util.*;

import org.se.songgen2backend.text.analysis.dict.Dict;
import org.se.songgen2backend.text.analysis.dict.WordStemmer;
import org.se.songgen2backend.text.analysis.model.TagType;

/**
 * @author Val Richter
 * @reviewer Jakob Kautz
 *
 *           Stores a word with its associated type (e.g. noun) and optionally stores some {@link WordStemmer} object
 *           with it as well.
 *           That data attribute is an optimization to avoid computing it twice. The {@link Dict} class would
 *           otherwise compute the {@link WordStemmer} in two places, despite already having computed it.
 */
public class Tag {
	private TagType type;
	private String word;
	private Optional<WordStemmer> data;

	public Tag(String word, TagType type) {
		this.word = word;
		this.type = type;
		this.data = Optional.empty();
	}

	public Tag(String word, TagType type, WordStemmer data) {
		this.word = word;
		this.type = type;
		this.data = Optional.ofNullable(data);
	}

	public boolean is(TagType type) {
		return this.type == type;
	}

	public TagType getType() {
		return this.type;
	}

	public void setType(TagType type) {
		this.type = type;
	}

	public String getWord() {
		return this.word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public Optional<WordStemmer> getData() {
		return this.data;
	}

	public void setData(Optional<WordStemmer> data) {
		this.data = data;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Tag tag)) {
			return false;
		}
		return Objects.equals(type, tag.type) && Objects.equals(word, tag.word);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, word);
	}

	@Override
	public String toString() {
		return "{" + " type='" + getType() + "'" + ", word='" + getWord() + "'" + "}";
	}

}
