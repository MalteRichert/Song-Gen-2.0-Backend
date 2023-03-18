package org.se.songgen2backend.text.metric;

/**
 * @author Jakob Kautz
 */
public class WordCounter {
	public static int[] countWords(String content) {
		String[] sentenceParts = content.split("[.!?]");
		int ctCsTotal = sentenceParts.length;
		int wordsTotal = 0;
		int words;

		for (String currentSentence : sentenceParts) {

			words = 0;
			for (int j = 0; j < currentSentence.length(); j++) {

				int state = 0;
				if (j + 1 < currentSentence.length() && currentSentence.charAt(j) == '-' && currentSentence.charAt(j + 1) == '\n') {
					state = 0;
				} else if (currentSentence.charAt(j) == ' ' || currentSentence.charAt(j) == '\n' || currentSentence.charAt(j) == '\t') {
					state = 1;
				}
				if (state == 1) {
					words = words + 1;
				}
			}
			wordsTotal = wordsTotal + words;

		}

		return new int[] { wordsTotal, ctCsTotal };
	}
}
