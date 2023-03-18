package org.se.songgen2backend.music.model;

import java.util.List;
import java.util.Map;

/**
 * Class modelling a parsed drumbeat.
 * Parsing is done by the BeatData-class
 * 
 * @author Malte Richert
 * @reviewer Benjamin Frahm
 */

public class Beat {
	final Map<String, List<Integer[]>> mainPattern;
	final Map<String, List<Integer[]>> bigFill;
	final Map<String, List<Integer[]>> smallFill;

	public Beat(Map<String, List<Integer[]>> mainPattern, Map<String, List<Integer[]>> bigFill, Map<String, List<Integer[]>> smallFill) {

		this.mainPattern = mainPattern;
		this.bigFill = bigFill;
		this.smallFill = smallFill;
	}

	public Map<String, List<Integer[]>> getMainPattern() {
		return mainPattern;
	}

	public Map<String, List<Integer[]>> getBigFill() {
		return bigFill;
	}

	public Map<String, List<Integer[]>> getSmallFill() {
		return smallFill;
	}
}