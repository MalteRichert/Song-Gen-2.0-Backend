package org.se.songgen2backend.text.generation;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.*;

/**
 * @author Olivier Stenzel
 */
public class TextTemplate {
	@JsonProperty
	private final String[] strophe;
	@JsonProperty
	private final String[] moods;
	@JsonProperty
	private final int length;

	public TextTemplate(@JsonProperty("strophe") String[] strophe, @JsonProperty("moods") String[] moods, @JsonProperty("length") int length) {
		this.strophe = strophe;
		this.moods = moods;
		this.length = length;
	}

	public String[] getStrophe() {
		return strophe;
	}

	public String[] getMoods() {
		return moods;
	}

	public int getLength() {
		return length;
	}

	@Override
	public String toString() {
		return "Template{" + "strophe=" + Arrays.toString(strophe) + ", moods=" + Arrays.toString(moods) + ", length=" + length + '}';
	}
}
