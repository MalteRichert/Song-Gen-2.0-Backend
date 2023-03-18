package org.se.songgen2backend.text.analysis.model;

import java.util.*;

/**
 * @author Val Richter
 */
public class Conjugation extends TermAffix {
	// Attributes must be public for the CSV-Parser to access them
	public Person person;
	public Tense tense;

	public Conjugation() {
	}

	public Conjugation(String radix, Numerus numerus, Person person, Tense tense, AffixType type, boolean toUmlaut) {
		super(radix, numerus, type, toUmlaut);
		this.person = person;
		this.tense = tense;
	}

	@Override
	public boolean grammaticallyEquals(TermAffix other) {
		if (!(other instanceof Conjugation conjugation)) return false;
		return super.grammaticallyEquals(conjugation) && person == conjugation.person && tense == conjugation.tense;
	}

	public Person getPerson() {
		return this.person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public Tense getTense() {
		return this.tense;
	}

	public void setTense(Tense tense) {
		this.tense = tense;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Conjugation conjugation)) {
			return false;
		}
		return Objects.equals(person, conjugation.person) && Objects.equals(tense, conjugation.tense);
	}

	@Override
	public int hashCode() {
		return Objects.hash(person, tense);
	}

	@Override
	public String toString() {
		return "{" + super.toStringHelper() + " person='" + getPerson() + "'" + ", tense='" + getTense() + "'" + "}";
	}
}
