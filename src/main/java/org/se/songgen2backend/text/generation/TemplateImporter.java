package org.se.songgen2backend.text.generation;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.se.songgen2backend.music.model.Genre;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

/**
 * @author Olivier Stenzel
 */
public class TemplateImporter {
	final YAMLFactory yaml = new YAMLFactory();
	final ObjectMapper mapper = new ObjectMapper(yaml);

	/**
	 * returns all templates based on the genre
	 */
	public List<TextTemplate> getTemplates(Genre genre) {
		YAMLParser yamlParser;
		// load Templates from yml
		try {
			if (genre == Genre.POP) {
				yamlParser = yaml.createParser(new File("./src/main/resources/static/text/text_templates_pop.yml"));
			} else {
				yamlParser = yaml.createParser(new File("./src/main/resources/static/text/text_templates_blues.yml"));
			}
			return mapper.readValues(yamlParser, TextTemplate.class).readAll();

		} catch (IOException e) {
			System.out.println("Encountered exception while trying to read " + genre + "-Template.");
			e.printStackTrace();

			return List.of();
		}

	}
}
