package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cryptoMining")
public class MiningController {

    // Handle mining request
    @PostMapping("/mine")
    public ResponseEntity<String> mineCrypto(@RequestBody MiningData data) {
        // For now, we just print the received data from frontend
        System.out.println("Mining request received: " + data.getMiningPower() + " coins.");
        return new ResponseEntity<>("Mining successful!", HttpStatus.OK);
    }

    // Handle upgrade request
    @PostMapping("/upgrade")
    public ResponseEntity<String> upgradeMiningPower(@RequestBody MiningData data) {
        if (data.getCoins() >= 10) {
            System.out.println("Upgrade request received: New mining power: " + data.getMiningPower());
            return new ResponseEntity<>("Upgrade successful!", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Not enough coins!", HttpStatus.BAD_REQUEST);
        }
    }
}

// Data class to hold mining data from frontend
class MiningData {
    private int coins;
    private int miningPower;

    // Getters and Setters
    public int getCoins() { return coins; }
    public void setCoins(int coins) { this.coins = coins; }
    
    public int getMiningPower() { return miningPower; }
    public void setMiningPower(int miningPower) { this.miningPower = miningPower; }
}
