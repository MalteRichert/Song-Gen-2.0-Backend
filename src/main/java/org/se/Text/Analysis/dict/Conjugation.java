package org.se.Text.Analysis.dict;

import java.util.*;
import org.se.Text.Analysis.*;

/**
 * @author Val Richter
 */
public class Conjugation extends TermEndings {
	public Person person;
	public Tense tense;

	public Conjugation() {
	}

	public Conjugation(String radix, Numerus numerus, Person person, Tense tense, boolean toUmlaut) {
		super(radix, numerus, toUmlaut);
		this.person = person;
		this.tense = tense;
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

	public Conjugation person(Person person) {
		setPerson(person);
		return this;
	}

	public Conjugation tense(Tense tense) {
		setTense(tense);
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;
		if (!(o instanceof Conjugation)) {
			return false;
		}
		Conjugation conjugation = (Conjugation) o;
		return Objects.equals(person, conjugation.person) && Objects.equals(tense, conjugation.tense);
	}

	@Override
	public int hashCode() {
		return Objects.hash(person, tense);
	}

	@Override
	public String toString() {
		return "{" +
				super.toStringHelper() +
				" person='" + getPerson() + "'" +
				", tense='" + getTense() + "'" +
				"}";
	}
}
