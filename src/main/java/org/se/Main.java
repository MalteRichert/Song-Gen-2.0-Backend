package org.se;

import java.io.IOException;
import java.nio.file.Path;

import org.se.Text.Analysis.Analyzer;
import org.se.Text.Analysis.TermCollection;

public class Main {
    public static void main(String[] args) throws IOException {

		TermCollection x = Analyzer.analyze(Path.of("", "test.txt"));
    }
}