package org.se.songgen2backend.music.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.se.songgen2backend.music.logic.playables.BassContainer;
import org.se.songgen2backend.music.logic.playables.BeatContainer;
import org.se.songgen2backend.music.logic.playables.ChordContainer;
import org.se.songgen2backend.music.Config;
import org.se.songgen2backend.music.logic.playables.MidiPlayable;
import org.se.songgen2backend.music.logic.playables.PitchedPlayable;
import java.util.*;

/**
 * Model of a part in a music piece.
 * Consists of a length, required instruments, optional instruments
 * and methods to fill the part with MidiPlayables according to the
 * specifications provided
 *
 * @author Malte Richert
 * @author Benjamin Frahm
 */

public class Part {
	@JsonProperty
	private final int length;
	@JsonProperty
	private final List<InstrumentEnum> reqInsts;
	@JsonProperty
	private final List<InstrumentEnum> optInsts;
	@JsonProperty
	private final int randomizationLevel;
	private List<List<String>> chordProgression;
	private final Random ran = new Random();
	private final List<MidiPlayable> midiPlayables = new ArrayList<>();
	private final List<MidiText> midiTexts = new ArrayList<>();

	@JsonCreator
	public Part(@JsonProperty("length") int length, @JsonProperty("req") List<InstrumentEnum> reqInsts,
			@JsonProperty(value = "opt", defaultValue = "[]") List<InstrumentEnum> optInsts,
			@JsonProperty(value = "randomizationLevel", defaultValue = "0") int randomizationLevel) {
		this.length = length;
		this.reqInsts = reqInsts;
		this.optInsts = optInsts;
		this.randomizationLevel = randomizationLevel;
	}

	/**
	 * method for filling a part based on a given chord progression
	 *
	 * @param chordProgression
	 *            the chord progression that is meant to be used
	 * @param key
	 *            the key of the part
	 * @param trackMapping
	 *            the instrument-track-mapping of the sequence
	 */
	public void fillPart(List<List<String>> chordProgression, MusicalKey key, Map<Integer, Integer> trackMapping, int themeLength, String[][] text) {
		this.chordProgression = chordProgression;
		fillPart(key, trackMapping, themeLength, text);
	}

	private void fillPart(MusicalKey key, Map<Integer, Integer> trackMapping, int themeLength, String[][] text) {
		int beatNo = ran.nextInt(BeatContainer.getDrumBeats().size());
		Theme theme = new Theme(key, chordProgression, themeLength);
		MidiPlayable m;
		PitchedPlayable p;
		MidiText t;
		for (InstrumentEnum instr : optInsts) {
			if (ran.nextInt(3) == 0) {
				reqInsts.add(instr);
			}
		}
		for (int bar = 0; bar < length; bar++) {
			for (InstrumentEnum instr : reqInsts) {
				if (instrEnumBeginsWith(instr, "chords")) {
					p = new ChordContainer(trackMapping.get(Config.getInstrumentMapping().get(instr.toString())), bar, key,
							chordProgression.get(bar % chordProgression.size()));
					midiPlayables.add(p);
					for (int chordNo = 0; chordNo < p.getInflatedChords().length; chordNo++) {
						if (chordNo == 0 || p.getInflatedChords()[chordNo] != p.getInflatedChords()[chordNo - 1]) {
							t = new MidiText(trackMapping.get(Config.getInstrumentMapping().get(instr.toString())), bar,
									p.getInflatedChords()[chordNo], chordNo);
							midiTexts.add(t);
						}
					}

					m = new ChordContainer(trackMapping.get(Config.getInstrumentMapping().get(instr.toString())) + 1, bar, key,
							chordProgression.get(bar % chordProgression.size()), true);
					midiPlayables.add(m);
				} else if (instrEnumBeginsWith(instr, "drum")) {
					int fill;
					if (bar == length - 1) {
						fill = 1;
					} else if (bar % 8 == 7) {
						fill = ran.nextInt(3) == 0 ? 0 : 1;
					} else if (bar % 4 == 3) {
						fill = ran.nextInt(2) - 1;
					} else {
						fill = -1;
					}
					m = new BeatContainer(beatNo, bar, fill, trackMapping.get(Config.getInstrumentMapping().get(instr.toString())));
					midiPlayables.add(m);
				} else if (instrEnumBeginsWith(instr, "bass")) {
					m = new BassContainer(trackMapping.get(Config.getInstrumentMapping().get(instr.toString())), bar, key,
							chordProgression.get(bar % chordProgression.size()), chordProgression.get((bar + 1) % chordProgression.size()));
					midiPlayables.add(m);
				} else if (bar % theme.getLengthInBars() == 0) {
					if (instr.toString().equals("melody2")) {
						m = new ThemeVariation(theme, trackMapping.get(Config.getInstrumentMapping().get(instr.toString())), bar, false);
					} else {
						System.out.println("bar: " + bar);
						System.out.println("themeLength: " + theme.getLengthInBars());
						m = new ThemeVariation(
								theme, trackMapping.get(Config.getInstrumentMapping().get(instr.toString())),
								bar,
								(text != null) ? Arrays.copyOfRange(text, bar, bar + theme.getLengthInBars()) : null
						);
					}
					midiPlayables.add(m);
				}
			}
		}
	}

	/**
	 * Method to fill a new part with a random chord progression
	 *
	 * @param key
	 *            - the key of the part
	 * @param trackMapping
	 *            - the Instrument-track-mapping of the sequence
	 */
	public void fillRandomly(MusicalKey key, Map<Integer, Integer> trackMapping, int themeLength, String[][] text) {
		chordProgression = Config.getChordProgressions().get(ran.nextInt(Config.getChordProgressions().size()));
		fillPart(key, trackMapping, themeLength, text);
	}

	@Override
	public String toString() {
		return "Part{" + "length=" + length + ", reqInsts=" + reqInsts + ", optInsts=" + optInsts + ", randomizationLevel=" + randomizationLevel
				+ ", chords=" + chordProgression + '}';
	}

	private boolean instrEnumBeginsWith(InstrumentEnum instr, String startPhrase) {
		return instr.toString().startsWith(startPhrase);
	}

	public int getLength() {
		return length;
	}

	public List<MidiPlayable> getMidiPlayables() {
		return midiPlayables;
	}

	public List<InstrumentEnum> getReqInsts() {
		return reqInsts;
	}

	public List<InstrumentEnum> getOptInsts() {
		return optInsts;
	}

	public int getRandomizationLevel() {
		return randomizationLevel;
	}

	public List<List<String>> getChords() {
		return chordProgression;
	}

	public List<MidiText> getMidiTexts() {
		return midiTexts;
	}
}
