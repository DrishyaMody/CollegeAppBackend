package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/mining")
@CrossOrigin(origins = {"http://localhost:8085", "http://127.0.0.1:8085"})
public class MiningController {

    @Autowired
    private MiningService miningService;

    @Autowired
    private UserRepository userRepository;

    // Create a static class for the request body
    public static class CreateUserRequest {
        private String username;
        private String password;
        private String email;

        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    @PostMapping("/user/create")
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        try {
            if (request.getUsername() == null || request.getPassword() == null || request.getEmail() == null) {
                return new ResponseEntity<>("Username, password, and email are required", 
                                         HttpStatus.BAD_REQUEST);
            }

            // Create a new user
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setEmail(request.getEmail());
            
            // Set default values
            user.setBtcBalance(0.0);
            user.setUsdBalance(1000.0);
            user.setMiningActive(false);
            user.setTotalMined(0.0);
            user.setTotalShares(0);
            user.setCurrentPool("NiceHash");

            // Save and return the user
            User savedUser = userRepository.save(user);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to create user: " + e.getMessage(), 
                                      HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/start/{userId}")
    public ResponseEntity<String> startMining(@PathVariable Long userId) {
        boolean success = miningService.startMining(userId);
        if (success) {
            return ResponseEntity.ok("Mining started successfully");
        }
        return ResponseEntity.badRequest().body("Failed to start mining");
    }

    @PostMapping("/stop/{userId}")
    public ResponseEntity<String> stopMining(@PathVariable Long userId) {
        boolean success = miningService.stopMining(userId);
        if (success) {
            return ResponseEntity.ok("Mining stopped successfully");
        }
        return ResponseEntity.badRequest().body("Failed to stop mining");
    }

    @GetMapping("/rewards/{userId}")
    public ResponseEntity<Double> getRewards(@PathVariable Long userId) {
        double rewards = miningService.calculateRewards(userId);
        return ResponseEntity.ok(rewards);
    }

    @PostMapping("/buy-gpu/{userId}/{gpuId}")
    public ResponseEntity<String> buyGPU(
            @PathVariable Long userId, 
            @PathVariable Long gpuId) {
        boolean success = miningService.buyGPU(userId, gpuId);
        if (success) {
            return ResponseEntity.ok("GPU purchased successfully");
        }
        return ResponseEntity.badRequest().body("Failed to purchase GPU");
    }

    @GetMapping("/gpus")
    public ResponseEntity<List<GPU>> getAllGPUs() {
        List<GPU> gpus = miningService.getAllGPUs();
        return ResponseEntity.ok(gpus);
    }

    @GetMapping("/stats/{userId}")
    public ResponseEntity<User> getUserStats(@PathVariable Long userId) {
        User user = miningService.getUserStats(userId);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    // Add GPU endpoint
    @PostMapping("/gpu/add")
    public ResponseEntity<?> addGPU(@RequestBody GPU gpu) {
        try {
            GPU savedGPU = gpuRepository.save(gpu);
            return new ResponseEntity<>(savedGPU, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to add GPU: " + e.getMessage(), 
                                      HttpStatus.BAD_REQUEST);
        }
    }

    // Get all users endpoint
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @Autowired
    private GPURepository gpuRepository;
}