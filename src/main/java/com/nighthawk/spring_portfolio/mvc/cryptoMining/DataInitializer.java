package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private GPURepository gpuRepository;

    @Override
    public void run(String... args) {
        // Only initialize if DB is empty
        if (gpuRepository.count() == 0) {
            GPU.initializeGPUs().forEach(gpu -> gpuRepository.save(gpu));
        }
    }
}