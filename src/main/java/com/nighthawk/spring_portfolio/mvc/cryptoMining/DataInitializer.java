package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private MarketRepository marketRepository;

    @Override
    public void run(String... args) {
        if (marketRepository.count() == 0) {
            // Initialize with default values
            createMarket("BTC", 75000.0, 0.0);
            createMarket("ETH", 3000.0, 0.0);
            createMarket("NICE", 0.9, 0.0);
            createMarket("F2P", 0.95, 0.0);
        }
    }

    private void createMarket(String symbol, double price, double change) {
        Market market = new Market();
        market.setSymbol(symbol);
        market.setPrice(price);
        market.setChange24h(change);
        market.setLastUpdated(LocalDateTime.now());
        marketRepository.save(market);
    }
} 
