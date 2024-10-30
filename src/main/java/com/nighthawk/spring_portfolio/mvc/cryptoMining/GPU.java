package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gpus")
public class GPU {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double hashRate;  // in MH/s
    private int powerConsumption;  // in Watts
    private double price;  // in USD
    private int memory;  // in GB
    private boolean available = true;
    private String category;  // "Free", "Budget", "Mid-Range", "High-End", "Premium"
    private int temp;  // in Celsius
    private double efficiency;  // MH/W

    // Constructor for creating a new GPU
    public GPU(String name, double hashRate, int powerConsumption, double price, int memory, 
               String category, int temp) {
        this.name = name;
        this.hashRate = hashRate;
        this.powerConsumption = powerConsumption;
        this.price = price;
        this.memory = memory;
        this.category = category;
        this.temp = temp;
        this.efficiency = hashRate / powerConsumption;
    }

    // Static method to initialize default GPUs
    public static List<GPU> initializeGPUs() {
        return Arrays.asList(
            // Free Starter GPUs
            new GPU("NVIDIA GeForce GT 1030", 1.5, 30, 0, 2, "Free", 65),
            
            // Budget GPUs ($50-500)
            new GPU("NVIDIA GeForce GTX 1050", 14, 75, 150, 4, "Budget", 67),
            new GPU("AMD RX 570 8GB", 28, 120, 250, 8, "Budget", 70),
            new GPU("NVIDIA GTX 1660", 30, 125, 300, 6, "Budget", 69),
            new GPU("AMD RX 580 8GB", 32, 135, 350, 8, "Budget", 72),
            
            // Mid-Range GPUs ($500-1500)
            new GPU("NVIDIA RTX 3060", 50, 170, 800, 12, "Mid-Range", 67),
            new GPU("AMD RX 6600 XT", 47, 160, 900, 8, "Mid-Range", 68),
            new GPU("NVIDIA RTX 3060 Ti", 60, 200, 1200, 8, "Mid-Range", 70),
            
            // High-End GPUs ($1500-3000)
            new GPU("NVIDIA RTX 3070", 62, 220, 2000, 8, "High-End", 71),
            new GPU("AMD RX 6800 XT", 65, 300, 2200, 16, "High-End", 72),
            new GPU("NVIDIA RTX 3080", 98, 320, 2500, 10, "High-End", 73),
            
            // Premium GPUs ($3000+)
            new GPU("NVIDIA RTX 3090", 120, 350, 3000, 24, "Premium", 74),
            new GPU("AMD RX 6900 XT", 110, 330, 3200, 16, "Premium", 74),
            new GPU("NVIDIA RTX 4080", 130, 400, 3500, 16, "Premium", 75),
            new GPU("NVIDIA RTX 4090", 140, 450, 4000, 24, "Premium", 75)
        );
    }
}