package com.example.api.Controller;

import com.example.api.Model.Artwork;
import com.example.api.Model.User;
import com.example.api.Service.ArtworkService;
import com.example.api.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artwork")
public class ArtworkController {

    @Autowired
    private ArtworkService artworkService;

    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public ResponseEntity<String> createArt(@RequestParam int userId,
                                            @RequestBody Artwork art) {
        User owner = userService.findById(userId);
        art.setUser(owner);
        artworkService.createArt(art);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("Artwork created.");
    }

    @GetMapping("/getall")
    public ResponseEntity<List<Artwork>> getAllArt() {
        return ResponseEntity.ok(artworkService.getAll());
    }

    @GetMapping("/getByUser/{userId}")
    public ResponseEntity<List<Artwork>> getByUser(@PathVariable int userId) {
        return ResponseEntity.ok(artworkService.getArtByUser(userId));
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<String> deleteArt(@PathVariable int id) {
        artworkService.deleteArt(id);
        return ResponseEntity.ok("Artwork deleted.");
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Artwork> getArt(@PathVariable int id) {
        return ResponseEntity.ok(artworkService.getArt(id));
    }
}
