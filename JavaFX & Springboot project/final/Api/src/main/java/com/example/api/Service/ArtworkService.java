package com.example.api.Service;


import com.example.api.Repository.ArtworkRepository;
import com.example.api.Model.Artwork;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArtworkService {
    @Autowired
    private ArtworkRepository artworkRepository;

    public void createArt(Artwork art){
        artworkRepository.save(art);
    }

    public Artwork getArt(int id) {
        return artworkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Artwork not found with id: " + id));
    }

    @Transactional
    public void deleteArt(int id) {

        artworkRepository.deleteById(id);
    }

    public List<Artwork> getArtByUser(int userId) {
        return artworkRepository.findByUserId(userId);
    }

    public List<Artwork> getAll() {
        return artworkRepository.findAll();
    }
}
