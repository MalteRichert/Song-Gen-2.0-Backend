package org.se.songgen2backend;

import org.apache.commons.io.FileUtils;
import org.se.songgen2backend.music.Config;
import org.se.songgen2backend.music.logic.MidiSequence;
import org.se.songgen2backend.music.logic.StructureGenerator;
import org.se.songgen2backend.text.analysis.Analyzer;
import org.se.songgen2backend.text.analysis.FileReader;
import org.se.songgen2backend.text.analysis.Preprocessor;
import org.se.songgen2backend.text.analysis.TermCollection;
import org.se.songgen2backend.text.metric.MetricAnalyzer;
import org.se.songgen2backend.text.metric.Metrics;
import org.se.songgen2backend.text.analysis.dict.Dict;
import org.se.songgen2backend.text.analysis.model.Sentence;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * This class generates the song.
 *
 * @author Val Richter
 */
public class SongGenerator {
	private final Settings settings;
	private final byte[] inputBytes;
	private MidiSequence seq;

	public SongGenerator(Settings settings, byte[] inputBytes) {
		this.settings = settings;
		this.inputBytes = inputBytes;
	}

	protected MidiSequence call() {
		try {
			Config.loadConfig(settings.getGenre());

			if (settings.getTextModeFlag())
				textMode();
			else
				musicMode();

			return seq;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void musicMode() {
		seq = StructureGenerator.generateStructure(settings);
	}

	private void textMode() throws IOException {
		Dict dict = Dict.getDefault();

		FileUtils.writeByteArrayToFile(new File("./data/text." + settings.getFileType()), inputBytes);
		String content = FileReader.main("./data/text." + settings.getFileType());

		Preprocessor preprocessor = new Preprocessor(content);
		List<Sentence> sentences = preprocessor.preprocess();

		Analyzer analyzer = new Analyzer(sentences, dict);
		TermCollection terms = analyzer.buildTerms(analyzer.tag());

		Metrics metrics = MetricAnalyzer.getMetrics(content, sentences, terms);
		if (settings.getTempo() == -1) settings.setTempo(metrics.getTempo());

		seq = StructureGenerator.generateStructure(settings, terms, metrics.getMood());
	}

	public MidiSequence getSeq() {
		return seq;
	}

	public String getFileName() {
		String hashCode = Integer.toString(seq.hashCode());
		return settings.getGenre().toString() +"_" + seq.getBpm() + "_" + (hashCode.length()>16 ?hashCode.substring(0,16) : hashCode);
	}
}
