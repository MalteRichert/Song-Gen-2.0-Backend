package org.se.songgen2backend.text.analysis;

import java.util.*;

import org.se.songgen2backend.PartialProgressTask;
import org.se.songgen2backend.text.analysis.dict.Dict;
import org.se.songgen2backend.text.analysis.model.Sentence;
import org.se.songgen2backend.text.analysis.model.TagType;
import org.se.songgen2backend.text.analysis.dict.*;
import org.se.songgen2backend.text.analysis.model.*;

/**
 * @author Val Richter
 * @reviewer Jakob Kautz
 */
public class Analyzer {
	private final List<Sentence> sentences;
	private final Dict dict;

	public Analyzer(List<Sentence> sentences, Dict dict) {
		this.sentences = sentences;
		this.dict = dict;
	}

	protected TermCollection call() throws Exception {
		List<Tag> tags = tag();

		TermCollection res = buildTerms(tags);
		return res;
	}

	/**
	 * Count how many characters are capitalized in a given String
	 */
	private int capitalizedCount(String str) {
		int count = 0;
		for (char c : str.toCharArray()) {
			if (Character.isUpperCase(c)) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Checks whether the given word is capitalized. If `word` has more than one capitalized character, it is not considered
	 * capitalized.
	 */
	private boolean isCapitalized(String word) {
		return capitalizedCount(word) == 1 && Character.isUpperCase(word.charAt(0));
	}

	/**
	 * Tag a list of sentences
	 *
	 * @return A list of Tags. Each tag also stores the associated word, so they don't need to be coupled with the words in
	 *         `sentences` seperately again.
	 */
	public List<Tag> tag() {
		List<Tag> tags = new ArrayList<>();

		int sentencesAmount = sentences.size();
		for (int i = 0; i < sentencesAmount; i++) {
			Sentence sentence = sentences.get(i);

			double wordsAmount = sentence.size();
			for (int j = 0; j < wordsAmount; j++) {
				String word = sentence.get(j);
				Tag tag;

				// If it's not the first word in the sentence and is capitalized, it's a noun
				// this check can only be trusted on, if only nouns are capitalized
				if (j != 0 && isCapitalized(word)) {
					tag = new Tag(word, TagType.NOUN);
				} // Otherwise, let the dictionary tag the word
				else {
					tag = dict.tagWord(word);
				}

				tags.add(tag);
			}
		}
		return tags;
	}

	/**
	 * Build Terms from the given Tags. The Terms are stored as {@link NounTerm} and {@link VerbTerm} inside a
	 * {@link TermCollection} object.
	 * 
	 * @param tags
	 *            The list of tags, that is created in this class' tag() method.
	 * @return The {@link TermCollection}, that holds all nouns and verbs that were found in the text.
	 */
	public TermCollection buildTerms(List<Tag> tags) {
		Map<String, TermVariations<NounTerm>> nounVariations = new HashMap<>();
		Map<String, TermVariations<VerbTerm>> verbVariations = new HashMap<>();

		int tagsAmount = tags.size();
		for (int i = 0; i < tagsAmount; i++) {
			Tag t = tags.get(i);
			if (!t.is(TagType.OTHER)) {

				if (t.is(TagType.NOUN)) {
					Optional<NounTerm> term = dict.buildNounTerm(t);
					if (term.isPresent()) {
						if (nounVariations.containsKey(term.get().getRadix())) {
							TermVariations<NounTerm> variations = nounVariations.get(term.get().getRadix());
							term.get().setVariations(variations);
							variations.add(term.get());
						} else {
							TermVariations<NounTerm> variations = new TermVariations<>(term.get());
							term.get().setVariations(variations);
							nounVariations.put(term.get().getRadix(), variations);
						}
					}
				} else {
					Optional<VerbTerm> term = dict.buildVerbTerm(t);
					if (term.isPresent()) {
						if (verbVariations.containsKey(term.get().getRadix())) {
							TermVariations<VerbTerm> variations = verbVariations.get(term.get().getRadix());
							term.get().setVariations(variations);
							variations.add(term.get());
						} else {
							TermVariations<VerbTerm> variations = new TermVariations<>(term.get());
							term.get().setVariations(variations);
							verbVariations.put(term.get().getRadix(), variations);
						}
					}
				}
			}
		}
		return new TermCollection(dict, nounVariations, verbVariations);
	}

}
