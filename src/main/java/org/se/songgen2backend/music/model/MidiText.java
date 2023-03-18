package org.se.songgen2backend.music.model;

/**
 * This class can be passed to an instance of the MidiSequence-class,
 * the text will then be displayed in the midi at the specified locations
 * 
 * @author Benjamin Frahm
 * @reviewer Malte Richert
 */
public class MidiText {
	private int bar;
	private final int relativeOffset, trackNo;
	private final String text;
	private final boolean isChordName;

	public MidiText(int trackNo, int bar, String text) {
		this.bar = bar;
		this.text = text;
		this.relativeOffset = 0;
		this.trackNo = trackNo;
		this.isChordName = false;
	}

	/**
	 *
	 * @param trackNo
	 *            - the track number under which the text is supposed to appear
	 * @param bar
	 *            - the bar in which the text is meant to appear
	 * @param chord
	 *            - the chord whose name is meant to appear
	 * @param relativeOffset
	 *            - the offset inside the bar in quarter notes from the beginning
	 */
	public MidiText(int trackNo, int bar, Chord chord, int relativeOffset) {
		this.bar = bar;
		this.isChordName = true;
		this.relativeOffset = relativeOffset;
		this.trackNo = trackNo;
		this.text = chord.getNoteName();
	}

	public int getPos() {
		return bar * 4 + (isChordName ? relativeOffset : 1);
	}

	public String getText() {
		return text;
	}

	public int getBar() {
		return bar;
	}

	public void setBar(int bar) {
		this.bar = bar;
	}

	public int getTrackNo() {
		return trackNo;
	}
}
