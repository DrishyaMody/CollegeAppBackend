package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.*;
import java.time.LocalDateTime;

@Service
public class MiningService {
    @Autowired
    private MiningUserRepository miningUserRepository;
    
    @Autowired
    private MarketRepository marketRepository;
    
    @Autowired
    private GPURepository gpuRepository;

    @Scheduled(fixedRate = 60000) // Every minute
    public void processMining() {
        List<MiningUser> activeMiners = miningUserRepository.findAll().stream()
            .filter(MiningUser::isMining)
            .toList();
            
        for (MiningUser miner : activeMiners) {
            double hashrate = miner.getCurrentHashrate();
            double btcMined = hashrate * 60 / (1e12); // Example calculation
            miner.setPendingBalance(miner.getPendingBalance() + btcMined);
            miner.setShares(miner.getShares() + 1);
            miningUserRepository.save(miner);
        }
    }

    public Map<String, Object> buyGPU(MiningUser user, Long gpuId) {
        GPU gpu = gpuRepository.findById(gpuId).orElse(null);
        if (gpu == null) {
            return Map.of("success", false, "message", "GPU not found");
        }

        Market btcMarket = marketRepository.findBySymbol("BTC");
        if (btcMarket == null) {
            return Map.of("success", false, "message", "Market data unavailable");
        }

        double gpuCostInBTC = gpu.getPrice() / btcMarket.getPrice();
        if (user.getBtcBalance() < gpuCostInBTC) {
            return Map.of("success", false, "message", "Insufficient funds");
        }

        user.setBtcBalance(user.getBtcBalance() - gpuCostInBTC);
        user.addGPU(gpu);
        miningUserRepository.save(user);

        return Map.of(
            "success", true,
            "message", "Successfully purchased " + gpu.getName(),
            "newBalance", user.getBtcBalance()
        );
    }

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void updateMarkets() {
        updateMarket("BTC", 30000, 45000);
        updateMarket("ETH", 1800, 2200);
        updateMarket("NICE", 0.0001, 0.0002);
        updateMarket("F2P", 0.00015, 0.00025);
    }

    private void updateMarket(String symbol, double minPrice, double maxPrice) {
        Market market = marketRepository.findBySymbol(symbol);
        if (market == null) {
            market = new Market();
            market.setSymbol(symbol);
        }
        
        double oldPrice = market.getPrice();
        double newPrice = minPrice + Math.random() * (maxPrice - minPrice);
        double change = ((newPrice - oldPrice) / oldPrice) * 100;
        
        market.setPrice(newPrice);
        market.setChange24h(change);
        market.setLastUpdated(LocalDateTime.now());
        
        marketRepository.save(market);
    }
}