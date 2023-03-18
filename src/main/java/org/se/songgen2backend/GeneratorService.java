package org.se.songgen2backend;

import org.se.songgen2backend.api.ReqBody;
import org.se.songgen2backend.music.logic.MidiSequence;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class GeneratorService {
    private SongGenerator songGenerator;
    public String generate(ReqBody request) {
        songGenerator = new SongGenerator(request.getSettings());
        MidiSequence sequence = songGenerator.call();
        String filename = songGenerator.getFileName();
        sequence.createFile("./data/" + filename + ".mid");

        return filename;
    }
}
