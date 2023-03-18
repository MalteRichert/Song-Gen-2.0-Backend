package org.se.songgen2backend.text.analysis;

import java.util.*;

import org.se.songgen2backend.text.analysis.model.Gender;
import org.se.songgen2backend.text.analysis.model.GrammaticalCase;
import org.se.songgen2backend.text.analysis.model.Numerus;
import org.se.songgen2backend.text.analysis.model.*;

/**
 * @author Val Richter
 * @reviewer Jakob Kautz
 */
public class NounTerm extends Term<NounTerm> {
	private final GrammaticalCase grammaticalCase;
	private final Gender gender;
	private final boolean changeableGender;

	public NounTerm(String radix, String word, Numerus numerus, GrammaticalCase grammaticalCase, Gender gender, boolean changeableGender,
                    TermVariations<NounTerm> variations) {
		super(radix, word, numerus, variations);
		this.grammaticalCase = grammaticalCase;
		this.gender = gender;
		this.changeableGender = changeableGender;
	}

	public NounTerm(String radix, String word, Numerus numerus, GrammaticalCase grammaticalCase, Gender gender) {
		super(radix, word, numerus);
		this.grammaticalCase = grammaticalCase;
		this.gender = gender;
		this.changeableGender = false;
	}

	@Override
	public String forLyrics() {
		return Util.capitalize(this.getWord());
	}

	@Override
	public int hashData() {
		return NounTerm.hashData(gender, grammaticalCase, numerus);
	}

	public static int hashData(Gender gender, GrammaticalCase grammaticalCase, Numerus numerus) {
		int genderNum = gender.ordinal();
		int caseNum = grammaticalCase.ordinal();
		int pluralNum = numerus.ordinal();
		return genderNum * 100 + caseNum * 10 + pluralNum;
	}

	@Override
	public String toString() {
		return "{" + super.toStringHelper() + ", grammaticalCase='" + getGrammaticalCase() + "'" + ", gender='" + getGender() + "'"
				+ ", changeableGender='" + getChangeableGender() + "'" + "}";
	}

	// Boilerplate:

	@Override
	public String getWord() {
		switch (word.length()) {
			case 0:
				return word;

			case 1:
				return word.toUpperCase();

			default:
				return word.substring(0, 1).toUpperCase() + word.substring(1);
		}
	}

	public GrammaticalCase getGrammaticalCase() {
		return this.grammaticalCase;
	}

	public Gender getGender() {
		return this.gender;
	}

	public boolean getChangeableGender() {
		return this.changeableGender;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof NounTerm nounTerm)) {
			return false;
		}
		return super.equals(nounTerm) && Objects.equals(grammaticalCase, nounTerm.grammaticalCase) && Objects.equals(gender, nounTerm.gender);
	}

	@Override
	public int hashCode() {
		return Objects.hash(frequency, radix, word, syllableAmount, numerus, grammaticalCase, gender);
	}
}
