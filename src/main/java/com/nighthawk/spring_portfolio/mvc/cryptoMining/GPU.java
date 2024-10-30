package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;

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

    // Constructor for creating a new GPU
    public GPU(String name, double hashRate, int powerConsumption, double price, int memory) {
        this.name = name;
        this.hashRate = hashRate;
        this.powerConsumption = powerConsumption;
        this.price = price;
        this.memory = memory;
    }
}