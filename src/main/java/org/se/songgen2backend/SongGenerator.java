package org.se.songgen2backend;

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
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * This class generates the song.
 *
 * @author Val Richter
 */
public class SongGenerator {
	private final Settings settings;
	private MidiSequence seq;

	public SongGenerator(Settings settings) {
		this.settings = settings;
	}

	protected MidiSequence call() {
		try {
			Dict dict = Dict.getDefault();
			Config.loadConfig(settings.getGenre());

			String content = FileReader.main("/Users/malterichert/miditest.mid");

			Preprocessor preprocessor = new Preprocessor(content);
			List<Sentence> sentences = preprocessor.preprocess();

			Analyzer analyzer = new Analyzer(sentences, dict);
			TermCollection terms = analyzer.buildTerms(analyzer.tag());

			Metrics metrics = MetricAnalyzer.getMetrics(content, sentences, terms);
			if (settings.getTempo() == -1) settings.setTempo(metrics.getTempo());

			seq = StructureGenerator.generateStructure(settings, terms, metrics.getMood());

			return seq;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public MidiSequence getSeq() {
		return seq;
	}

	public String getFileName() {
		String hashCode = Integer.toString(seq.hashCode());
		return settings.getGenre().toString() + seq.getBpm() + hashCode.substring(0,16);
	}
}
