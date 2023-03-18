package org.se.songgen2backend.music;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import java.io.File;
import java.io.IOException;
import java.util.*;
import org.se.songgen2backend.music.logic.playables.BeatContainer;
import org.se.songgen2backend.music.model.*;

/**
 * Static configuration class to parse music templates and provide access.
 *
 * @author Malte Richert
 * @reviewer Benjamin Frahm
 */

public class Config {
	private static List<Structure> structuresPop, structuresBlues;
	private static List<List<List<String>>> chordProgressionsPop, chordProgressionsBlues;
	private static HashMap<String, Integer> instrumentMapping;
	private static Genre genre;

	public static void loadConfig(Genre inputGenre) {
		YAMLFactory yaml = new YAMLFactory();
		ObjectMapper mapper = new ObjectMapper(yaml);
		genre = inputGenre;

		// load drum program config into BeatContainer
		try {
			TypeReference<HashMap<String, Integer>> type = new TypeReference<>() {
			};
			BeatContainer.setDrumPrograms(mapper.readValue(new File("./src/main/resources/static/music/drum_prog_no.yml"), type));
		} catch (IOException e) {
			System.out.println("Encountered exception while trying to read drum_prog config.");
			e.printStackTrace();
		}

		// load beat templates into BeatContainer
		try {
			YAMLParser yamlParser = yaml.createParser(new File("./src/main/resources/static/music/beat_templates.yml"));
			List<BeatData> beatDataList = mapper.readValues(yamlParser, BeatData.class).readAll();

			List<Beat> outBeats = new ArrayList<>();
			for (BeatData beatData : beatDataList) {
				outBeats.add(beatData.toBeat());
			}
			BeatContainer.setDrumBeats(outBeats);
		} catch (IOException e) {
			System.out.println("Encountered exception while trying to read Beat template.");
			e.printStackTrace();
		}

		// load structure template
		try {
			YAMLParser yamlParser = yaml.createParser(new File("./src/main/resources/static/music/structure_templates_pop.yml"));
			structuresPop = mapper.readValues(yamlParser, Structure.class).readAll();
		} catch (IOException e) {
			System.out.println("Encountered exception while trying to read Structure template for pop.");
			e.printStackTrace();
		}
		try {
			YAMLParser yamlParser = yaml.createParser(new File("./src/main/resources/static/music/structure_templates_blues.yml"));
			structuresBlues = mapper.readValues(yamlParser, Structure.class).readAll();
		} catch (IOException e) {
			System.out.println("Encountered exception while trying to read Structure template for blues.");
			e.printStackTrace();
		}

		// load chord modifiers into Chord class
		try {
			TypeReference<HashMap<String, ArrayList<Integer>>> type = new TypeReference<>() {
			};
			Chord.setChordModifiers(mapper.readValue(new File("./src/main/resources/static/music/chord_modifiers.yml"), type));
		} catch (IOException e) {
			System.out.println("Encountered exception while trying to read Chord modifiers from config.");
			e.printStackTrace();
		}

		// load chord progressions
		try {
			TypeReference<List<List<List<String>>>> type = new TypeReference<>() {
			};
			chordProgressionsPop = mapper.readValue(new File("./src/main/resources/static/music/chord_progressions_pop.yml"), type);
		} catch (IOException e) {
			System.out.println("Encountered exception while trying to read Chord progressions for pop from template.");
			e.printStackTrace();
		}
		try {
			TypeReference<List<List<List<String>>>> type = new TypeReference<>() {
			};
			chordProgressionsBlues = mapper.readValue(new File("./src/main/resources/static/music/chord_progressions_blues.yml"), type);
		} catch (IOException e) {
			System.out.println("Encountered exception while trying to read Chord progressions for blues from template.");
			e.printStackTrace();
		}

		// load instrument mappings
		try {
			TypeReference<HashMap<String, Integer>> type = new TypeReference<>() {
			};
			instrumentMapping = mapper.readValue(new File("./src/main/resources/static/music/instrument_mapping.yml"), type);
		} catch (IOException e) {
			System.out.println("Encountered exception while trying to read Instrument mappings from config.");
			e.printStackTrace();
		}
	}

	public static List<Structure> getStructures() {
		return switch (genre) {
			case POP -> structuresPop;
			case BLUES -> structuresBlues;
		};
	}

	public static List<List<List<String>>> getChordProgressions() {
		return switch (genre) {
			case POP -> chordProgressionsPop;
			case BLUES -> chordProgressionsBlues;
		};
	}

	public static Map<String, Integer> getInstrumentMapping() {
		return instrumentMapping;
	}
}
