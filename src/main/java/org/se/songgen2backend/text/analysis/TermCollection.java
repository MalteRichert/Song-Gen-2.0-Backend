package org.se.songgen2backend.text.analysis;

import java.util.*;
import java.util.function.*;

import org.se.songgen2backend.text.analysis.dict.Dict;
import org.se.songgen2backend.text.analysis.model.Gender;
import org.se.songgen2backend.text.analysis.model.GrammaticalCase;
import org.se.songgen2backend.text.analysis.model.Numerus;
import org.se.songgen2backend.text.analysis.model.*;

/**
 * @author Val Richter
 * @reviewer Jakob Kautz
 *
 *           A collection of all Terms offering an API to interact with and especially query these Terms according to
 *           some constraints (like the term's numerus for example).
 */
public class TermCollection {
	private List<TermVariations<NounTerm>> nouns;
	private List<TermVariations<VerbTerm>> verbs;
	private final Dict dict;
	static final Random rand = new Random();

	public TermCollection(Dict dict, Map<String, TermVariations<NounTerm>> nouns, Map<String, TermVariations<VerbTerm>> verbs) {
		this.nouns = new ArrayList<>();
		this.verbs = new ArrayList<>();
		for (TermVariations<NounTerm> noun : nouns.values()) {
			this.nouns.add(noun);
		}
		for (TermVariations<VerbTerm> verb : verbs.values()) {
			this.verbs.add(verb);
		}
		this.dict = dict;
	}

	public int size() {
		return nouns.size() + verbs.size();
	}

	// Iterators

	public void iter(Consumer<? super TermVariations<? extends Term<?>>> f) {
		iterNouns(f);
		iterVerbs(f);
	}

	public void iterNouns(Consumer<? super TermVariations<NounTerm>> f) {
		nouns.forEach(f);
	}

	public void iterVerbs(Consumer<? super TermVariations<VerbTerm>> f) {
		verbs.forEach(f);
	}

	public void flatIter(Consumer<? super Term<?>> f) {
		flatIterNouns(f);
		flatIterVerbs(f);
	}

	public void flatIterNouns(Consumer<? super NounTerm> f) {
		nouns.forEach(variations -> variations.forEach(f));
	}

	public void flatIterVerbs(Consumer<? super VerbTerm> f) {
		verbs.forEach(variations -> variations.forEach(f));
	}

	// Querying Methods

	/**
	 * Query all Nouns by constraning all attributes of a {@link NounTerm}. The resulting list is sorted according to the
	 * terms' frequencies.
	 *
	 * @param grammaticalCase
	 *            The {@link GrammaticalCase} that the Term must have. If the term wasn't already found with the correct
	 *            {@link GrammaticalCase} in the text, it will be declinated into the desired {@link GrammaticalCase}. This
	 *            happens along with the coercion into the desired {@link Numerus} as they are checked and declinated
	 *            together.
	 * @param gender
	 *            The {@link Gender} that the Term must have. If the term wasn't already found with the correct
	 *            {@link Gender} in the text, it is checked, whether the term can be coerced into the desired
	 *            {@link Gender}. If not, the term is excluded.
	 * @param numerus
	 *            The {@link Numerus} that the Term must have. If the term wasn't already found with the correct
	 *            {@link Numerus} in the text, it will be coerced into the desired {@link Numerus}. This happens along with
	 *            the coercive declination for the {@link GrammaticalCase} as they are checked and declinated together.
	 * @param syllableMin
	 *            The minimum amount of syllables that the term should have after being coerced into the correct
	 *            {@link GrammaticalCase}, {@link Gender} and {@link Numerus}.
	 * @param syllableMax
	 *            The maximum amount of syllables that the term should have after being coerced into the correct
	 *            {@link GrammaticalCase}, {@link Gender} and {@link Numerus}.
	 * @return A list of all terms, that fit the constraints outlined above. The resulting list is sorted according to the
	 *         terms' frequencies.
	 *
	 * @implNote
	 *           The frequencies compared between the terms when sorting the lists are determined by the {@link TermComp}
	 *           object. It adds the frequency of the specific form of the term (which might have been newly created and
	 *           thus have a frequency of 1) and the frequency of the general term (i.e. its {@link TermVariations}). Both
	 *           measures are multiplied by a certain bias, which must be adjusted in the {@link TermComp} class.
	 */
	public List<NounTerm> query(GrammaticalCase grammaticalCase, Gender gender, Numerus numerus, Integer syllableMin, Integer syllableMax) {
		List<NounTerm> res = new ArrayList<>();

		nouns.forEach(x -> {
			Optional<NounTerm> t = TermVariations.createTerm(x, gender, grammaticalCase, numerus, dict);
			if (t.isPresent()) {
				int syllableAmount = t.get().getSyllableAmount();
				if (syllableMin <= syllableAmount && syllableAmount <= syllableMax) res.add(t.get());
			}
		});

		res.sort(new TermComp<>());
		return res;
	}

	public List<NounTerm> query(GrammaticalCase grammaticalCase, Gender gender, Numerus numerus) {
		return query(grammaticalCase, gender, numerus, 0, Integer.MAX_VALUE);
	}

	public List<NounTerm> queryNounsBy(Predicate<? super NounTerm> f) {
		return queryBy(nouns, f);
	}

	public List<VerbTerm> queryVerbsBy(Predicate<? super VerbTerm> f) {
		return queryBy(verbs, f);
	}

	public List<NounTerm> queryNounsBySyllableRange(Integer minSyllableAmount, Integer maxSyllableAmount) {
		return TermCollection.queryBySyllableRange(nouns, minSyllableAmount, maxSyllableAmount);
	}

	public List<VerbTerm> queryVerbsBySyllableRange(Integer minSyllableAmount, Integer maxSyllableAmount) {
		return TermCollection.queryBySyllableRange(verbs, minSyllableAmount, maxSyllableAmount);
	}

	public List<NounTerm> queryNounsBySyllableAmount(Integer syllableAmount) {
		return TermCollection.queryBySyllableAmount(nouns, syllableAmount);
	}

	public List<VerbTerm> queryVerbsBySyllableAmount(Integer syllableAmount) {
		return TermCollection.queryBySyllableAmount(verbs, syllableAmount);
	}

	public List<NounTerm> queryNounsBy(GrammaticalCase grammaticalCase) {
		return TermCollection.queryBy(nouns, grammaticalCase);
	}

	public List<NounTerm> queryNounsBy(Boolean onlyPluralTerms) {
		return TermCollection.queryBy(nouns, onlyPluralTerms);
	}

	public List<VerbTerm> queryVerbsBy(Boolean onlyPluralTerms) {
		return TermCollection.queryBy(verbs, onlyPluralTerms);
	}

	public List<NounTerm> queryNounsBy(Gender gender) {
		return TermCollection.queryBy(nouns, gender);
	}

	public List<NounTerm> mostCommonNouns() {
		return TermCollection.mostCommonTerms(nouns);
	}

	public List<VerbTerm> mostCommonVerbs() {
		return TermCollection.mostCommonTerms(verbs);
	}

	public NounTerm getRandomNoun() {
		return TermCollection.getRandomTerm(nouns.stream().toList());
	}

	public VerbTerm getRandomVerb() {
		return TermCollection.getRandomTerm(verbs.stream().toList());
	}

	// Static Query Functions

	public static <T extends Term<T>> List<T> queryBySyllableRange(List<TermVariations<T>> terms, Integer minSyllableAmount,
			Integer maxSyllableAmount) {
		return TermCollection.queryBy(terms, x -> minSyllableAmount <= x.syllableAmount && x.syllableAmount <= maxSyllableAmount);
	}

	public static <T extends Term<T>> List<T> queryBySyllableAmount(List<TermVariations<T>> terms, Integer syllableAmount) {
		return TermCollection.queryBy(terms, x -> Objects.equals(x.syllableAmount, syllableAmount));
	}

	public static List<NounTerm> queryBy(List<TermVariations<NounTerm>> terms, GrammaticalCase grammaticalCase) {
		return TermCollection.queryBy(terms, x -> x.getGrammaticalCase() == grammaticalCase);
	}

	public static <T extends Term<T>> List<T> queryBy(List<TermVariations<T>> terms, Boolean onlyPluralTerms) {
		return TermCollection.queryBy(terms, x -> (x.getNumerus() == Numerus.PLURAL) == onlyPluralTerms);
	}

	public static List<NounTerm> queryBy(List<TermVariations<NounTerm>> terms, Gender gender) {
		return TermCollection.queryBy(terms, x -> x.getGender() == gender);
	}

	public static <T extends Term<T>> List<T> queryBy(List<TermVariations<T>> terms, Predicate<? super T> f) {
		List<T> res = new ArrayList<>();
		terms.forEach(x -> res.addAll(x.queryBy(f)));
		res.sort(new TermComp<>());
		return res;
	}

	public static <T extends Term<T>> List<T> mostCommonTerms(List<TermVariations<T>> terms) {
		List<T> res = terms.stream().map(x -> x.getVariations().values().stream()).flatMap(Function.identity()).toList();
		res.sort(new TermComp<>());
		return res.subList(0, 10);
	}

	public static <T extends Term<T>> T getRandomTerm(List<TermVariations<T>> terms) {
		int i = rand.nextInt(terms.size());
		Collection<T> ts = terms.get(i).getVariations().values();
		int j = rand.nextInt(ts.size());
		return ts.stream().toList().get(j);
	}

	// Boilerplate:

	public List<TermVariations<NounTerm>> getNouns() {
		return this.nouns;
	}

	public void setNouns(List<TermVariations<NounTerm>> nouns) {
		this.nouns = nouns;
	}

	public List<TermVariations<VerbTerm>> getVerbs() {
		return this.verbs;
	}

	public void setVerbs(List<TermVariations<VerbTerm>> verbs) {
		this.verbs = verbs;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof TermCollection termCollection)) {
			return false;
		}
		return Objects.equals(nouns, termCollection.nouns) && Objects.equals(verbs, termCollection.verbs);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nouns, verbs);
	}

	@Override
	public String toString() {
		return "{" + " nouns='" + getNouns() + "'" + ", verbs='" + getVerbs() + "'" + "}";
	}

}
