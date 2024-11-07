package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
public class GPU {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double hashRate;  // MH/s
    private int powerConsumption;  // Watts
    private int temp;  // Celsius
    private double price;  // USD
    private String category;
    private boolean available = true;

    // Calculated property
    public double getEfficiency() {
        return hashRate / powerConsumption;
    }
    
    public double getHashRate() {
        return this.hashRate;
    }
}