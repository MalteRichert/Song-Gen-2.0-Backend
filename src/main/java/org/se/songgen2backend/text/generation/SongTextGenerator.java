package org.se.songgen2backend.text.generation;

import org.se.songgen2backend.music.model.Genre;
import org.se.songgen2backend.music.model.Structure;
import org.se.songgen2backend.text.analysis.NounTerm;
import org.se.songgen2backend.text.analysis.Term;
import org.se.songgen2backend.text.analysis.TermCollection;
import org.se.songgen2backend.text.analysis.VerbTerm;
import org.se.songgen2backend.text.analysis.model.Gender;
import org.se.songgen2backend.text.analysis.model.GrammaticalCase;
import org.se.songgen2backend.text.analysis.model.Numerus;
import org.se.songgen2backend.text.metric.Hyphenizer;
import org.se.songgen2backend.text.metric.MoodType;

import java.util.*;

/**
 * @author Olivier Stenzel
 */
public class SongTextGenerator {

	private final TemplateImporter templateImporter = new TemplateImporter();
	List<TextTemplate> unusedTextTemplateList;

	// text
	private TermCollection termCollection;
	private final Map<Integer, String> usedWords = new HashMap<>(); // to check if a Term was used in the Song before
	private final Random ran = new Random();

	/**
	 * returns a Hashmap which contains the Songtext split into parts with the
	 * number of syllables in each part
	 */
	public Map<String, List<String[][]>> generateSongText(Structure structure, TermCollection termCollection, MoodType mood) {

		List<String[]> songText = new ArrayList<>();
		this.termCollection = termCollection;
		List<String> order = structure.getOrder();
		unusedTextTemplateList = templateImporter.getTemplates(structure.getGenre());

		for (String s : order) {
			songText.add(generateStrophe(structure.getGenre(), structure.getParts().get(s).getLength(), mood));
		}

		return getPartText(order, songText);
	}

	/**
	 * returns a text (strophe) split into parts (bars)
	 */
	private String[] generateStrophe(Genre genre, int partLength, MoodType mood) {
		TextTemplate textTemplate = getUnusedStrophe(partLength, genre, mood);// 1000 just for the feeling

		// go through the strophe and store the verses
		String[] verse = new String[partLength / 2];
		for (int i = 0; i < partLength / 2; i++) {
			verse[i] = getVerse(textTemplate.getStrophe()[i]);
		}
		return getPartsInString(verse, partLength);
	}

	/**
	 * returns a strophe-template which was not yet used in the song
	 */
	private TextTemplate getUnusedStrophe(int partLength, Genre genre, MoodType mood) {
		if (unusedTextTemplateList.isEmpty()) unusedTextTemplateList = templateImporter.getTemplates(genre);
		TextTemplate textTemplate = unusedTextTemplateList.get(ran.nextInt(unusedTextTemplateList.size()));
		for (int i = 0; i < 100; i++) {
			textTemplate = getRandomNotUsedValue(genre, mood);// get random template

			if (textTemplate.getLength() == partLength) {
				return textTemplate;
			}
		}
		return textTemplate;
	}

	/**
	 * returns a random unused textTemplate
	 */
	private TextTemplate getRandomNotUsedValue(Genre genre, MoodType mood) {
		if (unusedTextTemplateList.isEmpty()) {
			unusedTextTemplateList = templateImporter.getTemplates(genre);
		}
		List<TextTemplate> filteredUnusedTextTemplates = unusedTextTemplateList.stream().filter(template -> {
			for (String s : template.getMoods()) {
				if (s.toUpperCase().startsWith(mood.toString())) return true;
			}
			return false;
		}).toList();

		TextTemplate pTemplate;
		if (filteredUnusedTextTemplates.isEmpty()) pTemplate = unusedTextTemplateList.get(ran.nextInt(unusedTextTemplateList.size()));
		else pTemplate = filteredUnusedTextTemplates.get(ran.nextInt(filteredUnusedTextTemplates.size()));
		unusedTextTemplateList.remove(pTemplate);
		return pTemplate;

	}

	/**
	 * returns a variable-free-verse based on the A verse from the template
	 */
	private String getVerse(String rawString) {
		int beginning = rawString.indexOf('$');
		int end = rawString.indexOf('$', beginning + 1);

		// if there's no variable Word in the verse
		if (end < 0) return rawString;

		String requirementsVariableString = rawString.substring(beginning + 1, end);
		String[] strArr = getStringArrFromRequirementsVariableString(requirementsVariableString);
		return (getVerse(rawString.substring(0, beginning)) + getTerm(strArr) + getVerse(rawString.substring(end + 1)));
	}

	/**
	 * returns an unused word based on the given requirements
	 */
	private String getTerm(String[] requirements) {
		int id = getIdFromRequirements(requirements);
		// if id was already used
		if (usedWords.containsKey(id)) return usedWords.get(id);

		List<? extends Term<?>> termList;

		if (isNoun(requirements)) {
			termList = getNounsTermListFromRequirements(requirements);
		} else {
			termList = getVerbsTermListFromRequirements(requirements);

		}

		// index gives the position of the noun in termList
		int termListSize = termList.size();
		int position; // default 0
		if (termListSize != 0) {
			position = getCorrectPosition(termList);
			String word = termList.get(position).forLyrics();
			usedWords.put(id, word);
			return word;
		} else {
			return "Computer";
		}
	}

	/**
	 * returns String Array based on requirementsVariableString
	 */
	private String[] getStringArrFromRequirementsVariableString(String requirementsVariableString) {
		String[] strArr = new String[] { "", "", "", "", "", "", "" }; // to have empty (not null) values in String[]
		int index = 0;
		while (requirementsVariableString.length() > 0) {
			if (requirementsVariableString.charAt(0) == ',') index++;
			else {
				strArr[index] = strArr[index] + requirementsVariableString.charAt(0); // String builder
			}
			requirementsVariableString = requirementsVariableString.substring(1);
		}
		return strArr;
	}

	/**
	 * returns all the parts which were in the verse
	 */
	private String[] getPartsInString(String[] verse, int partNumber) {
		StringBuilder allVerses = new StringBuilder();
		for (String s : verse) {
			if (s == null) {
				break;
			}
			allVerses.append(s).append("|");
		}
		String[] partText = new String[partNumber];

		for (int i = 0; i < partText.length; i++) {
			int partEnd = allVerses.indexOf("|");
			if (partEnd < 0) partEnd = allVerses.length();
			partText[i] = allVerses.substring(0, partEnd);
			allVerses = new StringBuilder(allVerses.substring(partEnd + 1));
		}
		return partText;

	}

	/**
	 * returns a Hashmap which contains the song text split into parts with the
	 * number of syllables in each part, based on the given song text
	 */
	private Map<String, List<String[][]>> getPartText(List<String> order, List<String[]> songText) {
		Map<String, List<String[][]>> partTextMap = new HashMap<>();
		for (int i = 0; i < order.size(); i++) {
			// for example the second verse
			String partName = order.get(i);
			partTextMap.computeIfAbsent(partName, k -> new ArrayList<>());
			partTextMap.get(partName).add(getPartSyllConcatenation(songText.get(i)));
		}
		return partTextMap;
	}

	/**
	 * returns an array for each strophe which contains each part and the number of
	 * syllables in this part
	 */
	private String[][] getPartSyllConcatenation(String[] stropheText) {
		String[][] textSyllConcatenation = new String[stropheText.length][2];

		for (int i = 0; i < stropheText.length; i++) {
			textSyllConcatenation[i][0] = stropheText[i];
			textSyllConcatenation[i][1] = Integer.toString(countSyllables(stropheText[i]));
		}

		return textSyllConcatenation;
	}

	/**
	 * returns number of syllables of a given String
	 */
	private int countSyllables(String s) {
		int sylCounter = 0;
		String[] words = s.split(" ");

		for (String word : words) {
			sylCounter += Hyphenizer.countSyllables(word);
		}
		return sylCounter;
	}

	/**
	 * print song text to console
	 */
	private void printSongtext(List<String[]> songText, List<String> order) {
		for (int j = 0; j < songText.size(); j++) {
			System.out.println(order.get(j));
			// print part-Content
			for (int i = 0; i < songText.get(j).length; i++) {
				System.out.println("Bar" + (i + 1) + ": " + songText.get(j)[i]);
			}
		}
	}

	/**
	 * returns a noun based on the requirements
	 */
	private List<NounTerm> getNounsTermListFromRequirements(String[] requirements) {
		// requirements look like [n,f,p,n,1,1,10]
		// [Noun,Gender,Plural,Case,id,syllMin,syllMax]
		GrammaticalCase grammaticalCase;
		Gender gender;
		Numerus numerus;
		int syllMin, syllMax;

		// detect Gender
		gender = switch (requirements[1]) {
			case "f" -> Gender.FEMALE;
			case "m" -> Gender.MALE;
			default -> Gender.NEUTRAL; // in case of a false Input, neutral is selected
		};

		// detect numerus
		if ("p".equals(requirements[2])) {
			numerus = Numerus.PLURAL;
		} else {
			numerus = Numerus.SINGULAR; // in case of a false Input singular is selected
		}

		grammaticalCase = switch (requirements[3]) {
			case "a" -> GrammaticalCase.ACCUSATIVE;
			case "d" -> GrammaticalCase.DATIVE;
			case "g" -> GrammaticalCase.GENITIVE;
			default -> GrammaticalCase.NOMINATIVE;
		};

		// detect min Syll
		try {
			syllMin = Integer.parseInt(requirements[5]);
		} catch (NumberFormatException ex) {
			syllMin = 0;
		}

		// detect max Syll
		try {
			syllMax = Integer.parseInt(requirements[6]);
		} catch (NumberFormatException ex) {
			syllMax = 15;
		}

		List<NounTerm> res = termCollection.query(grammaticalCase, gender, numerus, syllMin, syllMax);
		// If an empty list was returned, incrementally check for less constrained syllable-ranges
		for (int i = 1; res.isEmpty() && i < 10; i++) {
			res = termCollection.query(grammaticalCase, gender, numerus, 0, syllMax + i);
		}

		return res;
	}

	/**
	 * returns a verb based on the requirements
	 */
	private List<VerbTerm> getVerbsTermListFromRequirements(String[] requirements) {
		int syllMin, syllMax;
		// detect min Syll
		try {
			syllMin = Integer.parseInt(requirements[2]);
		} catch (NumberFormatException ex) {
			syllMin = 0;
		}

		// detect max Syll
		try {
			syllMax = Integer.parseInt(requirements[3]);
		} catch (NumberFormatException ex) {
			syllMax = 15;
		}

		List<VerbTerm> res = termCollection.queryVerbsBySyllableRange(syllMin, syllMax);
		// If an empty list was returned, incrementally check for less constrained syllable-ranges
		for (int i = 1; res.isEmpty() && i < 10; i++) {
			res = termCollection.queryVerbsBySyllableRange(0, syllMax + i);
		}

		return res;
	}

	/**
	 * returns id specified in the requirements
	 */
	private int getIdFromRequirements(String[] requirements) {
		int position = 1;
		// for nouns
		if (isNoun(requirements)) position = 4;
		try {
			return Integer.parseInt(requirements[position]);
		} catch (NumberFormatException ex) {
			return 4; // should be a random Number but would require a Math library

		}
	}

	/**
	 * returns the position of a word not used so far
	 */
	private int getCorrectPosition(List<? extends Term<?>> termList) {
		int termListSize = termList.size();

		for (int i = 0; i < termListSize; i++) {
			// word was not used yet
			if (!usedWords.containsValue(termList.get(i).getWord())) {
				return i;
			}
		}
		return ran.nextInt(termListSize);
	}

	/**
	 * checks whether searched word is a noun
	 */
	private boolean isNoun(String[] requirements) {
		return (requirements[0].equals("n"));
	}
}