package org.se.songgen2backend.music.logic.playables;

import org.se.songgen2backend.music.model.Beat;

import java.util.*;

/**
 * Midi-playable model of a drum beat. The templates are taken from the drumBeats-list as Beat-Objects
 *
 * @author Benjamin Frahm
 * @reviewer Malte Richert
 */

public class BeatContainer extends MidiPlayable {
	protected static Map<String, Integer> drumPrograms;
	protected static List<Beat> drumBeats;

	public BeatContainer(int beatNo, int bar, int fill, int trackNo) {
		super(trackNo, bar); // 118 is drumset instrument
		Beat beat = drumBeats.get(beatNo);
		Map<String, List<Integer[]>> beatShape;
		if (fill == 0) {
			beatShape = beat.getSmallFill();
		} else if (fill == 1) {
			beatShape = beat.getBigFill();
		} else {
			beatShape = beat.getMainPattern();
		}
		Map<Integer, List<Integer[]>> c = new HashMap<>();
		for (Map.Entry<String, List<Integer[]>> instr : beatShape.entrySet()) {
			c.put(drumPrograms.get(instr.getKey()), instr.getValue());
		}
		setContent(c);
	}

	public static void setDrumPrograms(Map<String, Integer> drumPrograms) {
		BeatContainer.drumPrograms = drumPrograms;
	}

	public static List<Beat> getDrumBeats() {
		return drumBeats;
	}

	public static void setDrumBeats(List<Beat> drumBeats) {
		BeatContainer.drumBeats = drumBeats;
	}
}
