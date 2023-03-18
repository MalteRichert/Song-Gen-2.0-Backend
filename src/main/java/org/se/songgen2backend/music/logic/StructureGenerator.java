package org.se.songgen2backend.music.logic;

import org.se.songgen2backend.Settings;
import org.se.songgen2backend.music.Config;
import org.se.songgen2backend.music.logic.playables.ChordContainer;
import org.se.songgen2backend.music.logic.playables.MidiPlayable;
import org.se.songgen2backend.music.model.*;
import org.se.songgen2backend.text.analysis.TermCollection;
import org.se.songgen2backend.text.generation.SongTextGenerator;
import org.se.songgen2backend.text.metric.MoodType;

import java.util.*;

/**
 * Generates a full song based on the provided inputs
 *
 * @author Malte Richert
 * @author Benjamin Frahm
 */
public class StructureGenerator {
	private static Structure structure;
	private static final Map<Integer, Integer> trackMapping = new HashMap<>();

	public static MidiSequence generateStructure(Settings settings){
		return generateStructure(settings, null, null);
	}
	public static MidiSequence generateStructure(Settings settings, TermCollection terms, MoodType mood) {
		Random ran = new Random();
		trackMapping.clear();
		structure = Config.getStructures().get(ran.nextInt(Config.getStructures().size()));
		structure.setGenre(settings.getGenre());
		structure.setKey(new MusicalKey());
		structure.setTempo(settings.getTempo());

		Map<String, List<String[][]>> songText = null;
		if (settings.getTextModeFlag()) {
			SongTextGenerator textGenerator = new SongTextGenerator();
			songText = textGenerator.generateSongText(structure, terms, mood);
		}

		MidiSequence seq = initMidiSequence(structure);

		structure.getPart(structure.getBasePartKey()).fillRandomly(
				structure.getKey(), trackMapping, structure.getGenre() == Genre.BLUES ? 12 : 4,
				(settings.getTextModeFlag()) ? songText.get(structure.getBasePartKey()).get(0) : null
		);

		for (String partName : structure.getParts().keySet()) {
			if (partName.equals(structure.getBasePartKey())) {
				continue;
			}
			Part part = structure.getPart(partName);
			List<List<String>> progression;
			if (part.getRandomizationLevel() == 0) {
				progression = structure.getPart(structure.getBasePartKey()).getChords();
			} else if (part.getRandomizationLevel() == 1) {
				List<String> reqChords = getImportantChords(structure.getPart(structure.getBasePartKey()).getChords());
				List<List<List<String>>> matchingProgressions = ChordContainer.getMatchingProgressions(reqChords);
				progression = matchingProgressions.get(ran.nextInt(matchingProgressions.size()));
			} else {
				progression = Config.getChordProgressions().get(ran.nextInt(Config.getChordProgressions().size()));
			}
			part.fillPart(progression, structure.getKey(), trackMapping, structure.getGenre() == Genre.BLUES ? 12 : 4,
					(settings.getTextModeFlag()) ? songText.get(partName).get(0) : null
			);
		}

		seq.setEnd(calculateLength());
		int barOffset = 0;
		for (String partName : structure.getOrder()) {
			for (MidiPlayable m : structure.getPart(partName).getMidiPlayables()) {
				m.setBar(m.getBar() + barOffset);
				seq.addMidiPlayable(m);
				m.setBar(m.getBar() - barOffset);
			}
			seq.addText(barOffset * 4, 0, partName);


			if (settings.getTextModeFlag() && partContainsVocal(structure.getPart(partName))) {
				assert songText != null;
				Part p = structure.getPart(partName);

				MidiText t;
				for (int bar = 0; bar < p.getLength(); bar++) {
					t = new MidiText(trackMapping.get(Config.getInstrumentMapping().get("vocals")), bar + barOffset,
							songText.get(partName).get(0)[bar][0]);
					seq.addMidiText(t);
				}
				if (!partName.contains("horus")) {
					songText.get(partName).remove(0);
				}
			}

			for (MidiText t : structure.getPart(partName).getMidiTexts()) {
				t.setBar(t.getBar() + barOffset);
				seq.addMidiText(t);
				t.setBar(t.getBar() - barOffset);
			}
			barOffset += structure.getPart(partName).getLength();
		}
		return seq;
	}

	public static MidiSequence initMidiSequence(Structure s) {
		int currentTrackNo = 0;
		System.out.println("starting to loop through parts: ");
		for (Part part : s.getParts().values()) {
			System.out.println("part: " + part);
			for (InstrumentEnum instrument : part.getReqInsts()) {
				currentTrackNo = putTrackNo(currentTrackNo, instrument);
			}
			for (InstrumentEnum instrument : part.getOptInsts()) {
				currentTrackNo = putTrackNo(currentTrackNo, instrument);
			}
		}

		MidiSequence seq = new MidiSequence(currentTrackNo);
		List<Integer> drumInstrs = getDrumInstrNo();
		for (Map.Entry<Integer, Integer> instr : trackMapping.entrySet()) {
			if (drumInstrs.contains(instr.getKey())) {
				seq.setInstrument(instr.getKey(), instr.getValue(), true);
			} else {
				seq.setInstrument(instr.getKey(), instr.getValue());
			}
			seq.setKey(structure.getKey().getBase(), instr.getValue());
		}
		seq.setBPM(structure.getTempo());
		return seq;
	}

	private static int putTrackNo(int currentTrackNo, InstrumentEnum instrument) {
		if (!trackMapping.containsKey(Config.getInstrumentMapping().get(instrument.toString()))) {
			trackMapping.put(Config.getInstrumentMapping().get(instrument.toString()), currentTrackNo);
			if (instrument.equals(InstrumentEnum.chords) || instrument.equals(InstrumentEnum.chords2)) {
				currentTrackNo++;
			}
			currentTrackNo++;
		}
		return currentTrackNo;
	}

	private static int calculateLength() {
		int length = 0;
		for (String i : structure.getOrder()) {
			length += structure.getParts().get(i).getLength();
		}
		return length;
	}

	/**
	 * Returns most important chords by ordering: 0,4,3,2,1,5,6
	 *
	 * @param chords
	 *            any list of chords in chord_progression template format
	 * @return list of two most important of the input chords
	 */
	private static List<String> getImportantChords(List<List<String>> chords) {
		Map<String, Integer> chordImportanceMap = Map.of("0", 0, "4", 1, "3", 2, "2", 3, "1", 4, "5", 5, "6", 6);
		List<String> importantChords = new ArrayList<>();
		for (List<String> bar : chords) {
			for (String chord : bar) {
				if (importantChords.contains(chord)) {
					continue;
				}
				if (importantChords.isEmpty()) {
					importantChords.add(chord);
				} else if (chordImportanceMap.get(chord.substring(0, 1)) < chordImportanceMap.get(importantChords.get(0).substring(0, 1))) {
					importantChords.add(0, chord);
					importantChords.remove(importantChords.size() - 1);
				} else if (importantChords.size() < 2
						|| chordImportanceMap.get(chord.substring(0, 1)) < chordImportanceMap.get(importantChords.get(1).substring(0, 1))) {
							if (importantChords.size() > 1) {
								importantChords.remove(1);
							}
							importantChords.add(1, chord);

						}
			}
		}
		return importantChords;
	}

	public static List<Integer> getDrumInstrNo() {
		List<Integer> d = new ArrayList<>();
		for (InstrumentEnum instr : InstrumentEnum.values()) {
			if (instr.toString().startsWith("drum")) {
				d.add(Config.getInstrumentMapping().get(instr.toString()));
			}
		}
		return d;
	}

	public static boolean partContainsVocal(Part part) {
		for (InstrumentEnum instr : part.getReqInsts()) {
			if (instr.toString().startsWith("vocals")) {
				return true;
			}
		}
		return false;
	}
}
