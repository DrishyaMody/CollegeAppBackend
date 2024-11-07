package com.nighthawk.spring_portfolio.mvc.cryptoMining;

import com.nighthawk.spring_portfolio.mvc.person.Person;
import com.nighthawk.spring_portfolio.mvc.person.PersonJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.*;
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
            System.out.println("\nDEBUG - Getting mining stats");
            MiningUser user = getOrCreateMiningUser();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("btcBalance", user.getBtcBalance());
            stats.put("pendingBalance", user.getPendingBalance());
            stats.put("hashrate", user.getCurrentHashrate());
            stats.put("shares", user.getShares());
            stats.put("isMining", user.isMining());
            stats.put("currentPool", user.getCurrentPool());
            
            List<Map<String, Object>> gpus = new ArrayList<>();
            for (GPU gpu : user.getGpus()) {
                Map<String, Object> gpuInfo = new HashMap<>();
                gpuInfo.put("id", gpu.getId());
                gpuInfo.put("name", gpu.getName());
                gpuInfo.put("hashrate", gpu.getHashRate());
                gpuInfo.put("power", gpu.getPowerConsumption());
                gpus.add(gpuInfo);
            }
            stats.put("gpus", gpus);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "error", e.getMessage(),
                    "type", e.getClass().getSimpleName(),
                    "trace", Arrays.toString(e.getStackTrace())
                ));
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
            List<Market> markets = marketRepository.findAll();
            Map<String, Object> marketData = new HashMap<>();
            
            for (Market market : markets) {
                Map<String, Object> data = new HashMap<>();
                data.put("price", market.getPrice());
                data.put("change24h", market.getChange24h());
                marketData.put(market.getSymbol().toLowerCase(), data);
            }
            
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
}
