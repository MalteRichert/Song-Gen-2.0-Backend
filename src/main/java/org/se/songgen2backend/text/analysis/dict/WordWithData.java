package org.se.songgen2backend.text.analysis.dict;

import java.util.*;

/**
 * @author Val Richter
 * @reviewer Jakob Kautz
 *
 *           This is just a convenient wrapper around a `HashMap<String, String>`. It is primarily used for data that
 *           comes from CSV-Files. The main convenience of this wrapper comes from a) having a base key and b) allowing
 *           immediately parsing the String-values via the {@link Parser} class
 */
public class WordWithData extends HashMap<String, String> {
	private final String baseKey;

	public WordWithData() {
		this.baseKey = "radix";
	}

	public WordWithData(String baseKey) {
		this.baseKey = baseKey;
	}

	public String get() {
		return this.get(baseKey);
	}

	@Override
	public String toString() {
		StringBuilder strbuilder = new StringBuilder();
		for (Entry<String, String> entry : entrySet()) {
			strbuilder.append(", ");
			strbuilder.append(entry.getKey());
			strbuilder.append("='");
			strbuilder.append(entry.getValue());
			strbuilder.append("'");
		}
		return "{" + " baseKey='" + getBaseKey() + "'" + strbuilder + "}";
	}

	public <T> Optional<T> get(String key, Class<T> cls) {
		return Parser.parse(get(key), cls);
	}

	public String getBaseKey() {
		return this.baseKey;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		return o instanceof WordWithData;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(baseKey);
	}
}
