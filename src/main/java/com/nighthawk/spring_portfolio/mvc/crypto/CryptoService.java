package com.nighthawk.spring_portfolio.mvc.crypto;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

@Service
public class CryptoService {

    private final String apiKey = "1f492999-fe80-4dba-8a81-c6993505b5b7"; // Replace with your actual CoinMarketCap API key
    private final String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
    
    // Method to fetch current crypto data from CoinMarketCap
    public Crypto[] getCryptoData() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("X-CMC_PRO_API_KEY", apiKey);
            headers.set("Accept", "application/json");

            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);
            org.springframework.http.ResponseEntity<String> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, String.class);

            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(response.getBody());
            JSONArray dataArray = (JSONArray) jsonResponse.get("data");

            Crypto[] cryptos = new Crypto[dataArray.size()];
            for (int i = 0; i < dataArray.size(); i++) {
                JSONObject cryptoData = (JSONObject) dataArray.get(i);
                String name = (String) cryptoData.get("name");

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

    // New method to fetch historical data from CoinGecko
    public List<Double> getCryptoHistoricalData(String cryptoId, int days) {
        try {
            // CoinGecko URL for historical data
            String url = "https://api.coingecko.com/api/v3/coins/" + cryptoId + "/market_chart?vs_currency=usd&days=" + days;
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);

            // Parse the JSON response
            JSONParser parser = new JSONParser();
            JSONObject jsonResponse = (JSONObject) parser.parse(response);
            JSONArray pricesArray = (JSONArray) jsonResponse.get("prices");

            // Extract only the prices from the JSON response
            List<Double> prices = new ArrayList<>();
            for (Object priceObj : pricesArray) {
                JSONArray priceArray = (JSONArray) priceObj;
                Double price = ((Number) priceArray.get(1)).doubleValue();
                prices.add(price);
            }
            return prices;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
