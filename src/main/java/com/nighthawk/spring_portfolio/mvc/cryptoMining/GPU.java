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
            new GPU("NVIDIA GeForce GT 1030", 1.5, 30, 0, 2, "Free", 65),
            new GPU("NVIDIA GeForce GTX 1050", 14, 75, 150, 4, "Budget", 67),
            new GPU("AMD RX 570 8GB", 28, 120, 250, 8, "Budget", 70),
            new GPU("NVIDIA RTX 3060 Ti", 60, 200, 1700, 8, "High-End", 70),
            new GPU("NVIDIA RTX 3070", 62, 220, 2000, 8, "High-End", 71),
            new GPU("NVIDIA RTX 4090", 140, 450, 4000, 24, "Premium", 75)
        );
    }
}