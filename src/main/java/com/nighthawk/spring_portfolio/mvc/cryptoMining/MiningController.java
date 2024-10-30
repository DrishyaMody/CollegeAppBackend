package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/mining")
@CrossOrigin(origins = {"http://localhost:8080", "http://127.0.0.1:8080"})
public class MiningController {

    @Autowired
    private MiningService miningService;

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
}