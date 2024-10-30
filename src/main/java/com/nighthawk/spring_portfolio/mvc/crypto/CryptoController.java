package com.nighthawk.spring_portfolio.mvc.crypto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/crypto")
public class CryptoController {

    @Autowired
    private CryptoService cryptoService;

    // Existing endpoint to get live cryptocurrency data from CoinMarketCap
    @GetMapping("/live")
    public ResponseEntity<?> getLiveCryptoData() {
        Crypto[] cryptoData = cryptoService.getCryptoData();
        
        if (cryptoData == null || cryptoData.length == 0) {
            return ResponseEntity.status(500).body("Failed to fetch cryptocurrency data");
        }
        
        return ResponseEntity.ok(cryptoData);
    }

    // New endpoint to get historical trend data from CoinGecko
    @GetMapping("/trend")
    public ResponseEntity<?> getCryptoTrend(@RequestParam String cryptoId, @RequestParam int days) {
        List<Double> trendData = cryptoService.getCryptoHistoricalData(cryptoId, days);

        if (trendData == null || trendData.isEmpty()) {
            return ResponseEntity.status(500).body("Failed to fetch trend data");
        }
        
        return ResponseEntity.ok(trendData);
    }
}
