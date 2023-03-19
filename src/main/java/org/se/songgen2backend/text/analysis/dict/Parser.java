package org.se.songgen2backend.text.analysis.dict;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.se.songgen2backend.text.analysis.model.*;

/**
 * @author Val Richter
 */
public class Parser {
	/**
	 * Parse a String into a specified Class. The supported Classes are:
	 * - {@link String}
	 * - {@link GrammaticalCase}
	 * - {@link Person}
	 * - {@link Tense}
	 * - {@link Gender}
	 * - {@link Numerus}
	 * - {@link CompoundPart}
	 * - {@link AffixType}
	 * - {@link Boolean}
	 * - {@link Integer}
	 * - {@link List}
	 *
	 * @param <T>
	 *            The Type of the desired Class
	 * @param s
	 *            The String to parse.
	 * @param cls
	 *            The class of the desired Type
	 * @return Returns an {@link Optional} value, that holds an object of Type `T` if it was successfully parsed and is
	 *         empty otherwise.
	 */
	public static <T> Optional<T> parse(String s, Class<T> cls) {
		if (cls == String.class) return Optional.ofNullable(cls.cast(s));
		else if (cls == GrammaticalCase.class) return Optional.ofNullable(cls.cast(parseGrammaticalCase(s)));
		else if (cls == Person.class) return Optional.ofNullable(cls.cast(parsePerson(s)));
		else if (cls == Tense.class) return Optional.ofNullable(cls.cast(parseTense(s)));
		else if (cls == Gender.class) return Optional.ofNullable(cls.cast(parseGender(s)));
		else if (cls == Numerus.class) return Optional.ofNullable(cls.cast(parseNumerus(s)));
		else if (cls == CompoundPart.class) return Optional.ofNullable(cls.cast(parseCompoundPart(s)));
		else if (cls == AffixType.class) return Optional.ofNullable(cls.cast(parseAffixType(s)));
		else if (cls == Boolean.class) return Optional.of(cls.cast(parseBool(s)));
		else if (cls == Integer.class) return Optional.ofNullable(cls.cast(parseInt(s)));
		else if (cls == List.class) return Optional.of(cls.cast(parseList(s)));

		return Optional.empty();
	}

	public static GrammaticalCase parseGrammaticalCase(String s) {
		s = s.toLowerCase();
		return switch (s.charAt(0)) {
			case 'g' -> GrammaticalCase.GENITIVE;
			case 'd' -> GrammaticalCase.DATIVE;
			case 'a' -> GrammaticalCase.ACCUSATIVE;
			default -> GrammaticalCase.NOMINATIVE;
		};
	}

	public static Person parsePerson(String s) {
		s = s.toLowerCase();
		return switch (s.charAt(0)) {
			case '2', 's' -> Person.SECOND;
			case '3', 't' -> Person.THIRD;
			default -> Person.FIRST;
		};
	}

	public static Tense parseTense(String s) {
		s = s.toLowerCase();
		if (s.startsWith("pr")) return Tense.PRESENT;
		else if (s.startsWith("pas")) return Tense.PAST;
		else return Tense.PARTICIPLE;
	}

	public static Gender parseGender(String s) {
		return switch (s.toLowerCase().charAt(0)) {
			case 'm' -> Gender.MALE;
			case 'f' -> Gender.FEMALE;
			default -> Gender.NEUTRAL;
		};
	}

	public static Numerus parseNumerus(String s) {
		return switch (s.toLowerCase().charAt(0)) {
			// t for true
			case 't' -> Numerus.PLURAL;

			// p for plural
			case 'p' -> Numerus.PLURAL;
			default -> Numerus.SINGULAR;
		};
	}

	public static CompoundPart parseCompoundPart(String s) {
		if (s.toLowerCase().charAt(0) == 's') {
			return CompoundPart.SUBTRACTION;
		}
		return CompoundPart.ADDITION;
	}

	public static AffixType parseAffixType(String s) {
		if (s.toLowerCase().charAt(0) == 's') return AffixType.SUFFIX;
		return AffixType.PREFIX;
	}

	public static Integer parseInt(String s) {
		return parseInt(s, 0);
	}

	public static Integer parseInt(String s, Integer def) {
		try {
			return Integer.parseInt(s.trim());
		} catch (Exception e) {
			return def;
		}
	}

	public static boolean parseBool(String s) {
		return s.toLowerCase().startsWith("t");
	}

	public static String[] parseList(String s) {
		return s.split("-");
	}

	/**
	 * Parse a CSV File with a given function. The function is consumes each row of the CSV-File except for the first, which
	 * is interpreted as the header-row
	 * <p>
	 * The CSV-File is expected to have no commas or newline-characters inside of values.
	 *
	 * @param filepath
	 *            The Path to the CSV-File
	 * @param forEachRow
	 *            The function that is executed on each row of the CSV-File. Its Input is a {@link WordWithData} object,
	 *            which is a wrapper around a {@link java.util.HashMap} of Strings to Strings. In other words, each row is
	 *            given as a map from the column's name to the cell's value.
	 */
	public static void parseCSV(Path filepath, Consumer<? super WordWithData> forEachRow) throws IOException {
		if (!filepath.toString().endsWith(".csv") && !Files.exists(filepath)) {
			filepath = Path.of(filepath + ".csv");
		}

		String[] rows = Files.readString(filepath).split("\\r?\\n");
		String[] firstRow = rows[0].split(",");

		for (int i = 1; i < rows.length; i++) {
			String[] cols = rows[i].split(",");
			WordWithData row = new WordWithData();
			for (int j = 0; j < firstRow.length && j < cols.length; j++) {
				row.put(firstRow[j], cols[j]);
			}
			forEachRow.accept(row);
		}
	}

	/**
	 * This function uses generics to set the attributes in a given class with the parsed entries of a CSV file. For this to
	 * work, the field in the CSV has to have the same name as the corresponding attribute in the class. And the attribute
	 * has to be public (either exactly the same name, or the same as the lowercase attribute's name). This is also the
	 * reason many Classes have public attributes in this project.
	 *
	 * @param <T>
	 *            The Type that each row should be parsed into.
	 * @param filepath
	 *            The Path to the CSV-File
	 * @param list
	 *            The list which should be populated with the data in the CSV-File
	 * @param cls
	 *            The Class of Type `T`. This is necessary to use Java's reflection capabilities.
	 */
	public static <T> void parseCSV(Path filepath, List<T> list, Class<T> cls) throws IOException {
		parseCSV(filepath, row -> {
			try {
				T t = cls.getDeclaredConstructor().newInstance();

				for (Field field : cls.getFields()) {
					String s = null;
					String fieldName = field.getName();
					if (row.containsKey(fieldName)) {
						s = row.get(fieldName);
					} else {
						String tmp = fieldName.toLowerCase();
						if (row.containsKey(tmp)) s = row.get(tmp);
					}

					if (s != null) {
						Optional<?> val = parse(s, field.getType());
						if (val.isPresent()) field.set(t, val.get());
					}
				}

				list.add(t);
			} catch (Exception e) {
				System.out.println("Encountered exception while parsing csv-file.");
				e.printStackTrace();
			}
		});
	}

	public static void readCSV(Path filepath, WordList list) throws IOException {
		parseCSV(filepath, list::uncheckedInsert);
		list.sort();
	}

	public static void readCSV(Path filepath, List<WordWithData> list) throws IOException {
		parseCSV(filepath, list::add);
	}
}
