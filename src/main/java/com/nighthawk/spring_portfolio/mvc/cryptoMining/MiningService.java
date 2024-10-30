package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Service
public class MiningService {
    
    @Autowired
    private GPURepository gpuRepository;

    public List<GPU> getAllGPUs() {
        return gpuRepository.findAll();
    }

    public List<GPU> getGPUsByCategory(String category) {
        return gpuRepository.findByCategory(category);
    }

    public double calculateMiningReward(GPU gpu) {
        double hashrate = gpu.getHashRate();
        // Simple reward calculation (can be adjusted)
        return (hashrate * 86400) / (1e12);  // Daily BTC reward
    }
}