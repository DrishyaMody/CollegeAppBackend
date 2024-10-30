package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@Service
public class MiningService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GPURepository gpuRepository;

    // Start mining
    public boolean startMining(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent() && userOpt.get().getCurrentGpu() != null) {
            User user = userOpt.get();
            user.setMiningActive(true);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Stop mining
    public boolean stopMining(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setMiningActive(false);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Calculate mining rewards
    public double calculateRewards(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent() && userOpt.get().isMiningActive() && userOpt.get().getCurrentGpu() != null) {
            User user = userOpt.get();
            double hashrate = user.getCurrentGpu().getHashRate();
            // Simple reward calculation
            double btcReward = (hashrate * 86400) / (1e12);  // Daily reward
            return btcReward;
        }
        return 0.0;
    }

    // Buy GPU
    public boolean buyGPU(Long userId, Long gpuId) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<GPU> gpuOpt = gpuRepository.findById(gpuId);
        
        if (userOpt.isPresent() && gpuOpt.isPresent()) {
            User user = userOpt.get();
            GPU gpu = gpuOpt.get();
            
            if (user.getUsdBalance() >= gpu.getPrice()) {
                user.setUsdBalance(user.getUsdBalance() - gpu.getPrice());
                user.setCurrentGpu(gpu);
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }

    // Get all available GPUs
    public List<GPU> getAllGPUs() {
        return gpuRepository.findAll();
    }

    // Get user's mining stats
    public User getUserStats(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
}