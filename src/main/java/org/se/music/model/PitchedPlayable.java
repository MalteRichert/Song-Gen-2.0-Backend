package org.se.music.model;

import java.util.List;

/**
 * Base-class for pitched instruments providing methods
 * to work with chords, inflate chords to fit length, etc.
 * @author Benjamin Frahm
 * @reviewer Malte Richert
 */
abstract public class PitchedPlayable extends MidiPlayable {

	protected Chord[] chords;
	protected Chord[] inflatedChords;
	protected MusicalKey key;

	public PitchedPlayable(int trackNo, int bar, MusicalKey key, List<String> chord) {
		super(trackNo, bar);
		this.key = key;
		this.chords = parseChordString(chord);
		inflateChordList();
	}

	public Chord[] parseChordString(List<String> chords) {
		Chord[] parsedChords = new Chord[chords.size()];
		for (int i = 0; i < chords.size(); i++) {
			int stair = Integer.parseInt(String.valueOf(chords.get(i).charAt(0)));
			String modifier = chords.get(i).substring(1);
			parsedChords[i] = new Chord(MusicalKey.getNotesInKey(key.getBaseNote())[stair], modifier);
		}
		return parsedChords;
	}

	protected void inflateChordList() {
		inflatedChords = new Chord[4];
		for (int chordNo = 0; chordNo < chords.length; chordNo++) {
			for (int i = 0; i < 4 / chords.length; i++) {
				inflatedChords[4 / chords.length * chordNo + i] = chords[chordNo];
			}
		}
	}

	public Chord[] getInflatedChords() {
		return inflatedChords;
	}
}
