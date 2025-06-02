package com.example.api.Service;

import com.example.api.Model.Artwork;
import com.example.api.Model.User;
import com.example.api.Repository.ArtworkRepository;
import com.example.api.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArtworkRepository artworkRepository;

    public User createUser(User user) {
        String hashed = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashed);
        return userRepository.save(user);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public boolean authenticate(String username, String password) {
        User user = findByUsername(username);
        return user != null && BCrypt.checkpw(password, user.getPassword());
    }

    public User findById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Transactional
    public boolean purchase(int userId, int artId) {
        User buyer = findById(userId);
        Artwork art = artworkRepository.findById(artId)
                .orElseThrow(() -> new RuntimeException("Artwork not found with id: " + artId));
        User seller = findById(art.getUser().getId());
        if (buyer.getId() == seller.getId()) {
            return false;
        }
        buyer.setArtBought(buyer.getArtBought() + 1);
        buyer.setTotalSpent(buyer.getTotalSpent() + art.getPrice());
        seller.setArtSold(seller.getArtSold() + 1);
        seller.setTotalIncome(seller.getTotalIncome() + art.getPrice());
        artworkRepository.delete(art);
        userRepository.save(buyer);
        userRepository.save(seller);
        return true;
    }
}
