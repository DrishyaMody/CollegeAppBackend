package com.nighthawk.spring_portfolio.mvc.crypto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crypto")
public class CryptoController {

    @Autowired
    private CryptoService cryptoService;

    @GetMapping("/live")
    public ResponseEntity<?> getLiveCryptoData() {
        Crypto[] cryptoData = cryptoService.getCryptoData();
        
        if (cryptoData == null || cryptoData.length == 0) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Failed to fetch cryptocurrency data");
        }
        
        return ResponseEntity.ok(cryptoData);  // Return the JSON array of cryptos
    }
}