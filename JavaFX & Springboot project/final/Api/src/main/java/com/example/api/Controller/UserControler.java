package com.example.api.Controller;

import com.example.api.Model.User;
import com.example.api.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserControler {

    @Autowired
    private UserService userService;

    @PostMapping("signup")
    public ResponseEntity<User> signup(@RequestBody User user) {
        if (userService.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.badRequest().build();
        }
        User created = userService.createUser(user);
        created.setPassword(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("login")
    public ResponseEntity<User> login(@RequestParam String username,
                                      @RequestParam String password) {
        if (!userService.authenticate(username, password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.findByUsername(username);
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @GetMapping("get/{username}")
    public ResponseEntity<User> getUser(@PathVariable String username) {
        User user = userService.findByUsername(username);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @GetMapping("report/{userId}")
    public ResponseEntity<User> getReport(@PathVariable int userId) {
        User user = userService.findById(userId);
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    @PostMapping("purchase")
    public ResponseEntity<String> purchaseArtwork(@RequestParam int userId,
                                                  @RequestParam int artId) {
        try {
            if (userService.purchase(userId, artId)) {
                return ResponseEntity.ok("Purchase completed successfully.");
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User cant buy their own art");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
