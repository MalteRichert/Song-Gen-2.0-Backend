package org.se.songgen2backend.text.analysis;

import java.io.IOException;
import com.ibm.icu.text.CharsetDetector;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

//Reurn ist string!!!

/**
 * @author Jakob Kautz
 */
public class FileReader {
	public static String main(String path) throws IOException {
		// Bekomme Pfad von UI

		// Unterschied zwischen pdf und txt Datei
		String[] cText = { ".txt", ".text", ".log", ".java" };
		String cPdf = ".pdf";

		if (path.endsWith(cPdf)) {
			return readPdf(path);
		}
		for (String ending : cText) {
			if (path.endsWith(ending)) {
				return readTxt(path);
			}
		}

		return ("Path does not meet requirements"); // Need error management because the code would still use this as
													// data input
	}

	public static String readPdf(String path) throws IOException {
		StringBuilder builder = new StringBuilder();
		try {
			PdfReader reader = new PdfReader(path);
			int numberOfPages = reader.getNumberOfPages();
			String s;
			for (int i = 1; i <= numberOfPages; i++) {
				s = PdfTextExtractor.getTextFromPage(reader, i);
				builder.append(s).append("\n");
			}
			return builder.toString();
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static String readTxt(String p) throws IOException {
		try {
			CharsetDetector detector = new CharsetDetector();
			Path path = Paths.get(p);
			byte[] bytes = Files.readAllBytes(path);
			return detector.getString(bytes, "");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}