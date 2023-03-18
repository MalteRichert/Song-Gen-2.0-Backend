package org.se.songgen2backend.music.logic.playables;

import java.util.*;

/**
 * <p>
 * This is the base class that is used to construct objects,
 * which can be interpreted and translated to actual midi by the MidiSequence-class
 * </p>
 *
 * @author Benjamin Frahm
 * @reviewer Malte Richert
 */

public abstract class MidiPlayable {
	private final int trackNo;
	private int bar;
	private Map<Integer, List<Integer[]>> content = new HashMap<>();

	protected MidiPlayable(int trackNo, int bar) {
		this.bar = bar;
		this.trackNo = trackNo;
	}

	public void setContent(Map<Integer, List<Integer[]>> content) {
		this.content = content;
	}

	public Map<Integer, List<Integer[]>> getContent() {
		return content;
	}

	public int getTrackNo() {
		return trackNo;
	}

	public int getBar() {
		return bar;
	}

	public void setBar(int bar) {
		this.bar = bar;
	}

	public Map<Integer, List<Integer[]>> deepCopy() {
		Map<Integer, List<Integer[]>> deepCopy = new HashMap<>();
		for (Map.Entry<Integer, List<Integer[]>> note : content.entrySet()) {
			List<Integer[]> k = new ArrayList<>();
			for (Integer[] innerList : note.getValue()) {
				k.add(innerList.clone());
			}
			deepCopy.put(note.getKey(), k);
		}
		return deepCopy;
	}
}
