package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class MiningPoolService {
    private final String NICEHASH_API = "https://api2.nicehash.com/main/api/v2/public/stats/global/current";
    private final String ETHERMINE_API = "https://api.ethermine.org/poolStats";
    private final String F2POOL_API = "https://api.f2pool.com/ethereum/mining_profit";
    private final RestTemplate restTemplate;

    public MiningPoolService() {
        this.restTemplate = new RestTemplate();
    }

    public double getNiceHashPrice() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(NICEHASH_API, Map.class);
            Map<String, Object> data = response.getBody();
            // NiceHash returns prices in BTC/TH/Day
            Map<String, Object> algorithms = (Map<String, Object>) ((List<?>) data.get("algorithms")).get(0);
            return ((Number) algorithms.get("paying")).doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public double getEtherminePrice() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(ETHERMINE_API, Map.class);
            Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");
            // Convert pool hashrate and price to comparable format
            return ((Number) data.get("price")).doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public double getF2PoolPrice() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(F2POOL_API, Map.class);
            Map<String, Object> data = response.getBody();
            // F2Pool returns estimated earnings per 100MH/s
            return ((Number) data.get("price")).doubleValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
} 