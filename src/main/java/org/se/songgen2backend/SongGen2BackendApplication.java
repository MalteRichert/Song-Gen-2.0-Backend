package org.se.songgen2backend;

import org.se.songgen2backend.music.logic.MidiSequence;
import org.se.songgen2backend.music.model.Genre;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class SongGen2BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SongGen2BackendApplication.class, args);
        SongGenerator songGenerator = new SongGenerator(new Settings(
                false,
                Genre.POP, 120));
        MidiSequence sequence = songGenerator.call();
        sequence.createFile("/Users/malterichert/miditest.mid");
        System.out.println("successfully saved midi file");

    }

}
