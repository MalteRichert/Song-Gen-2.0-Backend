package org.se.songgen2backend;

import org.apache.commons.io.IOUtils;
import org.se.songgen2backend.music.model.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ApiController {
    @Autowired
    GeneratorService generatorService;

    @PostMapping("/generate")
    public ResponseEntity<String> returnIdOfGeneratedSong(
            @RequestParam(name = "file", required = false) MultipartFile multipartFile,
            @RequestParam("genre") String genre,
            @RequestParam("BPM") String bpm,
            @RequestParam("fileType") String fileType
            ){
        System.out.println("Request Received");
        Settings settings = new Settings(!(multipartFile == null), Genre.valueOf(genre), Integer.parseInt(bpm), fileType);
        byte[] inputFile;
        if (!(multipartFile == null)) {
            try {
                inputFile = multipartFile.getBytes();
            } catch (IOException e) {
                inputFile = null;
                settings.setTextMode(false);
            }
        } else
            inputFile = null;

        return new ResponseEntity<>(generatorService.generate(settings, inputFile), HttpStatus.OK);
    }

    @GetMapping(
            value = "/file/{filename}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public @ResponseBody byte[] getMidiFile(@PathVariable("filename") String filename) {
        try {
            System.out.println("Received Request");
            InputStream is = new FileInputStream("./data/" + filename + ".mid");
            return IOUtils.toByteArray(is);
        } catch (Exception e) {
            return new byte[]{};
        }
    }
}
