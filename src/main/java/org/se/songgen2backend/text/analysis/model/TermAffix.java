package org.se.songgen2backend.text.analysis.model;

import java.util.*;

/**
 * @author Val Richter
 * 
 *         Parent Class for the {@link Declination} and {@link Conjugation} classes.
 */
public class TermAffix {
	// Attributes must be public for the CSV-Parser to access them
	public String radix;
	public Numerus numerus;
	public AffixType type;
	public Boolean toUmlaut;

	public TermAffix() {
	}

	public TermAffix(String radix, Numerus numerus, AffixType type, boolean toUmlaut) {
		this.radix = radix;
		this.numerus = numerus;
		this.type = type;
		this.toUmlaut = toUmlaut;
	}

	public boolean grammaticallyEquals(TermAffix other) {
		return numerus == other.numerus;
	}

	public String toStringHelper() {
		return " radix='" + getRadix() + "'" + ", numerus='" + getNumerus() + "'" + ", type='" + getType() + "'" + ", toUmlaut='" + getToUmlaut()
				+ "'";
	}

	public String getRadix() {
		return this.radix;
	}

	public void setRadix(String radix) {
		this.radix = radix;
	}

	public Numerus getNumerus() {
		return this.numerus;
	}

	public void setNumerus(Numerus numerus) {
		this.numerus = numerus;
	}

	public AffixType getType() {
		return type;
	}

	public void setType(AffixType type) {
		this.type = type;
	}

	public boolean getToUmlaut() {
		return this.toUmlaut;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof TermAffix termEndings)) {
			return false;
		}
		return Objects.equals(radix, termEndings.radix) && Objects.equals(numerus, termEndings.numerus) && toUmlaut == termEndings.toUmlaut;
	}

	@Override
	public int hashCode() {
		return Objects.hash(radix, numerus, toUmlaut);
	}

	@Override
	public String toString() {
		return "{" + toStringHelper() + "}";
	}

}
