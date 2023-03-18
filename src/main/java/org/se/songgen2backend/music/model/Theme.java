package org.se.songgen2backend.music.model;

import org.se.songgen2backend.music.logic.playables.MidiPlayable;

import java.util.*;

/**
 * Class providing a theme.
 * A theme consists of notes selected from the notes of the chord of each quarter-note.
 * Can be pass to the ThemeVariation to create a variation over the theme.
 *
 * @author Benjamin Frahm
 * @reviewer Malte Richert
 */

public class Theme extends MidiPlayable {

	private final MusicalKey key;
	private Chord[][] chords;
	private Chord[] inflatedChords;
	Map<Integer, List<Integer[]>> transposedContent;
	private final int length;
	private final Random ran = new Random();

	public Theme(MusicalKey key, List<List<String>> chordProgression, int length) {
		super(0, 0);
		this.key = key;
		this.length = length;
		parseChordString(chordProgression);
		inflateChordList();
		setContent();
		setTransposedContent();

	}

	public void setContent() {
		Map<Integer, List<Integer[]>> content = new HashMap<>();
		for (int i = 0; i < 8 * length; i++) {
			if (ran.nextInt(8) == 0 || (i % 2 == 0 && ran.nextInt(3) != 0)) {
				Integer chordNote = inflatedChords[i / 2].getChord().get(ran.nextInt(inflatedChords[i / 2].getChord().size()));
				Integer[] posAndLength = new Integer[] { i * 12, 12 + 12 * ran.nextInt(3) };
				if (content.containsKey(chordNote)) {
					content.get(chordNote).add(posAndLength);
				} else {
					List<Integer[]> l = new ArrayList<>();
					l.add(posAndLength);
					content.put(chordNote, l);
				}
			}
		}
		super.setContent(content);
	}

	public void setTransposedContent() {
		transposedContent = new HashMap<>();
		for (int note : getContent().keySet()) {
			for (Integer[] posAndLength : getContent().get(note)) {
				Integer[] noteAndLength = new Integer[] { note, posAndLength[1] };

				if (transposedContent.containsKey(posAndLength[0])) {
					transposedContent.get(posAndLength[0]).add(noteAndLength);
				} else {
					List<Integer[]> tmp = new ArrayList<>();
					tmp.add(noteAndLength);
					transposedContent.put(posAndLength[0], tmp);
				}
			}
		}
	}

	public void parseChordString(List<List<String>> chordProgression) {
		chords = new Chord[chordProgression.size()][];
		for (int bar = 0; bar < chordProgression.size(); bar++) {
			chords[bar] = new Chord[chordProgression.get(bar).size()];
			for (int i = 0; i < chordProgression.get(bar).size(); i++) {
				int stair = Integer.parseInt(String.valueOf(chordProgression.get(bar).get(i).charAt(0)));
				String modifier = chordProgression.get(bar).get(i).substring(1);
				chords[bar][i] = new Chord(MusicalKey.getNotesInKey(key.getBaseNote())[stair], modifier);
			}
		}
	}

	private void inflateChordList() {
		inflatedChords = new Chord[chords.length * length];
		for (int barNo = 0; barNo < chords.length; barNo++) {
			for (int chordNo = 0; chordNo < chords[barNo].length; chordNo++) {
				for (int i = 0; i < 4 / chords[barNo].length; i++) {
					inflatedChords[4 * barNo + 4 / chords[barNo].length * chordNo + i] = chords[barNo][chordNo];
				}
			}
		}
	}

	public MusicalKey getKey() {
		return key;
	}

	public int getLengthInBars() {
		return length;
	}
}
