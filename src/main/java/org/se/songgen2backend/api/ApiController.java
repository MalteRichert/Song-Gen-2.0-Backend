package org.se.songgen2backend.api;

import org.apache.commons.io.IOUtils;
import org.se.songgen2backend.GeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.InputStream;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ApiController {
    @Autowired
    GeneratorService generatorService;

    @PostMapping("/generate")
    public ResponseEntity<String> returnIdOfGeneratedSong(@RequestBody ReqBody body){
        System.out.println("Request Received");
        return new ResponseEntity<>(generatorService.generate(body), HttpStatus.OK);
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
