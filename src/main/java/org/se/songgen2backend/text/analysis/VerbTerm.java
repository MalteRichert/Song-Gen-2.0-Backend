package org.se.songgen2backend.text.analysis;

import java.util.*;

import org.se.songgen2backend.text.analysis.model.Numerus;

/**
 * @author Val Richter
 * @reviewer Jakob Kautz
 */
public class VerbTerm extends Term<VerbTerm> {
	String infinitive;

	public VerbTerm(String radix, String word, Numerus numerus, String infinitive) {
		super(radix, word, numerus);
		this.infinitive = infinitive;
	}

	@Override
	public String forLyrics() {
		return this.infinitive.toLowerCase();
	}

	@Override
	public String toString() {
		return "{" + super.toStringHelper() + " infinitive='" + getInfinitive() + "'" + "}";
	}

	@Override
	public int hashData() {
		return this.hashCode();
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.radix, super.frequency, super.syllableAmount, super.word, infinitive);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof VerbTerm verbTerm)) {
			return false;
		}
		return super.equals(verbTerm) && Objects.equals(infinitive, verbTerm.infinitive);
	}

	// Boilerplate:

	public String getInfinitive() {
		return this.infinitive;
	}

	public void setInfinitive(String infinitive) {
		this.infinitive = infinitive;
	}
}
