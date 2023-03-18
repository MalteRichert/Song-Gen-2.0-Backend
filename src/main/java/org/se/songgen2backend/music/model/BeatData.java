package org.se.songgen2backend.music.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

/**
 * parses a beat-yaml-template to be an object of the Beat-class
 *
 * @author Malte Richert
 * @reviewer Benjamin Frahm
 */
public class BeatData {
	@JsonProperty
	private final Map<String, String> mainPattern;
	@JsonProperty
	private final Map<String, String> bigFill;
	@JsonProperty
	private final Map<String, String> smallFill;
	@JsonProperty
	private final int resolution; // midi-length of smallest time-unit in template

	@JsonCreator
	public BeatData(@JsonProperty("mainPattern") Map<String, String> mainPattern, @JsonProperty("bigFill") Map<String, String> bigFill,
			@JsonProperty("smallFill") Map<String, String> smallFill, @JsonProperty("resolution") int resolution) {
		this.mainPattern = mainPattern;
		this.bigFill = bigFill;
		this.smallFill = smallFill;
		this.resolution = resolution;
	}

	public Beat toBeat() {
		Beat beat = new Beat(new HashMap<>(), new HashMap<>(), new HashMap<>());

		for (Map<String, String> map : List.of(mainPattern, bigFill, smallFill)) {
			List<Integer> positions = new ArrayList<>();
			for (String instrumentVal : map.values()) {
				for (int i = 0; i < instrumentVal.length(); i++) {
					String symbol = instrumentVal.substring(i, i + 1);
					if (symbol.equals("x")) {
						positions.add(i * resolution);
					}
				}
			}
			// sort positions-list and remove doubles
			positions = new HashSet<>(positions).stream().sorted().toList();

			for (Map.Entry<String, String> instrument : map.entrySet()) {
				List<Integer[]> notes = new ArrayList<>();
				for (int i = 0; i < instrument.getValue().length(); i++) {
					String symbol = instrument.getValue().substring(i, i + 1);

					if (symbol.equals("x")) {
						int position = i * resolution;

						int duration;
						if (positions.indexOf(position) < positions.size() - 1) {
							duration = positions.get(positions.indexOf(position) + 1) - position;
						} else {
							duration = 96 - position;
						}
						Integer[] note = new Integer[] { position, duration };
						notes.add(note);
					}
				}
				if (map == mainPattern) {
					beat.getMainPattern().put(instrument.getKey(), notes);
				} else if (map == bigFill) {
					beat.getBigFill().put(instrument.getKey(), notes);
				} else if (map == smallFill) {
					beat.getSmallFill().put(instrument.getKey(), notes);
				}
			}
		}
		return beat;
	}

}
