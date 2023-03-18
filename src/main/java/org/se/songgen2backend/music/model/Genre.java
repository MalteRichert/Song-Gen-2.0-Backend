package org.se.songgen2backend.music.model;

import java.util.Arrays;

/**
 * Available Genre
 *
 * @author Malte Richert
 * @reviewer Benjamin Frahm
 */
public enum Genre {
	POP, BLUES;

	public static String[] names() {
		String valuesStr = Arrays.toString(Genre.values());
		return valuesStr.substring(1, valuesStr.length() - 1).replace(" ", "").split(",");
	}
}
