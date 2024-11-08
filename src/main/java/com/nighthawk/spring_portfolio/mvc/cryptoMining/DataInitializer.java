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
        // Only initialize if no GPUs exist
        if (gpuRepository.count() == 0) {
            initializeGPUs();
        }
    }

    private void initializeGPUs() {
       // Free Starter GPU
       createGPU("NVIDIA GeForce GT 1030", 1.55, 30, 65, 0, "Free Starter GPU");

       // Budget GPUs ($50-500)
       createGPU("NVIDIA GeForce GTX 1050", 14, 75, 67, 150, "Budget GPUs ($50-500)");
       createGPU("AMD RX 570 8GB", 28, 120, 70, 250, "Budget GPUs ($50-500)");
       createGPU("NVIDIA GeForce GTX 1060 6GB", 22, 120, 68, 400, "Budget GPUs ($50-500)");

       // Mid-Range GPUs ($500-1500)
       createGPU("NVIDIA GeForce GTX 1660 SUPER", 31, 125, 69, 800, "Mid-Range GPUs ($500-1500)");
       createGPU("AMD RX 5600 XT", 40, 150, 71, 1000, "Mid-Range GPUs ($500-1500)");
       createGPU("NVIDIA RTX 2060", 32, 160, 70, 1200, "Mid-Range GPUs ($500-1500)");
       createGPU("NVIDIA RTX 2070", 42, 175, 71, 1400, "Mid-Range GPUs ($500-1500)");

       // High-End GPUs ($1500-3000)
       createGPU("NVIDIA RTX 3060 Ti", 60, 200, 70, 1700, "High-End GPUs ($1500-3000)");
       createGPU("NVIDIA RTX 3070", 62, 220, 71, 2000, "High-End GPUs ($1500-3000)");
       createGPU("NVIDIA RTX 3080", 64, 300, 73, 2300, "High-End GPUs ($1500-3000)");
       createGPU("NVIDIA RTX 3090", 98, 320, 72, 2800, "High-End GPUs ($1500-3000)");

       // Premium GPUs ($3000+)
       createGPU("NVIDIA RTX 4070", 100, 285, 71, 3200, "Premium GPUs ($3000+)");
       createGPU("AMD RX 7900 XTX", 110, 355, 73, 3500, "Premium GPUs ($3000+)");
       createGPU("NVIDIA RTX 4080", 130, 320, 73, 3800, "Premium GPUs ($3000+)");
       createGPU("NVIDIA RTX 4090", 140, 450, 75, 4000, "Premium GPUs ($3000+)");
   }

    private void createGPU(String name, double hashRate, int power, int temp, double price, String category) {
        GPU gpu = new GPU();
        gpu.setName(name);
        gpu.setHashRate(hashRate);
        gpu.setPowerConsumption(power);
        gpu.setTemp(temp);
        gpu.setPrice(price);
        gpu.setCategory(category);
        gpuRepository.save(gpu);
    }
} 