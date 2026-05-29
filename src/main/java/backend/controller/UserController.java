package backend.controller;

import backend.entity.User;
import backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestBody User updatedUser) {

        try {
            return userRepository.findById(id)
                    .map(user -> {
                        if (updatedUser.getName() != null) {
                            user.setName(updatedUser.getName());
                        }
                        if (updatedUser.getEmail() != null) {
                            user.setEmail(updatedUser.getEmail());
                        }
                        if (updatedUser.getPhone() != null) {
                            user.setPhone(updatedUser.getPhone());
                        }

                        User savedUser = userRepository.save(user);
                        return ResponseEntity.ok(savedUser);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update profile: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}