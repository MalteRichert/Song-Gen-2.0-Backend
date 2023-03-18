package org.se.songgen2backend.text.analysis.dict;

import org.se.songgen2backend.text.analysis.*;
import org.se.songgen2backend.text.analysis.model.*;
import org.se.songgen2backend.text.analysis.*;
import org.se.songgen2backend.text.analysis.model.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

/**
 * @author Val Richter
 * @reviewer Jakob Kautz
 *
 *           This class stores a lot of data from the resources and offers much of the logic for interacting with said
 *           data. Specifically, it also offers the ability to check the type of a word (i.e. is a noun/verb/other) and
 *           to build a full {@link Term} object from a given word.
 */
public class Dict {
	WordList nounSuffixes = new WordList();
	WordList nounPrefixes = new WordList();
	WordList nouns = new WordList();
	WordList verbSuffixes = new WordList();
	WordList verbPrefixes = new WordList();
	WordList verbs = new WordList();
	WordList diphthongs = new WordList();
	WordList umlautChanges = new WordList();
	WordList addableNounCompParts = new WordList();
	WordList subtractableNounCompParts = new WordList();
	WordList addableVerbCompParts = new WordList();
	WordList subtractableVerbCompParts = new WordList();
	WordList genderChangeSuffixes = new WordList();
	List<Declination> declinatedAffixes = new ArrayList<>();
	List<Conjugation> conjugatedAffixes = new ArrayList<>();
	final String baseKey;

	static final String GENDER_KEY = "gender";
	static final String TO_UMLAUT_KEY = "toUmlaut";
	static final String DEFAULT_BASE_KEY = "radix";
	static final String UMALUT_UPDATE_KEY = "with";

	/**
	 * Create a new {@link Dict} object. All the data will be read and parsed from the resource directory automatically.
	 *
	 * @param dirPath
	 *            The path to the resource directory, where all of the files for the {@link Dict} can be found.
	 * @throws IOException
	 */
	public Dict(Path dirPath) throws IOException {
		this.baseKey = DEFAULT_BASE_KEY;

		Parser.readCSV(dirPath.resolve("nounsDict"), this.nouns);
		Parser.readCSV(dirPath.resolve("verbsDict"), this.verbs);
		Parser.readCSV(dirPath.resolve("diphtongs"), this.diphthongs);
		Parser.readCSV(dirPath.resolve("umlautChanges"), this.umlautChanges);
		Parser.readCSV(dirPath.resolve("genderChangeSuffixes"), genderChangeSuffixes);
		Parser.parseCSV(dirPath.resolve("compoundParts"), row -> {
			switch (row.get("type")) {
				case "nounAddition" -> addableNounCompParts.insert(row);
				case "nounSubtraction" -> subtractableNounCompParts.insert(row);
				case "verbAddition" -> addableVerbCompParts.insert(row);
				case "verbSubtraction" -> subtractableVerbCompParts.insert(row);
			}
		});
		Parser.parseCSV(dirPath.resolve("affixesDict"), row -> {
			switch (row.get("type")) {
				case "nounSuffix" -> nounSuffixes.insert(row);
				case "nounPrefix" -> nounPrefixes.insert(row);
				case "verbSuffix" -> verbSuffixes.insert(row);
				case "verbPrefix" -> verbPrefixes.insert(row);
			}
		});
		Parser.parseCSV(dirPath.resolve("declinatedAffixes"), declinatedAffixes, Declination.class);
		Parser.parseCSV(dirPath.resolve("conjugatedAffixes"), conjugatedAffixes, Conjugation.class);
	}

	public static Dict getDefault() throws IOException {
		return new Dict(Path.of("", "./src/main/resources/static/dictionary"));
	}

	// Make a word into a term

	public List<WordStemmer> getPossibleNounStems(String s) {
		return getPossibleStems(s, nouns, declinatedAffixes, nounSuffixes, nounPrefixes, addableNounCompParts, subtractableNounCompParts);
	}

	public List<WordStemmer> getPossibleVerbStems(String s) {
		return getPossibleStems(s, verbs, conjugatedAffixes, verbSuffixes, nounPrefixes, addableVerbCompParts, subtractableVerbCompParts);
	}

	/**
	 * Get a list of all possible {@link WordStemmer} objects that can be formed from the given {@link String}.
	 *
	 * @implNote This calculation can take relatively long, so calling this function too often should be avoided.
	 */
	private List<WordStemmer> getPossibleStems(String s, WordList terms, List<? extends TermAffix> termAffixes, WordList suffixes, WordList prefixes,
                                               WordList addableCompParts, WordList subtractableCompParts) {
		WordStemmer[] l = WordStemmer.radicalize(s, terms, termAffixes, suffixes, prefixes, addableCompParts, subtractableCompParts, 3, diphthongs,
				umlautChanges, baseKey);
		List<WordStemmer> res = new ArrayList<>();

		for (WordStemmer w : l) {
			if (terms.has(w.getStem()) || (w.getSuffixes().size() > 1
					&& Util.any(w.getSuffixes(), data -> data.containsKey("certain") && Parser.parseBool(data.get("certain"))))) {
				res.add(w);
			}
		}
		return res;
	}

	// Biases for calculating a score for a given WordStemmer object. Specifically used in the heuristicForStemCmp function.
	static final int AFFIX_COUNT_BIAS = -6;
	static final int ALL_COMPOUNDS_IN_DICTIONARY = 100;
	static final int NO_COMPOUNDS = 100;
	static final int DECLINATED_SUFFIX_GENDER_BIAS = 20;
	static final int LAST_SUFFIX_GENDER_BIAS = 10;
	static final int ALL_SUFFIXES_GENDER_BIAS = 1;
	static final int ALL_SUFFIXES_DICT_GENDER_BIAS = 2;
	static final int STEM_NOUN_EXCEPTION_BIAS = -10;
	static Gender tmpGender = null;

	/**
	 * Calculate a score for comparing {@link WordStemmer} objects with one another. The closer a given {@link WordStemmer}
	 * object is to the original String the higher its score generally.
	 *
	 * @param stem
	 *            The {@link WordStemmer} object for which the score should be calculated.
	 * @param areNouns
	 *            Whether the {@link WordStemmer} object represents a noun. Some calculations for the score are different
	 *            based on whether a noun or a verb is being analyzed.
	 * @param dict
	 *            A reference to the {@link Dict} object
	 * @return an integer that approximately measures how likely the given stem is to be the correct interpretation of the
	 *         original string. The number only has a meaning when compared with the heuristic for other {@link WordStemmer}
	 *         objects.
	 */
	private static int heuristicForStemCmp(WordStemmer stem, boolean areNouns, WordList dict) {
		int count = 0;

		count += AFFIX_COUNT_BIAS * stem.affixesCount();

		if (stem.getCompounds().isEmpty()) count += NO_COMPOUNDS;
		else if (Util.all(stem.getCompounds(), compound -> dict.has(compound.get()))) count += ALL_COMPOUNDS_IN_DICTIONARY;

		try {
			if (areNouns) {
				Optional<WordWithData> dictEntry = dict.get(stem.getStem());
				if (dictEntry.isPresent()) {
					Gender dictGender = dictEntry.get().get(GENDER_KEY, Gender.class).get();

					if (((Declination) stem.getGrammartizedSuffix()).getGender() == dictGender) count += DECLINATED_SUFFIX_GENDER_BIAS;

					if (!stem.getSuffixes().isEmpty()) {
						if (stem.getSuffixes().get(stem.getSuffixes().size() - 1).get(GENDER_KEY, Gender.class).get() == dictGender) {
							count += LAST_SUFFIX_GENDER_BIAS;
						}

						tmpGender = null;
						if (Util.all(stem.getSuffixes(), suffix -> {
							Gender currentGender = suffix.get(GENDER_KEY, Gender.class).get();
							if (tmpGender == null || currentGender == tmpGender) {
								tmpGender = currentGender;
								return true;
							} else return false;
						})) {
							count += ALL_SUFFIXES_GENDER_BIAS;
						}
						if (tmpGender == dictGender) count += ALL_SUFFIXES_DICT_GENDER_BIAS;
					}
				}
			}
		} catch (Exception e) {
			// Something went wrong when treating the stem as a noun, probably because of
			// some NullPointer.
			// Since a noun was expected, we will decrease the count for this exception
			count += STEM_NOUN_EXCEPTION_BIAS;
		}

		return count;
	}

	/**
	 * Retrieve the best stem of a list of possible stems. The best stem is defined via a bunch of heuristics (see
	 * "heuristicForStemCmp").
	 * A Word is only considered, if its heuristic is positive and if it appears in the list of nouns/verbs.
	 *
	 * @param stems
	 *            The list of possible stems to choose from.
	 * @param areNouns
	 *            Whether the stems are all nounStems. If set to true,
	 *            additional metadata like the word's gender are taken into
	 *            account
	 * @return Returns the best WordStemmer object of the input and nothing, if the
	 *         list of stems is empty.
	 */
	public Optional<WordStemmer> getBestOfStems(List<WordStemmer> stems, boolean areNouns) {
		WordList dict = areNouns ? nouns : verbs;
		Optional<WordStemmer> best = Optional.empty();
		int bestCount = 0;
		for (WordStemmer stem : stems) {
			if (dict.has(stem.getStem())) {
				int currentCount = heuristicForStemCmp(stem, areNouns, dict);
				if (currentCount > 0 && (best.isEmpty() || currentCount > bestCount)) {
					best = Optional.of(stem);
					bestCount = currentCount;
				}
			}
		}
		return best;
	}

	/**
	 * Produce the Tag for a given word.
	 *
	 * @implNote This method tries to find the best {@link WordStemmer} representations for the word as a verb and then as a
	 *           noun. These calculations are rather costly and its results are thus stored in the data attribute of the
	 *           {@link Tag}. When building the Term later, that {@link WordStemmer} object is necessary and is only
	 *           calculated again if it hasn't been cached in the Tag already.
	 *
	 * @param s
	 *            The word to create the Tag for.
	 */
	public Tag tagWord(String s) {
		Optional<WordStemmer> verb = getBestOfStems(getPossibleVerbStems(s), false);
		if (verb.isPresent()) return new Tag(s, TagType.VERB, verb.get());

		Optional<WordStemmer> noun = getBestOfStems(getPossibleNounStems(s), true);
		if (noun.isPresent()) return new Tag(s, TagType.NOUN, noun.get());

		return new Tag(s, TagType.OTHER);
	}

	/**
	 * Add WordStemmer data in case it wasn't produced when tagging the word already. This can happen, when the Analyzer
	 * tags the word before giving it to the Dictionary (specifically when the word is immediately recognized as a noun
	 * because of its capitalization)
	 *
	 * @param t
	 *            The given Tag
	 * @param areNouns
	 *            Whether the Word is supposed to be a noun
	 */
	private void addWordStemmerData(Tag t, boolean areNouns) {
		if (t.getData().isEmpty()) {
			Function<String, List<WordStemmer>> f = areNouns ? this::getPossibleNounStems : this::getPossibleVerbStems;
			Optional<WordStemmer> stem = getBestOfStems(f.apply(t.getWord()), areNouns);
			t.setData(stem);
		}
	}

	/**
	 * Try to build a {@link NounTerm} from a given {@link Tag}. If it's not possible to create a noun from the given word
	 * (possibly because the word simply isn't a noun), then an empty {@link Optional} will be returned.
	 */
	public Optional<NounTerm> buildNounTerm(Tag t) {
		try {
			addWordStemmerData(t, true);

			if (t.getData().isPresent()) {
				WordStemmer data = t.getData().get();
				Declination declinatedSuffix = (Declination) data.getGrammartizedSuffix();

				StringBuilder radixBuilder = new StringBuilder();
				radixBuilder.append(data.getCompoundsStr());
				radixBuilder.append(data.getStem());

				Numerus numerus = declinatedSuffix.getNumerus();
				GrammaticalCase grammaticalCase = declinatedSuffix.getGrammaticalCase();
				Gender gender = declinatedSuffix.getGender();

				return Optional.of(new NounTerm(radixBuilder.toString(), t.getWord(), numerus, grammaticalCase, gender));
			} else {
				return Optional.empty();
			}
		} catch (Exception e) {
			return Optional.empty();
		}
	}

	/**
	 * Try to build a {@link VerbTerm} from a given {@link Tag}. If it's not possible to create a verb from the given word
	 * (possibly because the word simply isn't a verb), then an empty {@link Optional} will be returned.
	 */
	public Optional<VerbTerm> buildVerbTerm(Tag t) {
		addWordStemmerData(t, false);

		if (t.getData().isEmpty()) return Optional.empty();

		WordStemmer data = t.getData().get();
		String radix = data.getStem();
		String infinitive = radix;
		if (verbs.has(radix)) infinitive = verbs.get(radix).get().get("infinitive");
		else if (verbs.has(t.getWord())) infinitive = verbs.get(t.getWord()).get().get("infinitive");

		VerbTerm verb = new VerbTerm(radix, t.getWord(), data.getGrammartizedSuffix().getNumerus(), infinitive);
		return Optional.of(verb);
	}

	/**
	 * Create a new variation of the Term. It is not checked whether the given variation of the term already exists in the
	 * `variations` object. The new variation is added automatically to the `variations` object, to avoid calculating the
	 * same variation again. If the given term couldn't be coerced into the desired grammatical form, an empty
	 * {@link Optional} is returned.
	 *
	 * @param variations
	 *            The {@link TermVariations} object to which this new variation will belong. The new variation is
	 *            automaticaly added to this object.
	 * @param gender
	 *            The desired {@link Gender} for the new variation. Only some {@link Term} objects are allowed to change
	 *            their gender (for example: "Lehrer" into "Lehrerin").
	 * @param grammaticalCase
	 *            The desired {@link GrammaticalCase} for the new variation.
	 * @param numerus
	 *            The desired {@link Numerus} for the new variation.
	 * @return The new variation in form of a {@link NounTerm} or an empty {@link Optional} if the term couldn't be coerced
	 *         into the desired form.
	 */
	public Optional<NounTerm> createNounTerm(TermVariations<NounTerm> variations, Gender gender, GrammaticalCase grammaticalCase, Numerus numerus) {
		String radix = variations.getRadix();

		NounTerm tmp = ((NounTerm) variations.getRandomTerm());
		if (tmp.getGender().equals(gender)) {
			return createNounTermHelper(variations, radix, null, gender, grammaticalCase, numerus);
		} else if (tmp.getChangeableGender()) {
			Optional<WordWithData> genderChangeSuffix = genderChangeSuffixes.find(suffix -> {
				Optional<Gender> suffixGender = suffix.get(GENDER_KEY, Gender.class);
				return suffixGender.isPresent() && suffixGender.get().equals(gender);
			});

			if (genderChangeSuffix.isPresent()) {
				return createNounTermHelper(variations, radix, genderChangeSuffix.get(), gender, grammaticalCase, numerus);
			}
		}
		return Optional.empty();
	}

	private Optional<NounTerm> createNounTermHelper(TermVariations<NounTerm> variations, final String radix, WordWithData genderChangeSuffix,
			Gender gender, GrammaticalCase grammaticalCase, Numerus numerus) {
		List<Declination> possibleSuffixes = Util.findAll(declinatedAffixes,
				s -> s.getGender() == gender && s.getGrammaticalCase() == grammaticalCase && s.getNumerus() == numerus);
		Tuple<List<Declination>, List<Declination>> filteredSuffixes = Util.filter(possibleSuffixes,
				s -> s.getRadix().isEmpty() || (!radix.endsWith(s.getRadix()) && !radix.endsWith(s.getRadix().substring(0, 1))));

		if (!possibleSuffixes.isEmpty()) {
			Declination suffix;
			if (!filteredSuffixes.getY().isEmpty()) {
				Declination s = filteredSuffixes.getY().get(0);
				if (radix.endsWith(s.getRadix()))
					suffix = new Declination("", s.getGrammaticalCase(), s.getGender(), s.getNumerus(), s.getType(), false);
				else suffix = new Declination(s.getRadix().substring(1), s.getGrammaticalCase(), s.getGender(), s.getNumerus(), s.getType(), true);
			} else suffix = filteredSuffixes.getX().get(0);

			possibleSuffixes.get(0);
			boolean toUmlaut = suffix.getToUmlaut() && (genderChangeSuffix == null || genderChangeSuffix.get(TO_UMLAUT_KEY, Boolean.class).get());

			StringBuilder strbuilder = new StringBuilder(radix);
			if (toUmlaut) strbuilder = new StringBuilder(changeUmlaut(umlautChanges, diphthongs, radix, true));
			if (genderChangeSuffix != null) strbuilder.append(genderChangeSuffix.get());
			strbuilder.append(suffix.getRadix());

			String word = strbuilder.toString();
			NounTerm term = new NounTerm(radix, word, numerus, grammaticalCase, gender, genderChangeSuffix != null, variations);
			variations.add(term);
			return Optional.of(term);
		}

		return Optional.empty();
	}

	/**
	 * Change the Umlaute of a word. Either adding or removing Umlaute. Only the last "a", "o" or "u" would be effected by
	 * this change, as german words usually only change the last possible vowel to an Umlaut.
	 * 
	 * @param umlautChanges
	 *            The list of possible umlaut changes. This is a list stored in the {@link Dict} object.
	 * @param diphtongs
	 *            The list of diphtongs. This is a list stored in the {@link Dict} object.
	 * @param s
	 *            The String that should be changed.
	 * @param addUmlaut
	 *            Whether to add or remove Umlaute from the word.
	 * @return Returns the updated form of the String.
	 */
	public static String changeUmlaut(WordList umlautChanges, WordList diphtongs, String s, boolean addUmlaut) {
		char[] chars = s.toCharArray();
		boolean foundUmlaut = false;

		String initialKey = addUmlaut ? DEFAULT_BASE_KEY : UMALUT_UPDATE_KEY;
		String updatedKey = addUmlaut ? UMALUT_UPDATE_KEY : DEFAULT_BASE_KEY;

		for (int i = chars.length - 1; i >= 0 && !foundUmlaut; i--) {
			// Current character is part of a diphtong and should be skipped
			// Umlaut-changes for diphtongs are handled by the umlauts with several
			// characters (like au -> äu)
			if (i > 1 && diphtongs.has(chars[i] + "" + chars[i - 1]) && !(i > 2 && diphtongs.has(chars[i - 1] + "" + chars[i - 2]))) {
				continue;
			}

			for (WordWithData umlaut : umlautChanges) {
				char[] initial = umlaut.get(initialKey).toCharArray();
				boolean comparison = true;
				for (int j = 0; comparison && j < initial.length && j + i < chars.length; j++) {
					if (initial[j] != chars[i + j]) {
						comparison = false;
					}
				}
				// If the comparison was correct, update the umlaut sequence
				// break to stop checking for other umlaut sequences
				if (comparison) {
					char[] updated = umlaut.get(updatedKey).toCharArray();
					for (int j = 0; comparison && j < updated.length && j + i < chars.length; j++) {
						chars[i + j] = updated[j];
					}
					foundUmlaut = true;
					break;
				}
			}
		}
		return new String(chars);
	}

	// Getters, Setters & other Boilerplate
	public WordList getNounSuffixes() {
		return this.nounSuffixes;
	}

	public WordList getNounPrefixes() {
		return this.nounPrefixes;
	}

	public WordList getNouns() {
		return this.nouns;
	}

	public WordList getVerbSuffixes() {
		return this.verbSuffixes;
	}

	public WordList getVerbPrefixes() {
		return this.verbPrefixes;
	}

	public WordList getVerbs() {
		return this.verbs;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Dict dictionary)) {
			return false;
		}
		return Objects.equals(nounSuffixes, dictionary.nounSuffixes) && Objects.equals(nounPrefixes, dictionary.nounPrefixes)
				&& Objects.equals(nouns, dictionary.nouns) && Objects.equals(verbSuffixes, dictionary.verbSuffixes)
				&& Objects.equals(verbPrefixes, dictionary.verbPrefixes) && Objects.equals(verbs, dictionary.verbs);
	}

	@Override
	public int hashCode() {
		return Objects.hash(nounSuffixes, nounPrefixes, nouns, verbSuffixes, verbPrefixes, verbs);
	}

	@Override
	public String toString() {
		return "{" + " nounSuffixes='" + getNounSuffixes() + "'" + ", nounPrefixes='" + getNounPrefixes() + "'" + ", nouns='" + getNouns() + "'"
				+ ", verbSuffixes='" + getVerbSuffixes() + "'" + ", verbPrefixes='" + getVerbPrefixes() + "'" + ", verbs='" + getVerbs() + "'" + "}";
	}

}
