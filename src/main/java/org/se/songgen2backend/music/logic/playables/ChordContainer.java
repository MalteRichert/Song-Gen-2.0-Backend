package org.se.songgen2backend.music.logic.playables;

import org.se.songgen2backend.music.Config;
import org.se.songgen2backend.music.model.MusicalKey;

import java.util.*;

/**
 * Midi-playable model of a Chord. If isBassTrack is not set,
 * the given chords will be distributed equally among the 4 quarters of the bar
 * If isBassTrack is set, the root-note of the given chords will be distributed equally among the 4 quarters
 *
 * @author Benjamin Frahm
 * @reviewer Malte Richert
 */
public class ChordContainer extends PitchedPlayable {
	private final boolean isBassTrack;

	public ChordContainer(int trackNo, int bar, MusicalKey key, List<String> chords, boolean isBassTrack) {
		super(trackNo, bar, key, chords);
		this.isBassTrack = isBassTrack;
		setContent();
	}

	public ChordContainer(int trackNo, int bar, MusicalKey key, List<String> chords) {
		this(trackNo, bar, key, chords, false);
	}

	public static List<List<List<String>>> getMatchingProgressions(List<String> reqChords) {
		List<List<List<String>>> returnProgressions = new ArrayList<>();
		boolean flag;
		for (List<List<String>> progression : Config.getChordProgressions()) {
			flag = true;
			List<String> l = progression.stream().flatMap(Collection::stream).toList();
			for (String chord : reqChords) {
				if (!l.contains(chord)) {
					flag = false;
					break;
				}
			}
			if (flag) {
				returnProgressions.add(progression);
			}
		}
		return returnProgressions;
	}

	private void setContent() {
		Map<Integer, List<Integer[]>> content = new HashMap<>();
		inflateChordList();
		for (int count = 0; count < 4; count++) {
			List<Integer> singleChord = inflatedChords[count].getChord();
			if (isBassTrack) {
				singleChord = List.of(inflatedChords[count].getRootNote() - 24);
			}
			for (Integer note : singleChord) {
				Integer[] posAndLen;
				if (isBassTrack && count == 3) {
					posAndLen = new Integer[] { count * 24 + 12, 12 };
				} else {
					posAndLen = new Integer[] { count * 24, 24 };
				}
				if (content.containsKey(note)) {
					content.get(note).add(posAndLen);
				} else {
					List<Integer[]> posAndLenList = new ArrayList<>();
					posAndLenList.add(posAndLen);
					content.put(note, posAndLenList);
				}
			}
		}
		super.setContent(content);
	}
}
