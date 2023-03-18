package org.se.songgen2backend.music.logic.playables;

import org.se.songgen2backend.music.model.Chord;
import org.se.songgen2backend.music.model.MusicalKey;

import java.util.*;

/**
 * Midi-playable model of a bass line.
 * Bass lines are generated as eights, repeating the current chord's root or playing transition notes before changing chords.
 * 
 * @author Malte Richert
 * @reviewer Benjamin Frahm
 */

public class BassContainer extends PitchedPlayable {
	private final MusicalKey key;
	private final Chord[] nextChords;

	public BassContainer(int trackNo, int bar, MusicalKey key, List<String> currentChords, List<String> nextChords) {
		super(trackNo, bar, key, currentChords);
		this.key = key;
		this.nextChords = parseChordString(nextChords);
		setContent();
	}

	/**
	 * Generates a bass line over given chords and key. <br>
	 * It plays the root of the current chord on every eighth by default. <br>
	 * Transition notes are played for the number of eighths equal to steps between current chord and next chord. <br>
	 * If the next chord is one step away or equal to the current chord no transition is played. <br>
	 * If the next chord is two steps away, the step between both is played as transition on the eighth before chord switch. <br>
	 * This only goes up to three steps (-> two transition notes) because a distance of +4 is treated as -3 etc. <br>
	 * The bass line is constrained to one octave (36-47) what can lead to rather big jumps across octave bounds.
	 */
	private void setContent() {
		for (int count = 0; count < 8; count++) {
			int indexInChord = count % (8 / chords.length);
			int chordIndex = count / (8 / chords.length);
			int rootNote = chords[chordIndex].getRootNote() % 12; // gets root of chord and translates it to first octave

			// get number of eighths each chord is played
			int chordEights = 8 / chords.length;

			Chord nextChord;
			if (chordIndex + 1 < chords.length) {
				nextChord = chords[chordIndex + 1];
			} else {
				nextChord = nextChords[0];
			}

			int[] scale = MusicalKey.getNotesInKey(key.getBaseNote());
			Map<Integer, Integer> descaler = new HashMap<>();
			for (int index = 0; index < scale.length; index++) {
				descaler.put(scale[index] % 12, index);
			}
			// get distance to next chord's root
			int distance = (descaler.get(nextChord.getRootNote() % 12) - descaler.get(rootNote)) % 7;
			if (distance > 3) {
				distance -= 7;
			} else if (distance < -3) {
				distance += 7;
			}

			// play rootNote for first chordEights-distance+1 eights
			if (chordEights > indexInChord + (Math.abs(distance) - 1)) {
				addNoteToContent(count, rootNote);
			}
			// play transition steps
			else if (distance == 2 || (distance == 3 && chordEights - indexInChord == 2)) {
				addNoteToContent(count, scale[(descaler.get(rootNote) + 1) % 7]);
			}
			else if (distance == -2 || (distance == -3 && chordEights - indexInChord == 2)) {
				int scalar = (descaler.get(rootNote) - 1) % 7;
				addNoteToContent(count, scale[scalar >= 0 ? scalar : scalar + 7]);
			}
			else if (distance == 3) {
				addNoteToContent(count, scale[(descaler.get(rootNote) + 2) % 7]);
			}
			else if (distance == -3) {
				int scalar = (descaler.get(rootNote) - 2) % 7;
				addNoteToContent(count, scale[scalar >= 0 ? scalar : scalar + 7]);
			}
		}
	}

	/**
	 * @param count
	 *            current eighth between 0 and 7 (inclusive)
	 * @param pitch
	 *            pitch of note to be added, octave is ignored
	 */
	private void addNoteToContent(int count, int pitch) {
		pitch = pitch % 12 + 36;
		Integer[] posAndLen = new Integer[] { count * 12, 12 };
		if (super.getContent().containsKey(pitch)) {
			super.getContent().get(pitch).add(posAndLen);
		} else {
			List<Integer[]> posAndLenList = new ArrayList<>();
			posAndLenList.add(posAndLen);
			super.getContent().put(pitch, posAndLenList);
		}
	}
}
