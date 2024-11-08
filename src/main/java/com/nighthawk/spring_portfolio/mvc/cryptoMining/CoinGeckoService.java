package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CoinGeckoService {
    private final String COINGECKO_API = "https://api.coingecko.com/api/v3";
    private final RestTemplate restTemplate;

    public CoinGeckoService() {
        this.restTemplate = new RestTemplate();
    }

    public Map<String, Object> getCryptoPrice(String cryptoId) {
        try {
            String url = COINGECKO_API + "/simple/price?ids=" + cryptoId + 
                        "&vs_currencies=usd&include_24hr_change=true";
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return (Map<String, Object>) response.getBody().get(cryptoId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
} 