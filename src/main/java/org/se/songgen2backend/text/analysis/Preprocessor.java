package org.se.songgen2backend.text.analysis;

import java.util.*;

import org.se.songgen2backend.PartialProgressTask;
import org.se.songgen2backend.text.analysis.model.Sentence;

public class Preprocessor {
	protected String text;

	public Preprocessor(String text) {
		this.text = text;
	}

	protected List<Sentence> call() throws Exception {
		return preprocess();
	}

	/**
	 * Preprocess the text given to this object. The preprocessing removes unwanted characters (e.g. punctuation) and splits
	 * the text into a list of {@link Sentence} objects. Hyphens and underscores are also seen as splitting words (for
	 * example: in a PDF words often get split from one line to the next and connected via a hyphen). These
	 * splitting-characters ("-" or "_") are removed and the split word is put back together again.
	 *
	 * @implNote The preprocessing runs in `O(n)`, as it only loops through the text one time.
	 *
	 * @return
	 */
	public List<Sentence> preprocess() {
		String wordSplitter = "-_";
		boolean splitLastWord = false;
		String sentenceEnds = ".!?";
		String otherPunctuation = ",;:-()[]{}";
		List<Sentence> sentences = new ArrayList<>();
		Sentence currentSentence = new Sentence();
		char[] chars = text.toCharArray();
		StringBuilder currentWord = new StringBuilder();

		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			// ignore whitespace
			if (Character.isWhitespace(c)) {
				if (!currentWord.isEmpty() && !splitLastWord) {
					currentSentence.add(currentWord.toString());
					currentWord.delete(0, currentWord.length());
				}
			}
			// End sentence at specific punctuation (e.g. at .!?)
			else if (sentenceEnds.indexOf(c) != -1) {
				if (!currentWord.isEmpty()) {
					currentSentence.add(currentWord.toString());
					currentWord.delete(0, currentWord.length());
				}
				splitLastWord = false;
				Sentence copiedSentence = new Sentence(currentSentence);
				sentences.add(copiedSentence);
				currentSentence.clear();
			}
			// Current word gets split
			else if (!currentWord.isEmpty() && !splitLastWord && wordSplitter.indexOf(c) != -1) {
				splitLastWord = true;
			}
			// punctuation that doesn't end a sentence
			else if (otherPunctuation.indexOf(c) != -1) {
				if (!currentWord.isEmpty()) {
					currentSentence.add(currentWord.toString());
					currentWord.delete(0, currentWord.length());
				}
				splitLastWord = false;
			}
			// otherwise we just have another character for the word
			else {
				currentWord.append(c);
				splitLastWord = false;
			}

		}

		if (!currentWord.isEmpty()) {
			currentSentence.add(currentWord.toString());
		}
		if (!currentSentence.isEmpty()) {
			sentences.add(currentSentence);
		}
		return sentences;
	}
}
