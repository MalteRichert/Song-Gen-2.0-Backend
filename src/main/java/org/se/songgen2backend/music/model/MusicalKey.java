package org.se.songgen2backend.music.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Model of a musical key.
 * Provides methods to get the notes of the corresponding scale,
 * check if a note is in the corresponding scale
 * and translate midi note numbers to human-readable strings
 *
 * @author Benjamin Frahm
 * @reviewer Malte Richert
 */

public class MusicalKey {

	protected static final int[] keyMaj = { 0, 2, 4, 5, 7, 9, 11 };
	private final int baseNote;
	private final String base;

	public static int[] getNotesInKey(int baseNote) {
		int[] notesInKey = new int[7];
		for (int i = 0; i < 7; i++) {
			notesInKey[i] = baseNote + keyMaj[i];
		}
		return notesInKey;
	}

	public static int[] getCloseNotesInKey(int baseNote, int note) {
		int[] gamut = getNotesInKey(baseNote);
		while (gamut[0] > note) {
			for (int index = 0; index < gamut.length; index++) {
				gamut[index] -= 12;
			}
		}
		while (gamut[gamut.length - 1] < note) {
			for (int index = 0; index < gamut.length; index++) {
				gamut[index] += 12;
			}
		}
		return gamut;
	}

	public static int findIndexOfNoteInScale(int[] notesInKey, int note) {
		for (int index = 0; index < notesInKey.length; index++) {
			if (note % 12 == notesInKey[index] % 12) {
				return index;
			}
		}
		return 0;
	}

	public MusicalKey() {
		base = (String) musicalKeyMajor.keySet().toArray()[new Random().nextInt(musicalKeyMajor.keySet().size())];
		baseNote = translateNoteStringToValue(base);
	}

	public int getBaseNote() {
		return baseNote;
	}

	public String getBase() {
		return base;
	}

	public static int translateNoteStringToValue(String s) {
		return musicalStringToNoteValue.get(s.charAt(0)) + (s.length() == 1 ? 0 : (s.charAt(1) == 'b' ? -1 : 1));
	}

	public static final Map<String, Byte> musicalKeyMajor = new HashMap<>();
	static {
		musicalKeyMajor.put("Cb", (byte) -7);
		musicalKeyMajor.put("Gb", (byte) -6);
		musicalKeyMajor.put("Db", (byte) -5);
		musicalKeyMajor.put("Ab", (byte) -4);
		musicalKeyMajor.put("Eb", (byte) -3);
		musicalKeyMajor.put("Bb", (byte) -2);
		musicalKeyMajor.put("F", (byte) -1);
		musicalKeyMajor.put("C", (byte) 0);
		musicalKeyMajor.put("G", (byte) 1);
		musicalKeyMajor.put("D", (byte) 2);
		musicalKeyMajor.put("A", (byte) 3);
		musicalKeyMajor.put("E", (byte) 4);
		musicalKeyMajor.put("B", (byte) 5);
		musicalKeyMajor.put("Fs", (byte) 6);
		musicalKeyMajor.put("Cs", (byte) 7);

	}
	public static final Map<Character, Integer> musicalStringToNoteValue = new HashMap<>();
	static {
		musicalStringToNoteValue.put('C', 60);
		musicalStringToNoteValue.put('D', 62);
		musicalStringToNoteValue.put('E', 64);
		musicalStringToNoteValue.put('F', 65);
		musicalStringToNoteValue.put('G', 67);
		musicalStringToNoteValue.put('A', 69);
		musicalStringToNoteValue.put('B', 71);

	}

	@Override
	public String toString() {
		return "MusicalKey{" + ", \nbaseNote=" + baseNote + ", \nbase='" + base + '\'' + '}';
	}
}
