package org.se.songgen2backend;

import org.se.songgen2backend.music.logic.MidiSequence;
import org.springframework.stereotype.Component;

@Component
public class GeneratorService {
    public String generate(Settings settings, byte[] inputFile) {
        SongGenerator songGenerator = new SongGenerator(settings, inputFile);
        MidiSequence sequence = songGenerator.call();
        String filename = songGenerator.getFileName();
        sequence.createFile("/data/" + filename + ".mid");

        return filename;
    }
}
