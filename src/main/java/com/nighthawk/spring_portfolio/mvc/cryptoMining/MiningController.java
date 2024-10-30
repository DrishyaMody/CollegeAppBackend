package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/mining")
@CrossOrigin(origins = {"http://localhost:8085", "http://127.0.0.1:8085"})
public class MiningController {

    @Autowired
    private GPURepository gpuRepository;

    @GetMapping("/gpus")
    public ResponseEntity<List<GPU>> getAllGPUs() {
        return ResponseEntity.ok(gpuRepository.findAll());
    }

    @GetMapping("/gpus/category/{category}")
    public ResponseEntity<List<GPU>> getGPUsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(gpuRepository.findByCategory(category));
    }

    @GetMapping("/gpus/available")
    public ResponseEntity<List<GPU>> getAvailableGPUs() {
        return ResponseEntity.ok(gpuRepository.findByAvailableTrue());
    }
}