package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.Map;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/mining")
@Transactional
public class MiningController {
    @Autowired
    private PersonJpaRepository personRepository;
    
    @Autowired
    private MiningUserRepository miningUserRepository;
    
    @Autowired
    private MarketRepository marketRepository;

    @Autowired
    private GPURepository gpuRepository;

    private MiningUser getOrCreateMiningUser() {
        // Get authentication details
        var auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        
        System.out.println("DEBUG - Auth Details:");
        System.out.println("  Email: " + email);
        System.out.println("  Principal: " + auth.getPrincipal());
        System.out.println("  Authorities: " + auth.getAuthorities());

        if ("anonymousUser".equals(email)) {
            throw new RuntimeException("Not authenticated");
        }

        // Find person by email
        Person person = personRepository.findByEmail(email).orElseThrow(() -> {
            System.out.println("DEBUG - No person found for email: " + email);
            return new RuntimeException("User not found: " + email);
        });

        System.out.println("DEBUG - Found person: " + person.getEmail());

        // Find existing mining user
        Optional<MiningUser> existingUser = miningUserRepository.findByPerson(person);
        if (existingUser.isPresent()) {
            System.out.println("DEBUG - Found existing mining user");
            return existingUser.get();
        }

        // Create new mining user using constructor
        System.out.println("DEBUG - Creating new mining user");
        MiningUser newUser = new MiningUser(person);
        
        // Add starter GPU if exists
        GPU starterGpu = gpuRepository.findById(1L).orElse(null);
        if (starterGpu != null) {
            newUser.addGPU(starterGpu);
            System.out.println("DEBUG - Added starter GPU: " + starterGpu.getName());
        } else {
            System.out.println("DEBUG - No starter GPU found");
        }

        return miningUserRepository.save(newUser);
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getMiningStats() {
        try {
            MiningUser user = getOrCreateMiningUser();
            Map<String, Object> stats = new HashMap<>();
            
            // Add GPU information
            List<GPU> gpus = user.getGpus();
            if (!gpus.isEmpty()) {
                GPU currentGpu = gpus.get(0);
                stats.put("currentGpu", currentGpu.getName());
                stats.put("temperature", currentGpu.getTemp());
                stats.put("powerDraw", currentGpu.getPowerConsumption());
            } else {
                stats.put("currentGpu", "No GPU");
                stats.put("temperature", 0);
                stats.put("powerDraw", 0);
            }
            
            stats.put("btcBalance", user.getBtcBalance());
            stats.put("pendingBalance", user.getPendingBalance());
            stats.put("hashrate", user.getCurrentHashrate());
            stats.put("shares", user.getShares());
            stats.put("temperature", user.getAverageTemperature());
            stats.put("powerDraw", user.getPowerConsumption());
            
            // Calculate daily estimates
            double dailyRevenue = user.getCurrentHashrate() * 86400 / (1e12); // Example calculation
            double powerCost = user.getPowerConsumption() * 24 * 0.12 / 1000; // $0.12 per kWh
            
            stats.put("dailyRevenue", dailyRevenue);
            stats.put("powerCost", powerCost);
            stats.put("profit", dailyRevenue - powerCost);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/shop")
    public ResponseEntity<?> getGPUShop() {
        try {
            List<GPU> gpus = gpuRepository.findAll();
            return ResponseEntity.ok(gpus);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/buy/{gpuId}")
    public ResponseEntity<?> buyGPU(@PathVariable Long gpuId) {
        try {
            MiningUser user = getOrCreateMiningUser();
            GPU gpu = gpuRepository.findById(gpuId)
                .orElseThrow(() -> new RuntimeException("GPU not found"));
            
            user.addGPU(gpu);
            miningUserRepository.save(user);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Successfully purchased " + gpu.getName()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/toggle")
    public ResponseEntity<?> toggleMining() {
        try {
            MiningUser user = getOrCreateMiningUser();
            user.setMining(!user.isMining());
            miningUserRepository.save(user);
            
            return ResponseEntity.ok(Map.of(
                "isMining", user.isMining(),
                "message", user.isMining() ? "Mining started" : "Mining stopped"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/market")
    public ResponseEntity<?> getMarketData() {
        try {
            Map<String, Object> marketData = new HashMap<>();
            
            // Get market data from repository
            Market btcMarket = marketRepository.findBySymbol("BTC");
            Market ethMarket = marketRepository.findBySymbol("ETH");
            Market niceMarket = marketRepository.findBySymbol("NICE");
            Market f2pMarket = marketRepository.findBySymbol("F2P");

            // Structure the response to match frontend expectations
            marketData.put("nicehash", niceMarket != null ? niceMarket.getPrice() : 0.0);
            marketData.put("nicehashChange", niceMarket != null ? niceMarket.getChange24h() : 0.0);
            marketData.put("ethereum", ethMarket != null ? ethMarket.getPrice() : 0.0);
            marketData.put("ethereumChange", ethMarket != null ? ethMarket.getChange24h() : 0.0);
            marketData.put("f2pool", f2pMarket != null ? f2pMarket.getPrice() : 0.0);
            marketData.put("f2poolChange", f2pMarket != null ? f2pMarket.getChange24h() : 0.0);
            marketData.put("bitcoin", btcMarket != null ? btcMarket.getPrice() : 0.0);
            marketData.put("bitcoinChange", btcMarket != null ? btcMarket.getChange24h() : 0.0);

            return ResponseEntity.ok(marketData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/debug")
    public ResponseEntity<?> getDebugInfo() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            String email = auth.getName();
            
            Map<String, Object> debug = new HashMap<>();
            debug.put("currentEmail", email);
            debug.put("isAnonymous", "anonymousUser".equals(email));
            debug.put("principal", auth.getPrincipal().toString());
            debug.put("authorities", auth.getAuthorities().toString());
            
            Optional<Person> personOpt = personRepository.findByEmail(email);
            Person person = personOpt.orElse(null);
            debug.put("personFound", person != null);
            
            debug.put("allEmails", personRepository.findAll().stream()
                .map(Person::getEmail)
                .toList());
            
            if (person != null) {
                debug.put("personDetails", Map.of(
                    "id", person.getId(),
                    "email", person.getEmail()
                ));
                
                Optional<MiningUser> miningUser = miningUserRepository.findByPerson(person);
                debug.put("hasMiningUser", miningUser.isPresent());
                if (miningUser.isPresent()) {
                    debug.put("miningUserGpuCount", miningUser.get().getGpus().size());
                }
            }
            
            return ResponseEntity.ok(debug);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", e.getMessage(),
                    "type", e.getClass().getSimpleName()
                ));
        }
    }

    @PostMapping("/gpu/buy/{id}")
    public ResponseEntity<?> buyGpu(@PathVariable Long id) {
        try {
            MiningUser user = getOrCreateMiningUser();
            
            Optional<GPU> gpuOptional = gpuRepository.findById(id);
            if (!gpuOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "GPU not found"));
            }
            
            GPU gpu = gpuOptional.get();
            
            // Check if user can afford it (if it's not free)
            if (gpu.getPrice() > 0) {
                Market btcMarket = marketRepository.findBySymbol("BTC");
                double btcPrice = (btcMarket != null) ? btcMarket.getPrice() : 40000.0;
                
                double gpuPriceInBTC = gpu.getPrice() / btcPrice;
                if (user.getBtcBalance() < gpuPriceInBTC) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("success", false, "message", "Insufficient funds"));
                }
                
                user.setBtcBalance(user.getBtcBalance() - gpuPriceInBTC);
            }
            
            user.getGpus().add(gpu);
            miningUserRepository.save(user);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Successfully purchased " + gpu.getName()
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
