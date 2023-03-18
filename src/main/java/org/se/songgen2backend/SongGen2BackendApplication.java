package org.se.songgen2backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SongGen2BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(SongGen2BackendApplication.class, args);

        /*
        SongGenerator songGenerator = new SongGenerator(new Settings(
                false,
                Genre.POP, 120));
        MidiSequence sequence = songGenerator.call();
        sequence.createFile("/Users/malterichert/miditest.mid");
        System.out.println("successfully saved midi file"); */

    }

}
