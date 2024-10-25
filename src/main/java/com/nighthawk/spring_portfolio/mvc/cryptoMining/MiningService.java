package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;

@Service  // Marks this class as a service that provides business logic
public class MiningService {
    
    private List<MiningData> miningData;

    @Autowired
    public MiningService() {
        this.miningData = new ArrayList<>(); // Initialize the list
    }

    // Simulate a mining operation
    public String mine() {
        // Your logic for mining goes here
        return "Mining operation performed";
    }

    // Get all mining data
    public List<MiningData> getMiningData() {
        return miningData;
    }

    // Add a new mining record
    public void addMiningData(MiningData data) {
        miningData.add(data);
    }
}
