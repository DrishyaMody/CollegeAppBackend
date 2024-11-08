package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.*;
import java.time.LocalDateTime;

@Service
public class MiningService {
    @Autowired
    private MarketRepository marketRepository;
    
    @Autowired
    private CoinGeckoService coinGeckoService;

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void updateMarkets() {
        try {
            // Get Bitcoin price
            Map<String, Object> btcData = coinGeckoService.getCryptoPrice("bitcoin");
            if (btcData != null) {
                double btcPrice = ((Number) btcData.get("usd")).doubleValue();
                updateMarket("BTC", btcPrice, 0.0);
                
                // Simple pool rates based on BTC price
                updateMarket("NICE", btcPrice * 0.00001, 0.0);
                updateMarket("F2P", btcPrice * 0.000012, 0.0);
            }

            // Get Ethereum price
            Map<String, Object> ethData = coinGeckoService.getCryptoPrice("ethereum");
            if (ethData != null) {
                double ethPrice = ((Number) ethData.get("usd")).doubleValue();
                updateMarket("ETH", ethPrice, 0.0);
            }
        } catch (Exception e) {
            // Fallback values if API fails
            updateMarket("BTC", 75000.0, 0.0);
            updateMarket("ETH", 3000.0, 0.0);
            updateMarket("NICE", 0.75, 0.0);
            updateMarket("F2P", 0.80, 0.0);
        }
    }

    private void updateMarket(String symbol, double price, double change24h) {
        Market market = marketRepository.findBySymbol(symbol);
        if (market == null) {
            market = new Market();
            market.setSymbol(symbol);
        }
        market.setPrice(price);
        market.setChange24h(change24h);
        market.setLastUpdated(LocalDateTime.now());
        marketRepository.save(market);
    }
}