package com.nighthawk.spring_portfolio.mvc.crypto;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@Service
public class CryptoService {

    private final String apiKey = "1f492999-fe80-4dba-8a81-c6993505b5b7";
    private final String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";

    public Crypto[] getCryptoData() {
        try {
            // Setup for API call
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-CMC_PRO_API_KEY", apiKey);
            headers.set("Accept", "application/json");

            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            // Parse JSON response
            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(response.getBody());
            JSONArray dataArray = (JSONArray) jsonResponse.get("data");

            Crypto[] cryptos = new Crypto[dataArray.size()];
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject cryptoData = (JSONObject) dataArray.get(i);
                String name = (String) cryptoData.get("name");

                // Safely access price and change percentage
                JSONObject quoteData = (JSONObject) cryptoData.get("quote");
                JSONObject usdData = (JSONObject) quoteData.get("USD");
                double price = usdData.containsKey("price") ? ((Number) usdData.get("price")).doubleValue() : 0.0;
                double changePercentage = usdData.containsKey("percent_change_24h") 
                                          ? ((Number) usdData.get("percent_change_24h")).doubleValue() 
                                          : 0.0;

                cryptos[i] = new Crypto(null, name, price, changePercentage);
            }
            return cryptos;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}