package org.se.songgen2backend.text.analysis;

import java.util.*;

import org.se.songgen2backend.text.analysis.model.Numerus;
import org.se.songgen2backend.text.generation.SongTextGenerator;
import org.se.songgen2backend.text.metric.Hyphenizer;

/**
 * @author Val Richter
 * @reviewer Jakob Kautz
 *
 *           This is the parent class for {@link NounTerm} and {@link VerbTerm}.
 *
 * @implNote
 *           Because it can have a reference to the {@link TermVariations} object, that stores this {@link Term} object,
 *           it needs the generic `T extends Term<T>`. Said reference is especially necessary for the {@link TermComp}
 *           object to sort terms by their frequency.
 */
public class Term<T extends Term<T>> {
	protected int frequency;
	protected final String radix;
	protected final String word;
	protected final Integer syllableAmount;
	protected final Numerus numerus;
	protected TermVariations<T> variations = null;

	public Term(String radix, String word, Numerus numerus) {
		this.frequency = 1;
		this.radix = radix;
		this.word = word;
		this.numerus = numerus;
		this.syllableAmount = Hyphenizer.countSyllables(word);
	}

	public Term(String radix, String word, Numerus numerus, TermVariations<T> variations) {
		this.frequency = 1;
		this.radix = radix;
		this.word = word;
		this.numerus = numerus;
		this.syllableAmount = Hyphenizer.countSyllables(word);
		this.variations = variations;
	}

	/**
	 * This function is called by the {@link SongTextGenerator} and should be overwritten by
	 * children-classes.
	 *
	 * @return A normalized String-version of the Term. Here it's the same as `getWord`, but the {@link NounTerm} and
	 *         {@link VerbTerm} class overwrite this method.
	 */
	public String forLyrics() {
		return this.word;
	}

	public void increaseFrequency() {
		this.frequency++;
	}

	public int hashData() {
		return this.hashCode();
	}

	public String toStringHelper() {
		return " frequency='" + getFrequency() + "'" + ", radix='" + getRadix() + "'" + ", word='" + getWord() + "'" + ", syllableAmount='"
				+ getSyllableAmount() + "'" + ", numerus='" + getNumerus() + "'" + ", variations='[" + variations.getVariations().size() + "]'";
	}

	@Override
	public String toString() {
		return "{" + toStringHelper() + "}";
	}

	// Boilerplate:
	public int getFrequency() {
		return this.frequency;
	}

	public String getRadix() {
		return this.radix;
	}

	public String getWord() {
		return this.word;
	}

	public Integer getSyllableAmount() {
		return this.syllableAmount;
	}

	public Numerus getNumerus() {
		return this.numerus;
	}

	public TermVariations<T> getVariations() {
		return this.variations;
	}

	public void setVariations(TermVariations<T> variations) {
		this.variations = variations;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Term<?> term)) {
			return false;
		}
		return frequency == term.frequency && Objects.equals(radix, term.radix) && Objects.equals(word, term.word)
				&& Objects.equals(syllableAmount, term.syllableAmount) && Objects.equals(numerus, term.numerus);
	}

	@Override
	public int hashCode() {
		return Objects.hash(frequency, radix, word, syllableAmount, numerus);
	}

}
