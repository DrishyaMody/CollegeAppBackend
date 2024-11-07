package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import lombok.Data;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class Market {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;
    private double price;
    private double change24h;
    private LocalDateTime lastUpdated;
}