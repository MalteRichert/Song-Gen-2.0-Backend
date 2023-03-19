package org.se.songgen2backend.text.analysis;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.se.songgen2backend.text.analysis.dict.Dict;
import org.se.songgen2backend.text.analysis.model.*;

/**
 * @author Val Richter
 * @reviewer Jakob Kautz
 * <p>
 *           Stores all different variations of a {@link Term} that appeared in the text. A {@link Term} might appear in
 *           two different grammatical cases in the text for example and while their strings are different, they still
 *           represent the same term. Such different terms are all stored together in this class. Among other things,
 *           that allows counting the frequency of a term with all its variations instead of simply counting the
 *           frequencies of strings.
 *
 * @implNote The different variations are implemented using a {@link HashMap}. Their grammatical forms (i.e
 *           {@link GrammaticalCase}, {@link Gender} and {@link Numerus}) are transformed efficiently into integers and
 *           used as keys. The actual {@link Term} objects, which are of course different for different variations, are
 *           used as values. The rationale behind this is that the main interaction with Terms occurs via the querying
 *           methods of the {@link TermCollection}, which means that we frequently check if certain grammatical forms of
 *           Term exist.
 */
public class TermVariations<T extends Term<T>> {
	private final Map<Integer, T> variations;
	private Integer frequency;
	private String radix;
	private final Random rand = new Random();

	public TermVariations(T term) {
		this.variations = new HashMap<>();
		this.variations.put(term.hashData(), term);
		this.frequency = 1;
		this.radix = term.radix;
	}

	public void forEach(Consumer<? super T> f) {
		variations.forEach((key, term) -> f.accept(term));
	}

	public void add(T term) {
		if (this.radix.isEmpty()) this.radix = term.radix;

		if (this.has(term)) {
			this.variations.get(term.hashData()).increaseFrequency();
		} else {
			this.variations.put(term.hashData(), term);
		}

		increaseFrequency();
	}

	public boolean has(T term) {
		return this.variations.containsKey(term.hashData());
	}

	public List<T> queryBy(Predicate<? super T> f) {
		return this.variations.values().stream().filter(f).toList();
	}

	public void add(TermVariations<T> variations) {
		variations.variations.forEach((key, term) -> {
			if (!this.variations.containsKey(key)) {
				this.variations.put(key, term);
			} else {
				this.variations.get(key).increaseFrequency();
			}
			this.increaseFrequency();
		});
	}

	public void increaseFrequency() {
		this.frequency++;
	}

	public Term<T> getRandomTerm() {
		int i = rand.nextInt(variations.size());
		Object[] arr = variations.values().toArray();
		return (Term<T>) arr[i];
	}

	// Static Functions specifically for Nouns

	public static Optional<NounTerm> getTerm(TermVariations<NounTerm> nounVariations, Gender gender, GrammaticalCase grammaticalCase,
			Numerus numerus) {
		int hash = NounTerm.hashData(gender, grammaticalCase, numerus);
		if (nounVariations.variations.containsKey(hash)) {
			return Optional.of(nounVariations.variations.get(hash));
		}
		return Optional.empty();
	}

	/**
	 * Same as getTerm, except it allows the program to automatically create the queried variation if necessary.
	 *
	 * @implNote The reason this is a static method is that it has to be specific to NounTerms, while the class in general
	 *           is useful for both NounTerms and VerbTerms.
	 *
	 * @param nounVariations
	 *            The {@link TermVariations} object, that holds the generally desired term that.
	 * @param gender
	 *            The desired {@link Gender} for the {@link Term}. If the {@link Term} cannot be coerced into said
	 *            {@link Gender}, then an empty {@link Optional} will be returned.
	 * @param grammaticalCase
	 *            The desired {@link GrammaticalCase} for the {@link Term}.
	 * @param numerus
	 *            The desired {@link Numerus} for the {@link Term}.
	 * @param dict
	 *            The {@link Dict} object, that should be used to create the new variation if necessary.
	 * @return The speciic variation of the term, if possible. If the term can't be coereced into any of the given
	 *         constraints (usually this will be because of the given {@link Gender}), then an empty {@link Optional} is
	 *         returned.
	 */
	public static Optional<NounTerm> createTerm(TermVariations<NounTerm> nounVariations, Gender gender, GrammaticalCase grammaticalCase,
			Numerus numerus, Dict dict) {
		Optional<NounTerm> res = getTerm(nounVariations, gender, grammaticalCase, numerus);
		if (res.isPresent()) return res;

		return dict.createNounTerm(nounVariations, gender, grammaticalCase, numerus);
	}

	// Boilerplate:

	public String getRadix() {
		return this.radix;
	}

	public Map<Integer, T> getVariations() {
		return this.variations;
	}

	public Integer getFrequency() {
		return this.frequency;
	}

	@Override
	public int hashCode() {
		return this.radix.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof TermVariations)) {
			return false;
		}
		TermVariations<T> termVariations = (TermVariations<T>) o;
		return Objects.equals(variations, termVariations.variations) && Objects.equals(frequency, termVariations.frequency)
				&& Objects.equals(radix, termVariations.radix);
	}

	@Override
	public String toString() {
		return "{" + " variations='" + getVariations() + "'" + ", frequency='" + getFrequency() + "'" + ", radix='" + getRadix() + "'" + "}";
	}
}
