package com.example.api.Repository;

import com.example.api.Model.Artwork;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArtworkRepository extends JpaRepository<Artwork, Integer> {
    List<Artwork> findByUserId(int userId);

}
