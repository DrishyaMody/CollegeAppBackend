package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private GPURepository gpuRepository;

    @Override
    @Transactional
    public void run(String... args) {
        try {
            // Print current count
            System.out.println("Current GPU count: " + gpuRepository.count());
            
            // Only initialize if DB is empty
            if (gpuRepository.count() == 0) {
                System.out.println("Initializing GPUs...");
                GPU.initializeGPUs().forEach(gpu -> {
                    gpuRepository.save(gpu);
                    System.out.println("Saved GPU: " + gpu.getName());
                });
                System.out.println("GPU initialization complete!");
            }
        } catch (Exception e) {
            System.err.println("Error initializing GPUs: " + e.getMessage());
            e.printStackTrace();
        }
    }
}