package org.se.songgen2backend.text.metric;

import java.io.IOException;
import java.util.List;

import org.se.songgen2backend.text.analysis.FileReader;
import org.se.songgen2backend.text.analysis.model.Sentence;

/**
 * @author Jakob Kautz
 */

/*
 * Gets list of split sentences from Preprocessor function
 * reads Mood templates and compares these words to the current sentence
 * if sentence contains a word of the string count moodCounter one up
 * returns mood with highest count
 */

public class MoodAnalyzer {
	private static final String NEWLINE_REGEX = "\\r?\\n";

	public static MoodType getMood(List<Sentence> sentences) throws IOException {
		/*
		 * Global variables
		 * Counter to figure out how many words of each mood r in the text
		 * Strings that contain mood Words
		 */
		int happyCounter = 0;
		int hulkCounter = 0;
		int thirstyyyCounter = 0;
		int sadCounter = 0;
		String[] negation = FileReader.main("./src/main/resources/static/text/WordNegations.txt").split(NEWLINE_REGEX);
		String[] happy = FileReader.main("./src/main/resources/static/text/HappyMood.txt").split(NEWLINE_REGEX);
		String[] sad = FileReader.main("./src/main/resources/static/text/SadMood.txt").split(NEWLINE_REGEX);
		String[] angry = FileReader.main("./src/main/resources/static/text/HulkMood.txt").split(NEWLINE_REGEX);
		String[] horny = FileReader.main("./src/main/resources/static/text/ThirstyyyMood.txt").split(NEWLINE_REGEX);

		/*
		 * Traversing through Sentences (input sentences from original text input after preopocessor)
		 */
		for (Sentence sentence : sentences) {
			boolean isInverted = false;
			for (String word : sentence) {
				if (containsWord(negation, word)) {
					isInverted = true;
				} else if (containsWord(happy, word)) {
					if (isInverted) sadCounter++;
					else happyCounter++;
					isInverted = false;
				} else if (containsWord(sad, word)) {
					if (isInverted) happyCounter++;
					else sadCounter++;
					isInverted = false;
				} else if (containsWord(angry, word)) {
					if (isInverted) isInverted = false;
					else hulkCounter++;
				} else if (containsWord(horny, word)) {
					if (isInverted) isInverted = false;
					else thirstyyyCounter++;
				}
			}
		}

		if (happyCounter >= sadCounter && happyCounter >= hulkCounter && happyCounter >= thirstyyyCounter) return MoodType.HAPPY;
		else if (sadCounter >= happyCounter && sadCounter >= hulkCounter && sadCounter >= thirstyyyCounter) return MoodType.SAD;
		else if (hulkCounter >= sadCounter && hulkCounter >= happyCounter && hulkCounter >= thirstyyyCounter) return MoodType.ANGRY;
		else return MoodType.HORNY;
	}
	/*
	 * Function to traverse through StringArray and check if it contains word from anoher String
	 * Like the fucker u have for Lists but not for Stringarrays cuz Java sucks:/
	 */

	public static boolean containsWord(String[] arr, String s) {
		for (String t : arr) {
			if (t.equalsIgnoreCase(s)) return true;
		}
		return false;
	}
}
