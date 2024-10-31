package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/mining")
@CrossOrigin(origins = {"http://localhost:8088", "http://127.0.0.1:8088"})
public class MiningController {

    @Autowired
    private GPURepository gpuRepository;

    @GetMapping("/gpus")
    public ResponseEntity<List<GPU>> getAllGPUs() {
        try {
            List<GPU> gpus = gpuRepository.findAll();
            return new ResponseEntity<>(gpus, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/gpus/category/{category}")
    public ResponseEntity<List<GPU>> getGPUsByCategory(@PathVariable String category) {
        try {
            List<GPU> gpus = gpuRepository.findByCategory(category);
            return new ResponseEntity<>(gpus, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/gpus/available")
    public ResponseEntity<List<GPU>> getAvailableGPUs() {
        try {
            List<GPU> gpus = gpuRepository.findByAvailableTrue();
            return new ResponseEntity<>(gpus, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}