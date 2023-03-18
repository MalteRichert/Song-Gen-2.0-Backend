package org.se.songgen2backend.music.model;

import org.se.songgen2backend.music.logic.playables.MidiPlayable;
import java.util.*;

/**
 * Provided an instance of the Theme class, this class can be used to
 * create a MidiPlayable modeling a variation of the theme.
 *
 * @author Benjamin Frahm
 * @reviewer Malte Richert
 */

public class ThemeVariation extends MidiPlayable {

	final Theme theme;
	Map<Integer, List<List<Integer>>> transposedContent;
	String[][] text;
	static final Random ran = new Random();

	public ThemeVariation(Theme theme, int trackNo, int bar, String[][] text) {
		super(trackNo, bar);
		this.theme = theme;
		this.text = text;
		setContent(theme.deepCopy());
		createVariation();
	}

	public ThemeVariation(Theme theme, int trackNo, int bar, boolean variationFlag) {
		super(trackNo, bar);
		this.theme = theme;
		setContent(theme.deepCopy());
	}

	private void createVariation() {
		Map<Integer, List<Integer[]>> themeContent = theme.transposedContent;
		int pos, posNextSmaller, posNextBigger, newNote, newLength, loopCounter;
		Integer[] posAndLength;
		for (int bar = 0; bar < theme.getLengthInBars(); bar++) {
			loopCounter = 0;
			while (getNoteCountInBar(bar) < ((text != null && text[bar] != null)? Integer.parseInt(text[bar][1]) : ran.nextInt(2,14) ) && loopCounter < 50) {
				loopCounter++;
				pos = ran.nextInt(16) * 6 + bar * 96;
				posNextSmaller = getPosNext(pos, false);
				posNextBigger = getPosNext(pos, true);
				if (themeContent.containsKey(posNextBigger) && themeContent.containsKey(posNextSmaller)) {
					posAndLength = new Integer[2];
					if (posNextBigger == pos) {
						continue;
					}
					if (posNextBigger == pos + 6) {
						int[] gamut = MusicalKey.getCloseNotesInKey(theme.getKey().getBaseNote(), themeContent.get(pos + 6).get(0)[0]);
						newNote = gamut[(MusicalKey.findIndexOfNoteInScale(gamut, themeContent.get(pos + 6).get(0)[0]) + (7 - ran.nextInt(2))) % 7];
						newLength = 6;
						posAndLength[0] = pos;
						posAndLength[1] = newLength;

						addPosAndLengthToContent(newNote, posAndLength);
					}

					else if (posNextBigger - posNextSmaller >= 48
							&& themeContent.get(posNextBigger).get(0)[0].equals(themeContent.get(posNextSmaller).get(0)[0]) && ran.nextInt(2) == 0) {
								int[] gamut = MusicalKey.getCloseNotesInKey(theme.getKey().getBaseNote(), themeContent.get(posNextBigger).get(0)[0]);
								int index = MusicalKey.findIndexOfNoteInScale(gamut, themeContent.get(posNextBigger).get(0)[0]);
								if (index >= 4) {
									newNote = gamut[index - 2];
									newLength = 12;
									posAndLength = new Integer[] { posNextBigger - 24, newLength };
									addPosAndLengthToContent(newNote, posAndLength);
									newNote = gamut[index - 1];
								} else {
									newNote = gamut[index + 2];
									newLength = 12;
									posAndLength = new Integer[] { posNextBigger - 24, newLength };
									addPosAndLengthToContent(newNote, posAndLength);
									newNote = gamut[index + 1];
								}
								posAndLength[0] = posNextBigger - 36;
								addPosAndLengthToContent(newNote, posAndLength);
								posAndLength[0] = posNextBigger - 12;
								addPosAndLengthToContent(newNote, posAndLength);
							} else {
								int[] gamut = MusicalKey.getCloseNotesInKey(theme.getKey().getBaseNote(), themeContent.get(posNextBigger).get(0)[0]);

								int index1 = MusicalKey.findIndexOfNoteInScale(gamut, themeContent.get(posNextBigger).get(0)[0]);
								int index2 = MusicalKey.findIndexOfNoteInScale(gamut, themeContent.get(posNextSmaller).get(0)[0]);
								int newIndexInGamut = (index1 + index2) / 2;
								newLength = posNextBigger - pos;
								posAndLength = new Integer[] { pos, newLength };
								newNote = gamut[newIndexInGamut];

								addPosAndLengthToContent(newNote, posAndLength);
							}
				}
			}
		}
	}

	private void addPosAndLengthToContent(int newNote, Integer[] posAndLength) {
		Integer[] posAndLengthCopy = posAndLength.clone();
		if (getContent().containsKey(newNote)) {
			getContent().get(newNote).add(posAndLengthCopy);
		} else {
			List<Integer[]> posAndLengthList = new ArrayList<>();
			posAndLengthList.add(posAndLengthCopy);
			getContent().put(newNote, posAndLengthList);
		}
	}

	public int getPosNext(int posToFind, boolean bigger) {
		int foundPos = bigger ? theme.getLengthInBars() * 96 : 0;
		for (int pos : theme.transposedContent.keySet()) {
			if ((!bigger && pos < posToFind && foundPos < pos) || (bigger && pos >= posToFind && foundPos > pos)) {
				foundPos = pos;
			}
		}
		return foundPos;
	}

	public int getNoteCountInBar(int bar) {
		int count = 0;

		setTransposedContent();
		for (Map.Entry<Integer, List<List<Integer>>> entry : transposedContent.entrySet()) {
			if (bar * 96 <= entry.getKey() && entry.getKey() <= (bar + 1) * 96) {
				count += entry.getValue().size();
			}
		}
		return count;
	}

	public void setTransposedContent() {
		transposedContent = new HashMap<>();
		for (int note : getContent().keySet()) {
			for (Integer[] posAndLength : getContent().get(note)) {
				List<Integer> noteAndLength = new ArrayList<>();
				noteAndLength.add(note);
				noteAndLength.add(posAndLength[1]);
				if (transposedContent.containsKey(posAndLength[0])) {
					transposedContent.get(posAndLength[0]).add(noteAndLength);
				} else {
					List<List<Integer>> tmp = new ArrayList<>();
					tmp.add(noteAndLength);
					transposedContent.put(posAndLength[0], tmp);
				}
			}
		}
	}
}
